import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// The TV app shares Nathflix's version number: it's read from the plugin at the repository root
val pluginFile = rootProject.file("../nathflix.plugin.js")
val nathflixVersion = Regex("""@version\s+([\d.]+)""").find(pluginFile.readText())!!.groupValues[1]
val versionParts = nathflixVersion.split(".").map { it.toInt() }

// Release signing: tv/keystore.properties (not in git) or the NATHFLIX_TV_KEYSTORE* environment variables in CI
val keystoreProps = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun signingValue(key: String, env: String): String? = keystoreProps.getProperty(key) ?: System.getenv(env)

android {
    namespace = "com.lavagnou.nathflix"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lavagnou.nathflix"
        minSdk = 24
        targetSdk = 35
        versionName = nathflixVersion
        versionCode = versionParts[0] * 10000 + versionParts[1] * 100 + versionParts[2]
    }

    signingConfigs {
        // Test builds (Nathflix Dev) are always signed with the same key, kept in the repository, so a
        // new one installs over the last without losing the sign-in. It signs nothing that's released
        create("dev") {
            storeFile = file("dev.keystore")
            storePassword = "nathflix-dev"
            keyAlias = "nathflix-dev"
            keyPassword = "nathflix-dev"
        }
        create("release") {
            val store = signingValue("storeFile", "NATHFLIX_TV_KEYSTORE")
            if (store != null) {
                storeFile = rootProject.file(store)
                storePassword = signingValue("storePassword", "NATHFLIX_TV_KEYSTORE_PASSWORD")
                keyAlias = signingValue("keyAlias", "NATHFLIX_TV_KEY_ALIAS")
                keyPassword = signingValue("keyPassword", "NATHFLIX_TV_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            // "Nathflix Dev": installs next to the real app (named in src/debug/res)
            applicationIdSuffix = ".dev"
            signingConfig = signingConfigs.getByName("dev")
            // the emulator is x86
            ndk { abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64") }
        }
        release {
            // TVs and sticks are ARM: only those copies of the torrent engine (about 14 MB each)
            ndk { abiFilters += listOf("armeabi-v7a", "arm64-v8a") }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (signingValue("storeFile", "NATHFLIX_TV_KEYSTORE") != null) signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    // The torrent engine's native code compressed in the APK: a smaller download for updates
    packaging {
        jniLibs { useLegacyPackaging = true }
    }

    // The built-in Nathflix plugin is the one at the repository root, copied in at build time
    sourceSets["main"].assets.srcDir(layout.buildDirectory.dir("generated/nathflixAssets"))
}

val copyPlugin by tasks.registering(Copy::class) {
    from(pluginFile)
    into(layout.buildDirectory.dir("generated/nathflixAssets"))
}
tasks.named("preBuild") { dependsOn(copyPlugin) }

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.webkit:webkit:1.12.1")
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.5.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")
    implementation("androidx.media3:media3-datasource-okhttp:1.5.1")
    // phones and tablets: playing in the background, with controls in the notification
    implementation("androidx.media3:media3-session:1.5.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Torrents played on the TV itself (no streaming server needed): libtorrent, and a small local
    // web server that hands the downloading file to the player
    implementation("org.libtorrent4j:libtorrent4j:2.1.0-39")
    implementation("org.libtorrent4j:libtorrent4j-android-arm:2.1.0-39")
    implementation("org.libtorrent4j:libtorrent4j-android-arm64:2.1.0-39")
    implementation("org.libtorrent4j:libtorrent4j-android-x86:2.1.0-39")
    implementation("org.libtorrent4j:libtorrent4j-android-x86_64:2.1.0-39")
    implementation("org.nanohttpd:nanohttpd:2.3.1")
}
