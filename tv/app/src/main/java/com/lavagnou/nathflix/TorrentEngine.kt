package com.lavagnou.nathflix

import android.content.Context
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import org.libtorrent4j.AnnounceEntry
import org.libtorrent4j.Priority
import org.libtorrent4j.SessionHandle
import org.libtorrent4j.SessionManager
import org.libtorrent4j.SessionParams
import org.libtorrent4j.SettingsPack
import org.libtorrent4j.Sha1Hash
import org.libtorrent4j.TorrentFlags
import org.libtorrent4j.TorrentHandle
import org.libtorrent4j.TorrentInfo
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.concurrent.Executors

/**
 * Torrents played on the TV itself, with no streaming server anywhere: libtorrent fetches the file
 * in order (the part being watched first), and a small web server on 127.0.0.1 hands it to the
 * player as if it were any other link, waiting for pieces that haven't arrived yet.
 *
 * One torrent at a time; its download is deleted when playback stops (and at the next start, in
 * case the app was closed mid-way).
 */
object TorrentEngine {
    private const val TAG = "nathflix-torrent"
    private const val METADATA_TIMEOUT_S = 60
    private const val READ_AHEAD_PIECES = 12
    private const val PIECE_WAIT_MS = 90_000L

    // Well-known open trackers, added to every torrent alongside its own (as Stremio's server does):
    // many streams list few or none, and leave finding peers to the DHT
    private val OPEN_TRACKERS = listOf(
        "udp://tracker.opentrackr.org:1337/announce",
        "udp://open.stealth.si:80/announce",
        "udp://tracker.torrent.eu.org:451/announce",
        "udp://exodus.desync.com:6969/announce",
        "udp://open.demonii.com:1337/announce",
        "udp://explodie.org:6969/announce",
    )

    private val VIDEO = Regex("""\.(mkv|mp4|m4v|avi|mov|webm|ts|m2ts|wmv|flv)$""", RegexOption.IGNORE_CASE)
    private val PEERS_STATUS = Regex("""^\d+ \S+ · [\d.,]+ \S+/s$""")

    private var session: SessionManager? = null
    private var server: Server? = null
    private lateinit var dir: File
    // a thread per torrent being opened: one still waiting for an old torrent's details (up to a
    // minute) mustn't hold up the next
    private val worker = Executors.newCachedThreadPool()

    /** The torrent being played; [key]: its hash and the file asked for ("hash/3", or "hash/-1") */
    private class Active(val key: String, val hash: String, val handle: TorrentHandle, val info: TorrentInfo, val file: Int, val path: File) {
        val offset: Long = info.files().fileOffset(file)
        val size: Long = info.files().fileSize(file)
        val pieceLength: Int = info.pieceLength()
        @Volatile var closed = false
    }

    @Volatile private var active: Active? = null
    @Volatile private var opening: String? = null
    @Volatile private var failure: Int? = null // a string resource: why the torrent failed

