# Run Hero Run: release plan for the 2026 update (written 2026-11-25)

**Why:** Apple sent an App Store Improvement Notice. The app is removed in **February 2027** unless an update is submitted and approved first. The live build is libGDX/RoboVM 1.1 from 2015 (min iOS 8.0). This update replaces it with the Godot port on this branch.

**Target:** the build is in App Store Connect, attached to version 1.2, with the listing done, **ready for Crane to submit by Fri 2026-12-19**. The hard limit is Mon 2027-01-05, which leaves January for one or two rejection rounds before the February removal.

## 1. Where things stand (checked 2026-11-25)

### The port (branch `claude/run-hero-run-recovery-24o3ii`, Godot 4.4)
- `tests/playtest.tscn` **passes headless** on Godot 4.4.stable (33 checks). It covers the menu, sound toggle, tutorial, jump, pause, a 46 s demo run through the stages, the Pig, a power-up, two boss fights, death with a shield, the score screen, replay and pause → Menu. It also passes windowed, with 16 screenshots.
- **It also passes on Godot 4.7.2** (scratch copy, fresh import, 0 import errors). That is the version Dim Sum Sort shipped with.
- The only noise is "2 resources still in use" / "4 ObjectDB instances leaked" at exit. This is cosmetic.
- The rules, timings and hitboxes are ported from the Java core. The boss, combos, power-ups, Pig, parallax stages, effects and new UI are all in place.

### Broken or missing for an iOS release
| # | Problem | Why it matters |
|---|---|---|
| 1 | **3:2 letterbox** (`stretch/aspect="keep"`, 204×136 viewport). A 19.5:9 iPhone shows black bars of ~25 % of the width on both sides | Looks broken on every current iPhone; craft score; reviewer impression (Apple sent the notice for being outdated) |
| 2 | No safe-area handling; the HUD would sit under the Dynamic Island in landscape | Clipped HUD/pause |
| 3 | No iOS export preset, icon, launch screen or `ITSAppUsesNonExemptEncryption` | Can't build |
| 4 | No pause when the app goes to the background (`NOTIFICATION_APPLICATION_PAUSED` unhandled) | A run dies behind a phone call; reviewers notice |
| 5 | No haptics (`Input.vibrate_handheld` unused) | Principle 13 "felt" reaction on a stomp or a death |
| 6 | First run is a 4-rule HOW TO PLAY text card before the first tap | DESIGN.md principle 13 + banned "tutorial before task" |
| 7 | At-rest HUD is ~5 elements and covers the sky (score card with 2 rows, boss bar + name, pause, power-up chips) | Principle 12 |
| 8 | Game-over screen: SCORE banner, TOTAL, HS, stats line, HIGH SCORE stamp, medal, replay, Menu = 8 | Over the budget of 6 |
| 9 | The Pig art is a stand-in drawn by `tools/make_pig.py` | It looks fine. The original `Pig Sprite.png` is in Drive (optional) |
| 10 | Jump, Hit, Knight Attack and Wizard Attack sounds are synthesized stand-ins (`tools/make_sounds.py`) | They work. The originals are in Drive (optional; links in RECOVERY.md) |

`Trash3.fnt` and `libGdx.png` are **not needed** by the port (Fredoka One replaced the font, and the libGDX splash is gone).

