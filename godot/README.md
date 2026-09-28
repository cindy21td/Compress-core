# Run Hero Run: Godot port

A Godot 4.4 port of the 2015 libGDX game in `../Compress-core`. The rules, timings, spawn logic, hitboxes and screen layout follow the Java source line by line. The files map one to one: `Hero.java` → `scripts/hero.gd`, `GameRenderer` + screens + `InputHandler` → `scripts/main.gd`, and so on.

## Run it
1. Open this folder in Godot 4.4 or newer (Project Manager → Import → `project.godot`).
2. Press F5. Click or tap to play; Space/Enter also works as a tap.

## What's on top of the original
The game rules are unchanged. The port adds a presentation layer:
- **Stages:** the backdrop cycles every 150 m through the sky and four stage backgrounds from the 2016 art backup that never shipped, crossfading between them.
- **Parallax:** sky, individually drifting clouds and hills scroll at different speeds over the ground. Built by `tools/build_art.py` from the originals in `../art-src`.
- **Effects:** dust puffs, squash and stretch, a stomp burst with a "+1" popup and a brief freeze, screen shake, soft shadows, fireball glow and embers, soul sparkles, speed lines and a RUSH banner, and a vignette.
- **Interface:** a menu with a best-score badge and sound toggle, a how-to-play card, a HUD with pause, and a pause menu (Resume / Restart / Menu). The game-over screen animates: the board drops in, the score counts up, the medal pops in and a high-score stamp appears.
- **Sharper art:** hi-res title and menu art from the backup, drawn with smooth, mipmapped filtering.

## Assets
- **Art:** `texture.png` (the original sprite sheet), plus `assets/art/` (layers and hi-res art, from the backup).
- **Music and death sound:** the originals, converted to `.ogg`.
- **Jump and stomp sounds:** synthesized by `tools/make_sounds.py`. The originals are used instead if you add them to `../assets/sound` and run `tools/sync_assets.sh`.
- **Font:** [Fredoka One](assets/fonts/OFL.txt) (SIL Open Font License).

## Differences from the original
- The Rate button and prompt are gone, since the store listing no longer exists. Ads are gone too.
- The window keeps a 3:2 aspect ratio with letterboxing.
- Game logic runs at a fixed 60 ticks per second, so the frame-counted distance stays comparable.
- High score and the sound setting are saved to `user://compress.cfg`.

## Test
`tests/playtest.tscn` plays the whole game and checks state along the way: menu, sound toggle, tutorial, jump, pause, a 46-second invincible demo run through the stages, a real death, the score screen, replay, and pause → Menu. Pass a folder to also save screenshots:

```
godot --path . res://tests/playtest.tscn -- /tmp/shots   # optional folder for screenshots
```