    /** Set up once: the download folder (emptied) and the session */
    @Synchronized
    private fun ensure(context: Context) {
        if (session != null) {
            if (session!!.isPaused) session!!.resume()
            return
        }
        dir = File(context.cacheDir, "torrents")
        // what an earlier run left (it can be gigabytes) is deleted in the background: this runs on
        // the page's thread, which would freeze meanwhile
        if (dir.exists() && !dir.renameTo(File(context.cacheDir, "torrents-old-${System.currentTimeMillis()}"))) dir.deleteRecursively()
        worker.execute { context.cacheDir.listFiles { f -> f.name.startsWith("torrents-old-") }?.forEach { it.deleteRecursively() } }
        dir.mkdirs()
        val settings = SettingsPack()
            .listenInterfaces("0.0.0.0:6881,[::]:6881")
            .connectionsLimit(120)
            .activeDownloads(2)
        val s = SessionManager(false)
        s.start(SessionParams(settings))
        session = s
        server = Server().also { it.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true) }
        Log.i(TAG, "engine up, web server on ${server!!.listeningPort}")
    }

    /**
     * Starts fetching [infoHash] (file [fileIdx], or the biggest video in it when -1) and returns the
     * address the player reads it from. Getting the torrent's details from peers happens in the
     * background: the address answers once they're in.
     */
    fun open(context: Context, infoHash: String, fileIdx: Int, trackers: List<String>, nameHint: String): String {
        ensure(context)
        val hash = infoHash.lowercase()
        // (another file of the same torrent, a season pack's next episode, is another download)
        val key = "$hash/$fileIdx"
        synchronized(this) {
            if (active?.key != key && opening != key) {
                stop()
                opening = key
                failure = null
                worker.execute { start(key, hash, fileIdx, trackers, nameHint) }
            }
        }
        return "http://127.0.0.1:${server!!.listeningPort}/t/$key"
    }

    private fun start(key: String, hash: String, fileIdx: Int, trackers: List<String>, nameHint: String) {
        val s = session ?: return
        try {
            val allTrackers = (trackers.take(30) + OPEN_TRACKERS).distinct()
            val magnet = buildString {
                append("magnet:?xt=urn:btih:").append(hash)
                for (t in allTrackers) append("&tr=").append(java.net.URLEncoder.encode(t, "UTF-8"))
            }
            // the torrent's file list and pieces, from peers (DHT and the stream's trackers)
            val bytes = s.fetchMagnet(magnet, METADATA_TIMEOUT_S, dir)
            if (opening != key) return
            if (bytes == null) {
                failure = R.string.torrent_no_peers
                return
            }
            val info = TorrentInfo.bdecode(bytes)
            val files = info.files()
            val pick = if (fileIdx in 0 until files.numFiles()) fileIdx else chooseFile(info, nameHint)
            // only the file being watched is downloaded
            val priorities = Array(files.numFiles()) { if (it == pick) Priority.DEFAULT else Priority.IGNORE }
            s.download(info, dir, null, priorities, null, TorrentFlags.SEQUENTIAL_DOWNLOAD)
            val handle = s.find(Sha1Hash.parseHex(hash)) ?: run { failure = R.string.torrent_cant_start; return }
            handle.setFlags(TorrentFlags.SEQUENTIAL_DOWNLOAD)
            // the torrent's details don't carry the trackers: give them back, and ask them for peers now
            for (t in allTrackers) handle.addTracker(AnnounceEntry(t))
            handle.forceReannounce()
            handle.forceDHTAnnounce()
            val a = Active(key, hash, handle, info, pick, File(dir, files.filePath(pick)))
            // the start and the very end first: players read a file's index (MP4's moov, MKV's cues)
            // from either end before playing
            val first = a.offset / a.pieceLength
            val last = (a.offset + a.size - 1) / a.pieceLength
            for (p in first..minOf(last, first + READ_AHEAD_PIECES)) handle.setPieceDeadline(p.toInt(), 300 + (p - first).toInt() * 150)
            for (p in maxOf(first, last - 1)..last) handle.setPieceDeadline(p.toInt(), 600)
            // (closed meanwhile, or another torrent asked for: this one goes)
            synchronized(this) {
                if (opening != key) { s.remove(handle, SessionHandle.DELETE_FILES); return }
                active = a
            }
            Log.i(TAG, "playing ${files.filePath(pick)} (${a.size / 1048576} MB)")
        } catch (e: Exception) {
            Log.w(TAG, "couldn't open $hash", e)
            if (opening == key) failure = R.string.torrent_cant_open
        }
    }

    // Stremio sends the file to play; when it doesn't, the biggest video file is the film/episode
    private fun chooseFile(info: TorrentInfo, nameHint: String): Int {
        val files = info.files()
        val videos = (0 until files.numFiles()).filter { VIDEO.containsMatchIn(files.fileName(it)) }
        val pool = videos.ifEmpty { (0 until files.numFiles()).toList() }
        val hinted = if (nameHint.isNotBlank()) pool.filter { files.fileName(it).contains(nameHint, ignoreCase = true) } else emptyList()
        return (hinted.ifEmpty { pool }).maxByOrNull { files.fileSize(it) } ?: 0
    }

    /**
     * Stops the download and deletes it. With [key] (what a player reads: "hash/file"), only when
     * that's still the torrent playing: a newer film's torrent is left alone.
     */
    @Synchronized
    fun close(key: String? = null) {
        if (key != null && active?.key != key && opening != key) return
        stop()
        // nothing playing: no DHT, no peers, no open port until the next torrent
        try { session?.takeIf { !it.isPaused }?.pause() } catch (_: Exception) { }
    }

    @Synchronized
    private fun stop() {
        opening = null
        val a = active
        active = null
        if (a != null) {
            a.closed = true
            try { session?.remove(a.handle, SessionHandle.DELETE_FILES) } catch (_: Exception) { }
            Log.i(TAG, "closed ${a.hash}")
        }
    }

    /** For the player's status line: "Finding peers…", "12 peers · 2.4 MB/s", or why it failed */
    fun status(context: Context): String? {
        failure?.let { return Lang.text(context, it) }
        val a = active ?: return if (opening != null) Lang.text(context, R.string.torrent_finding_peers) else null
        return try {
            val st = a.handle.status()
            val rate = "%.1f".format(st.downloadPayloadRate() / 1048576.0)
            if (st.numPeers() == 1) Lang.text(context, R.string.torrent_one_peer, rate)
            else Lang.text(context, R.string.torrent_peers, st.numPeers(), rate)
        } catch (_: Exception) { null }
    }

    /** Whether the player's status line is the torrent's ("Finding peers…", "12 peers…") */
    fun isStatus(context: Context, text: CharSequence): Boolean {
        val finding = Lang.text(context, R.string.torrent_finding_peers)
        return text == finding || PEERS_STATUS.matches(text)
    }

    fun failed(context: Context): String? = failure?.let { Lang.text(context, it) }

    // ---- Reading the file as it arrives ----

    /** Waits until [piece] is downloaded (asking for it, and the next few, urgently) */
    private fun await(a: Active, piece: Int): Boolean {
        if (a.handle.havePiece(piece)) return true
        val lastPiece = ((a.offset + a.size - 1) / a.pieceLength).toInt()
        for (p in piece..minOf(lastPiece, piece + READ_AHEAD_PIECES)) a.handle.setPieceDeadline(p, (p - piece) * 200)
        val until = System.currentTimeMillis() + PIECE_WAIT_MS
        while (!a.closed && System.currentTimeMillis() < until) {
            if (a.handle.havePiece(piece)) return true
            Thread.sleep(60)
        }
        return false
    }

    /** The file from [start], handed over piece by piece as each one arrives */
    private class PieceStream(private val a: Active, start: Long, private val end: Long) : InputStream() {
        private var pos = start
        private var raf: RandomAccessFile? = null

        private val one = ByteArray(1)

        override fun read(): Int = if (read(one, 0, 1) == 1) one[0].toInt() and 0xff else -1

        override fun read(b: ByteArray, off: Int, len: Int): Int {
            if (pos > end || a.closed) return -1
            val piece = ((a.offset + pos) / a.pieceLength).toInt()
            if (!await(a, piece)) return -1
            // as far as the end of this piece (the next may not be here yet)
            val pieceEnd = (piece.toLong() + 1) * a.pieceLength - a.offset
            val n = minOf(len.toLong(), end - pos + 1, pieceEnd - pos).toInt()
            val file = raf ?: RandomAccessFile(a.path, "r").also { raf = it }
            file.seek(pos)
            val got = file.read(b, off, n)
            if (got > 0) pos += got
            return got
        }

        override fun close() {
            try { raf?.close() } catch (_: Exception) { }
        }
    }

    private class Server : NanoHTTPD("127.0.0.1", 0) {
        override fun serve(session: IHTTPSession): Response {
            val key = session.uri.removePrefix("/t/").lowercase()
            // (addresses from before the file was part of them: just the hash)
            fun ours(a: Active?) = a != null && (a.key == key || a.hash == key)
            fun waiting() = opening == key || opening?.substringBefore('/') == key
            // the torrent's details can take a while to arrive from peers: the player's first request waits
            val until = System.currentTimeMillis() + (METADATA_TIMEOUT_S + 5) * 1000L
            var a = active
            while (!ours(a) && failure == null && waiting() && System.currentTimeMillis() < until) {
                Thread.sleep(100)
                a = active
            }
            if (a == null || !ours(a)) return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, if (failure != null) "failed" else "not playing")
            // the file is created as soon as the download starts; wait for it to exist
            for (i in 0 until 50) { if (a.path.exists()) break; Thread.sleep(100) }

            val size = a.size
            var start = 0L
            var end = size - 1
            val range = session.headers["range"]
            val ranged = range != null && range.startsWith("bytes=")
            if (ranged) {
                val parts = range!!.removePrefix("bytes=").substringBefore(',').split("-")
                val from = parts[0].trim().toLongOrNull()
                val to = parts.getOrNull(1)?.trim()?.toLongOrNull()
                if (from == null && to != null) {
                    // "bytes=-500": the last 500 bytes
                    start = (size - to).coerceAtLeast(0L)
                } else {
                    start = from ?: 0L
                    end = to?.coerceAtMost(size - 1) ?: (size - 1)
                }
                if (start >= size) {
                    return newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, "").apply { addHeader("Content-Range", "bytes */$size") }
                }
            }
            val length = end - start + 1
            val response = newFixedLengthResponse(if (ranged) Response.Status.PARTIAL_CONTENT else Response.Status.OK, mime(a.path.name), PieceStream(a, start, end), length)
            response.addHeader("Accept-Ranges", "bytes")
            if (ranged) response.addHeader("Content-Range", "bytes $start-$end/$size")
            return response
        }

        private fun mime(name: String): String = when (name.substringAfterLast('.').lowercase()) {
            "mp4", "m4v" -> "video/mp4"
            "webm" -> "video/webm"
            "avi" -> "video/x-msvideo"
            "ts", "m2ts" -> "video/mp2t"
            "mov" -> "video/quicktime"
            else -> "video/x-matroska"
        }
    }
}
