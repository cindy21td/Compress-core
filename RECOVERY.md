# Run Hero Run: recovery notes

App Store: https://apps.apple.com/us/app/run-hero-run/id1058214268 (libGDX, iOS via RoboVM)

## What this repo has
- **Game code:** the complete `core` module (`Compress-core/src`). The last commit ("Back up", 2015-11-11) matches the shipped build.
- Deleted classes (`Boss`, `GestureHandler`, `Element`) are still in git history: `git show 35ea813:Compress-core/src/com/crane/GameObjects/Boss.java`.

## What's missing
1. **Assets** (never committed): `texture.png`, `libGdx.png`, `Trash3.fnt` (+ its page png), and `sound/*.m4a` (Death Sound, Hit Sound, Jump Sound, Knight Attack, theme, Wizard Attack).
2. **Launcher modules** (`ios`/`android`/`desktop`) and the root Gradle files. These were never pushed. The iOS launcher implemented `IActivityRequestHandler` (banner ads and the rate prompt).

## Assets recovered from Google Drive
The app is delisted, so the App Store route below no longer works. A May 2016 backup is in Google Drive under `Compress - Assets/` (subfolders `Reformed/img`, `Sound` and `Font`).

Already in `assets/`:
- `texture.png` (1650x1013, the sprite sheet `AssetLoader` slices)
- `Trash3.png` (font page)
- `sound/theme.m4a` (Drive name: `Run Theme.m4a`)
- `sound/Death Sound.m4a`

Still to copy into `assets/` (download each from Drive):
| File | Drive link |
| --- | --- |
| `Trash3.fnt` | https://drive.google.com/file/d/1IkS9MUWlng_36D1eVRDbJ8S1no9vewLR/view |
| `libGdx.png` | https://drive.google.com/file/d/1PfaUL4ecNfRim_7vuweXvbtxz4jG9zP5/view |
| `sound/Hit Sound.m4a` | https://drive.google.com/file/d/1IBzKy5YQdMX5_VH16NoRfxEIukpIJK0g/view |
| `sound/Jump Sound.m4a` | https://drive.google.com/file/d/1IFIywE0Jq8y9jCVgEmNO1GL19ZLr9_Ld/view |
| `sound/Knight Attack.m4a` | https://drive.google.com/file/d/1I3LWXvpa9KimHFET4Ub4XojfW2Ihz8Xk/view |
| `sound/Wizard Attack.m4a` | https://drive.google.com/file/d/1ICG-zlTKlTm9-q916NFZ8_S1GoCa6ZT9/view |

The same folders also hold source art that the game doesn't load: the separate background layers, the menu and title art, `Scoreboard.png`, `Boss Theme.m4a` and the Pixelmator project files.

Four regions in `AssetLoader` (`ratePrompt1/2`, `stillOne/Two`) extend 1–5px past the sheet's edge. libGDX tolerates this.

## Getting the assets back from the App Store build (only if the app is relisted)
Asset files inside an IPA are **not** encrypted. Only the executable is protected by FairPlay.
1. On a Mac, install **Apple Configurator** and sign in with the Apple ID that owns or once downloaded the app.
2. Choose Add → Apps, then select Run Hero Run. Configurator caches the `.ipa` under
   `~/Library/Group Containers/K36BKF7T3D.group.com.apple.configurator/Library/Caches/Assets/TemporaryItems/MobileApps/`.
   Copy it out before closing the "already exists" dialog.
   (Other routes: an old iTunes backup at `~/Music/iTunes/iTunes Media/Mobile Applications/`, or the IPA you uploaded in Xcode's Organizer / Application Loader archives, `~/Library/Developer/Xcode/Archives`.)
3. Run `scripts/extract-ipa-assets.sh RunHeroRun.ipa`. Files go into `./assets`, the libGDX assets dir. The script reports any expected file it can't find.

## Code that can't be recovered from the binary
RoboVM compiled the Java ahead of time to native ARM, and the binary is FairPlay-encrypted, so the launcher code can't be decompiled back to Java. Regenerate the launchers with libGDX `gdx-liftoff`, using package `com.crane.compress` and main class `Compress`. Then point them at `./assets` and re-implement `IActivityRequestHandler`.
