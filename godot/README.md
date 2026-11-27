# Run Hero Run: Godot port

A Godot 4.4 port of the 2015 libGDX game in `../Compress-core`. The rules, timings, spawn logic, hitboxes and screen layout follow the Java source line by line. The files map one to one: `Hero.java` → `scripts/hero.gd`, `GameRenderer` + screens + `InputHandler` → `scripts/main.gd`, and so on.

## Run it
1. Open this folder in Godot 4.4 or newer (Project Manager → Import → `project.godot`).
2. Press F5. Click or tap to play; Space/Enter also works as a tap.

## New gameplay
- **The Devourer (boss):** the dragon boss cut from the 2015 build, restored from `Boss.java` in git history. A Summoner that escapes alive calls it in. It creeps in from the top-left with its own music; its jaw only eats souls, but if it reaches mid-screen it drops on you. Souls of stomped enemies are drawn into its jaw: each one hurts it (+2) and knocks it back, and six hits beat it (+20).
- **Combos:** stomps chained without touching the ground score ×2, ×3, up to ×4.
- **Power-ups:** orbs float in every 14–24 s. **Shield** takes one hit (and even repels the boss). **Slow-mo** slows time for 4.5 s. **×2** doubles points for 8 s. **Soul magnet** sends souls straight at the boss and adds +1 per stomp for 8 s.
- **The Pig:** a new ground enemy from the 2016 art that charges in fast and hops at random. It appears after 150 m. The art is a stand-in drawn by `tools/make_pig.py`; put the original `Pig Sprite.png` in `../art-src/` and re-run the script to use it.

## What's on top of the original
The port also adds a presentation layer:
- **Stages:** the backdrop cycles every 150 m through the sky and four stage backgrounds from the 2016 art backup that never shipped, crossfading between them.
- **Parallax:** sky, individually drifting clouds and hills scroll at different speeds over the ground. Built by `tools/build_art.py` from the originals in `../art-src`.
- **Effects:** dust puffs, squash and stretch, a stomp burst with a "+1" popup and a brief freeze, screen shake, soft shadows, fireball glow and embers, soul sparkles, speed lines and a RUSH banner, and a vignette.
- **Interface:** a menu with a best-score badge and sound toggle, a how-to-play card, a HUD with pause, and a pause menu (Resume / Restart / Menu). The game-over screen animates: the board drops in, the score counts up, the medal pops in and a high-score stamp appears.
- **Sharper art:** hi-res title and menu art from the backup, drawn with smooth, mipmapped filtering.

## Assets
- **Art:** `texture.png` (the original sprite sheet), plus `assets/art/` (layers and hi-res art, from the backup).
- **Music, boss theme, boss fall and death sound:** the originals, converted to `.ogg`.
- **Jump, stomp, boss landing, power-up and combo sounds:** synthesized by `tools/make_sounds.py`. The originals are used instead if you add them to `../assets/sound` and run `tools/sync_assets.sh`.
- **Font:** [Fredoka One](assets/fonts/OFL.txt) (SIL Open Font License).

## Differences from the original
- The old Rate button and "rate me" bubble are gone, and ads are gone too. From 1.3 the game asks for a rating at most once, through Apple's own prompt (see below).
- The window keeps a 3:2 aspect ratio with letterboxing.
- Game logic runs at a fixed 60 ticks per second, so the frame-counted distance stays comparable.
- High score and the sound setting are saved to `user://compress.cfg`.

## Rating prompt (1.3)
`scripts/rating_policy.gd` decides, `scripts/rating_ask.gd` asks. At most once ever (saved in `user://rating.cfg`), never in the first session or while the in-run lessons are still teaching, and only after a good run: a new best of at least 40 or a Devourer win. The ask is never made on the score board: it waits until the player taps Title, then fires on the title screen. Apple decides whether the prompt shows (at most three times a year, never in TestFlight).

The call goes through a small GDExtension of our own, `addons/rating_ask/` (iOS framework plus a no-op macOS stub for the editor and tests). Source and build script: `native/rating_ask/` (`native/rating_ask/build.sh`, needs Xcode; the built binaries are committed). It is a no-op off iOS and in debug builds.

## Test
`tests/playtest.tscn` plays the whole game and checks state along the way: menu, sound toggle, tutorial, jump, pause, a 46-second invincible demo run through the stages, the Pig, a power-up pickup, two boss fights (one won, one where it drops), a real death with a shield that absorbs the first hit, the score screen, replay, and pause → Menu. Pass a folder to also save screenshots:

```
godot --path . res://tests/playtest.tscn -- /tmp/shots   # optional folder for screenshots
godot --headless --path . res://tests/rating_test.tscn   # rating policy, wrapper and title wiring
```
