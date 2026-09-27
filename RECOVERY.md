# Run Hero Run: recovery notes

App Store: https://apps.apple.com/us/app/run-hero-run/id1058214268 (libGDX, iOS via RoboVM)

## What this repo has
- **Game code:** the complete `core` module (`Compress-core/src`). The last commit ("Back up", 2015-11-11) matches the shipped build.
- Deleted classes (`Boss`, `GestureHandler`, `Element`) are still in git history: `git show 35ea813:Compress-core/src/com/crane/GameObjects/Boss.java`.

## What's missing
1. **Assets** (never committed): `texture.png`, `libGdx.png`, `Trash3.fnt` (+ its page png), and `sound/*.m4a` (Death Sound, Hit Sound, Jump Sound, Knight Attack, theme, Wizard Attack).
2. **Launcher modules** (`ios`/`android`/`desktop`) and the root Gradle files. These were never pushed. The iOS launcher implemented `IActivityRequestHandler` (banner ads and the rate prompt).

## Getting the assets back from the App Store build
Asset files inside an IPA are **not** encrypted. Only the executable is protected by FairPlay.
1. On a Mac, install **Apple Configurator** and sign in with the Apple ID that owns or once downloaded the app.
2. Choose Add → Apps, then select Run Hero Run. Configurator caches the `.ipa` under
   `~/Library/Group Containers/K36BKF7T3D.group.com.apple.configurator/Library/Caches/Assets/TemporaryItems/MobileApps/`.
   Copy it out before closing the "already exists" dialog.
   (Other routes: an old iTunes backup at `~/Music/iTunes/iTunes Media/Mobile Applications/`, or the IPA you uploaded in Xcode's Organizer / Application Loader archives, `~/Library/Developer/Xcode/Archives`.)
3. Run `scripts/extract-ipa-assets.sh RunHeroRun.ipa`. Files go into `./assets`, the libGDX assets dir. The script reports any expected file it can't find.

## Code that can't be recovered from the binary
RoboVM compiled the Java ahead of time to native ARM, and the binary is FairPlay-encrypted, so the launcher code can't be decompiled back to Java. Regenerate the launchers with libGDX `gdx-liftoff`, using package `com.crane.compress` and main class `Compress`. Then point them at `./assets` and re-implement `IActivityRequestHandler`.
