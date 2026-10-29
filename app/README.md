# Nathflix desktop app

The Nathflix app: [Stremio Enhanced](https://github.com/REVENGE977/stremio-enhanced) 1.2.0 by REVENGE977 (MIT), renamed to Nathflix, with the Nathflix plugin from the repository root built in. For what it does and how to install it, see the [main README](../README.md#install).

What's different from Stremio Enhanced:

- It's called Nathflix, with its own icon, and keeps its data in its own folder (`%APPDATA%\nathflix`, `~/Library/Application Support/nathflix`, `~/.config/nathflix`).

- The Nathflix plugin is built in (`src/core/BuiltIn.ts`): it's put in the plugins folder and switched on at first launch, and only ever replaces an older copy, so the plugin's own updates keep working between app releases.

- First launch offers to copy Stremio Enhanced's sign-in, library, settings, plugins, themes and streaming server over (`NATHFLIX_IMPORT=copy|fresh` answers that for automated tests).

- Stremio's streaming server (`server.js`) is downloaded and kept current automatically.

- Discord Rich Presence uses Nathflix's own Discord application, with a "Get Nathflix" button. Until it has one (`DISCORD.CLIENT_ID` in `src/constants/index.ts` is empty), the status is off and its switch is hidden.

- Updates are checked against this repository's releases, and the app's version always matches the plugin's (`node tools/bundle-theme.mjs` keeps `version` and `package.json` in step).

## Building

You need Node.js 22 or newer.

```sh
cd app
npm ci
npm run dev                  # run from source (add --devtools for DevTools)
npm run build:linux:x64      # or build:win:x64, build:mac:arm64, …
```

Every build copies `../nathflix.plugin.js` in as the built-in plugin. Publishing a release on GitHub builds every platform and attaches the files to it (`.github/workflows/app.yml`).