### App Store Connect (read-only, 2026-11-25)
- App **1058214268** "Run Hero Run!", bundle **com.crane.compress** (matches `com.crane.compress` in RECOVERY.md), SKU com.crane.compress, en-US, available in new territories.
- Versions: **1.1 READY_FOR_SALE** (2015-11-25, build 1). 1.0 was replaced. No editable version exists yet: **create 1.2**.
- Builds: two, both "1", min OS 8.0, `usesNonExemptEncryption=false`.
- Screenshot sets on 1.1: APP_IPHONE_35, _40, _47 and _55 (5 each). **No APP_IPHONE_67 (6.9") set**, which ASC requires now. No iPad sets: the app is iPhone-only, so keep `targeted_device_family=0` and no iPad 13" set is needed.
- Age rating: 9+ (cartoon violence infrequent/mild). The 2025 questionnaire fields are **unanswered** (advertising, loot box, messaging, parental controls, age assurance, UGC, guns, health topics, social media). Crane answers these. Ads are gone, so advertising = none.
- Primary category **GAMES with no subcategory**, no secondary. Set Games > Action + Games > Arcade.
- Content rights: "uses third-party content" (Fredoka One is OFL; fine to keep).
- **Privacy policy URL: empty** (required). **Support URL: http://www.mcindyprasetio.com/** (likely dead). Marketing: none.
- Review detail: `demoAccountRequired: true` with no account. Set it to false. No notes.
- No IAPs. Free.
- App Privacy labels: probably never answered (the app predates them). Crane answers them: Data Not Collected.
- Description and keywords are from 2015 ("Action, Run, Coin, Adventure, Fun, Challenging"). What's New: "Added rating button functionality."

## 2. Design pass (game-adapted DESIGN.md 4/11/12/13)

### Inspiration study (well-rated one-tap / endless runners)
| Game | Rating | What we take |
|---|---|---|
| **Run!!!** (Kongregate) | 4.7, 44k | One-tap side runner. The first obstacle comes slowly and the run *is* the tutorial; the HUD is one number top-centre |
| **Sonic Dash** (SEGA) | 4.7, 418k | The first-run gestures are taught at the exact obstacle, with one glyph and a slow-down, then never again. The score is a single top-centre counter |
| **Subway Surfers** (Sybo) | 4.6, 3.9M | The title sits over the live scene and the first tap starts running at once (no menu hub). Power-up timers sit as small bars at the side and are only visible while active |
| (also) **Alto's Odyssey** | 4.7+ | Calm HUD: distance only while riding; everything else appears on pause/death |

What they have in common: the title sits over the live game, one tap starts, teaching happens at the obstacle (a glyph, slow-mo), the HUD is one top-centre number at rest, and transient things appear only while active.

### Current vs. proposed (mocks in `docs/release-2026-11/`)
- `current.jpg`: the menu letterboxed on a 6.9" phone, the 4-rule card, the HUD at rest.
- `dirA.jpg` **A, teach in the run:** the first tap from the title starts the run. A slowed knight brings a ring cue on the hero; a stomp gives a freeze, +1 and the drawn arc; the first fireball gives the double-jump cue. HUD = score, metres, pause.
- `dirB.jpg` **B, the title is the start line:** the title over the live stage with BEST as a badge, a practice beat that waits for the first jump, and later rules shown as one pill the first time each happens.

**Blind rubric (two sonnet reviewers, DESIGN.md principles + banned patterns only):**
| | Learnable | Calm HUD | Show | Character | Overall |
|---|---|---|---|---|---|
| Current | 2 / 2 | 3 / 2 | 1 / 2 | 4 / 3 | **2 / 2** |
| A | 4 / 4 | 5 / 4 | 4 / 4 | 4 / 4 | **4 / 4** |
| B | 5 / 3 | 5 / 4 | 4 / 3 | 5 / 4 | **5 / 3** |

**Pick (delegated, 2026-11-25): the B shell with A's teaching.** A averages 4.0 and B 4.0, so the two are combined along the lines both reviewers suggested:
1. **Title over the live stage** (B1). There is no separate menu screen: the hero idles on the stage 1 ground with the title art and BEST badge above, and the sound toggle is the only other control. Tapping anywhere starts the run. This is the game's now screen (principle 4).
2. **Teaching in the run, no stall** (A1 to A3). The first knight comes at 0.5× speed with a pulsing ring on the hero and no sentence; if the player doesn't tap, the knight walks past harmlessly (first run only). The first stomp gets a 120 ms freeze, a "+1" on the knight, the arc drawn as a dotted trail, a haptic and a sound. The first fireball brings a ring with a ghost double-arc. After the first stomp and the first double jump, cues never show again (saved in `user://compress.cfg`).
3. **Later rules as one mark, the first time only.** These cover the combo (the "×2" pill sits on the hero, not a sentence), orbs (the orb pulses and its icon flies into the timer slot) and the Summoner (an arrow glyph on the escaping Summoner, then the boss bar enters). Only one transient overlay shows at a time, and RUSH!/banners never overlap the hero's arc.
4. **Calm HUD.** At rest: **score** (large, top centre, outlined, no card), **metres** (small under it), **pause** (top trailing, inside the safe area) = 3. While active: power-up timer rings (top leading), the boss bar (under the score, name label dropped once seen). Kill icon and card are gone.
5. **Game over within 6:** score count-up (hero) with a HIGH SCORE stamp on it, a single stats line (metres · best combo · Devourers), **Run again** (primary), and Menu. The TOTAL/HS duplicate board and the medal are removed. The medal moves into the stamp.
6. **Full bleed.** Use `stretch/aspect="expand"` with the height fixed at 136, so the world widens on 19.5:9 phones. Spawn x positions are measured from the right edge, the hero stays at the original x, the parallax layers tile, and the HUD is anchored to `DisplayServer.get_display_safe_area()` with the leading and trailing insets treated separately.
7. **Felt reactions:** `Input.vibrate_handheld(15)` on a stomp, 40 on a hit or shield break, 80 on death, gated by the sound/haptics toggle and skipped under Reduce Motion where it applies.

The rubric reviewers' fixes are already folded in: no "Land on their heads" sentence, cues as glyphs, and one overlay at a time.

## 3. Work list (sizes: S ≤ ½ day, M ≈ 1 day, L ≈ 2–3 days)
| # | Item | Size | Gated? |
|---|---|---|---|
| W1 | Move to Godot 4.7.2 (the playtest already passes in a scratch copy); commit `.uid`/project changes | S | no |
| W2 | Full-bleed `expand` + safe-area HUD anchoring + spawn/scroll from the right edge; playtest at 2.17:1 and 1.5:1 window sizes | L | no |
| W3 | Pause on `NOTIFICATION_APPLICATION_PAUSED`/focus out; resume shows the pause card | S | no |
| W4 | Title-over-stage start (item 1), remove the separate menu screen and the HOW TO PLAY card | M | no |
| W5 | In-run teaching beats + first-time marks (items 2–3) + persisted flags; playtest checks for each beat | L | no |
| W6 | Calm HUD (item 4) and game-over trim (item 5) | M | no |
| W7 | Haptics + toggle (item 7) | S | no |
| W8 | Optional: drop in the original SFX + Pig sprite if Crane fetches them from Drive (`tools/sync_assets.sh`, `tools/make_pig.py`) | S | no |
| W9 | iOS preset + icon + launch screen + `tools/check_ios_build.sh` (copied *pattern* from Dim Sum Sort, landscape check inverted) | M | no (writing), yes (export) |
| W10 | First gated export → archive → audit → upload as **1.2 (2)** to TestFlight | M | **yes** |
| W11 | Device pass on TestFlight (Crane's phone): touch latency, audio, haptics, Dynamic Island side | S | Crane |
| W12 | Store shots: 6.9" landscape 2868×1320 (APP_IPHONE_67), 5–6 frames from a `STORE_SIZE` autopilot in the playtest | M | yes (framing only; captures run in the Godot window) |
| W13 | Listing (below), privacy/support pages on curiouscrane.dev/runherorun/ (manual app-sites push, new folder only) | M | no |
| W14 | `/bar-raiser`-style check + presubmit lines; create version 1.2, attach the build, set the review details | S | ASC writes |

Total ≈ 10–12 working days of agent time.

## 4. iOS export and signing (from Dim Sum Sort, `~/Documents/Projects/dim-sum-sort/APP_STORE.md`)
Preset `iOS` in `godot/export_presets.cfg`:
- `application/app_store_team_id="55KH4DKW5R"`, `bundle_identifier="com.crane.compress"`, `short_version="1.2"`, `version="2"`, `min_ios_version="17.0"`, `targeted_device_family=0` (iPhone), `export_method_release=0` (App Store).
- `additional_plist_content="<key>ITSAppUsesNonExemptEncryption</key><false/>"`.
- Landscape only: `display/window/handheld/orientation=0` (landscape) and sensor landscape, so both landscape sides are allowed. Make sure the plist has no portrait orientations; the audit script checks this.
- `icons/icon_1024x1024` from the original title-art hero face (no alpha), `storyboard/use_custom_bg_color=true` with the stage 1 sky colour.
- `exclude_filter="tests/*, tools/*, docs/*, *.md"`; no `user_data` sharing.
- The privacy manifest is generated by Godot: no tracking, no domains.

Commands (each one gated on the Mac):
```bash
G="/Applications/Godot 4.7.app/Contents/MacOS/Godot"
cd godot
"$G" --headless --path . res://tests/playtest.tscn                       # must print RESULT: PASS, no SCRIPT ERROR
"$G" --headless --path . --export-release "iOS" build/ios/RunHeroRun.ipa  # writes the Xcode project + .xcarchive
tools/check_ios_build.sh build/ios/RunHeroRun.xcarchive                    # orientations, privacy manifest, min OS, encryption key, version
node ~/Documents/Projects/mobile-apps/scripts/fleet/appstore-profile.mjs com.crane.compress   # "Fleet AppStore com.crane.compress" profile
# export + upload with the fleet's manual signing (Apple Distribution: Marcella Prasetio (55KH4DKW5R)) and the API key:
~/Documents/Projects/mobile-apps/scripts/fleet/upload-batch.sh …         # or the orchestrator's $SP/upload-batch.sh; ExportOptions: method app-store-connect, signingStyle manual
pnpm check:binary build/ios/RunHeroRun.xcarchive --max 50                 # from mobile-apps; commit the fingerprint
```
Bump `application/version` before every upload. ASC builds so far are "1", so the first new build is **2**.

## 5. Store shots
- **Required:** APP_IPHONE_67, landscape **2868×1320**, 3–6 shots. The old 3.5/4/4.7/5.5 sets can stay (ASC scales the 6.9" set down) or be deleted when the new set is uploaded (standing OK).
- **Not needed:** an iPad 13" set (iPhone-only).
- Proposed frames, all real gameplay: (1) the title over the live stage, (2) a stomp with the drawn arc and ×3 combo, (3) the Devourer chase with its soul stream, (4) a later stage (stage 3 or 4 art), (5) a power-up active, (6) game over with the HIGH SCORE stamp. Captions are short, in game vocabulary, and placed in the sky band so they never cover the hero.

## 6. Listing changes (en-US)
- **Name:** keep "Run Hero Run!" (the brand of a live app; renaming risks the ranking it still has).
- **Subtitle (new):** "Stomp Knights, Outrun a Dragon" (30).
- **Keywords (draft; run `/aso`-style Astro checks before finalizing):** `runner,endless,jump,stomp,knight,wizard,dragon,boss,ninja,arcade,pixel,retro,one tap,offline`
- **Promotional text:** "Ten years later, the hero runs again: a rebuilt game, the dragon boss that was cut in 2015, and new stages."
- **Description (draft):**
  ```
  One tap to jump, two to leap higher. Land on a knight or a wizard and they're gone; miss and you're done.

  Chain stomps without touching the ground for up to ×4 points. Grab orbs for a shield, slow motion, double points or a soul magnet. Let a Summoner escape and the Devourer comes for you: feed its jaw the souls of the enemies you stomp, six times, before it drops on you.

  Run through four stages, from open sky to the dragon's lands, and beat your best distance.

  • Rebuilt from the ground up for current iPhones
  • The Devourer boss, cut from the original release, is finally in
  • No ads, no accounts, no tracking. Plays offline
  ```
- **What's New (1.2):** "Rebuilt for today's iPhones: full-screen play, the Devourer boss, combos, power-ups, a new enemy and four stages. No more ads."
- **Category:** Games > Action (primary), Games > Arcade (secondary).
- **Privacy policy:** https://curiouscrane.dev/runherorun/privacy/ · **Support:** https://curiouscrane.dev/runherorun/ (pages to add to app-sites; manual push of the new folder only).
- **Copyright:** 2026 Marcella Prasetio.

## 7. App Review notes (draft)
```
Run Hero Run! is an offline one-tap side-scrolling runner. This update rebuilds the 2015 game for current iPhones (full-screen landscape, safe areas) and adds the boss, combos, power-ups and new stages.

How to play: tap anywhere on the title to start. Tap to jump, tap again in the air to double jump; land on enemies to defeat them. If a purple-hatted Summoner leaves the screen alive, the Devourer boss appears (usually within 1–2 minutes of play). Pause is the button at the top right.

There are no accounts, ads, in-app purchases or network use, and no data is collected. Unlike the account's puzzle and utility apps, this is a reflex action game.
```
Set `demoAccountRequired=false`.

## 8. Schedule
| Date | Milestone |
|---|---|
| Wed 11-26 – Fri 11-28 | W1, W3, W7, W9 (code-only); Crane: age rating answers, App Privacy, Drive assets (optional) |
| Mon 12-01 – Wed 12-03 | W2 full bleed + safe area; playtest at two aspect ratios |
| **Thu 12-04** | **First gated export + TestFlight upload 1.2 (2)** (proves signing and processing early) |
| Fri 12-05 – Thu 12-11 | W4, W5, W6 (title-over-stage, teaching beats, calm HUD, game over); build 3 to TestFlight |
| Fri 12-12 | Crane's device pass on TestFlight (W11); cold-start walk by a fresh reviewer (≥4) |
| Mon 12-15 – Wed 12-17 | Fixes, store shots (W12), listing + site pages (W13); final build 4 |
| **Fri 12-19** | **Ready for Crane to submit**: version 1.2 created, build attached, listing, shots, age rating, privacy, review notes done |
| 12-22 – 01-02 | Holiday buffer (Apple review slows). Submit whenever Crane chooses |
| **Mon 2027-01-05** | Latest "ready" date that still leaves time for **two rejection rounds** (≈1 week each) before February |
| Jan 2027 | Rejection rounds if any; release manually after approval |

## 9. Needs Crane
1. **Apple notice details:** the exact removal date and the reason given (outdated SDK? crashes on new iOS? 32-bit?). If the notice names a guideline, the review notes should answer it.
2. **Age rating questionnaire** (new fields unanswered) and **App Privacy** labels (Data Not Collected).
3. **Optional Drive assets** for the original sound: Hit, Jump, Knight Attack and Wizard Attack `.m4a`, plus `Pig Sprite.png` (links in RECOVERY.md). The game ships without them: synthesized stand-ins are in.
4. The device pass on TestFlight (touch feel, audio, haptics).
5. Submit (never done by agents).
