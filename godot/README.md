# Run Hero Run: Godot port

A Godot 4.4 port of the 2015 libGDX game in `../Compress-core`. The rules, timings, spawn logic, hitboxes and screen layout follow the Java source line by line. The files map one to one: `Hero.java` → `scripts/hero.gd`, `GameRenderer` + screens + `InputHandler` → `scripts/main.gd`, and so on.

## Run it
1. Open this folder in Godot 4.4 or newer (Project Manager → Import → `project.godot`).
2. Press F5. Click or tap to play; Space/Enter also works as a tap.

## Assets
- **Art:** `texture.png` (sprite sheet) and `Trash3.png`, recovered from the original.
- **Music and death sound:** the originals, converted to `.ogg` (Godot can't play `.m4a`).
- **Jump and stomp sounds:** new effects synthesized by `tools/make_sounds.py`. Run it again to regenerate them.
- **Font:** [Fredoka One](assets/fonts/OFL.txt) (SIL Open Font License), with a white outline. It stands in for the original Trash Hand bitmap font.
- **Splash screen:** shows the game's title art instead of the libGDX logo.

The originals still take priority if you add them. Download them into `../assets/` (links in `../RECOVERY.md`) and run `tools/sync_assets.sh`. Its converted `.ogg` sounds are used ahead of the `.wav` replacements, and `Trash3.fnt` is used ahead of Fredoka One.

## Differences from the original
- The splash shows the title art instead of the libGDX logo.
- Ads and the "Rate" button do nothing: the store listing is gone, and the desktop build of the original disabled them too.
- The window keeps a 3:2 aspect ratio with letterboxing. The original stretched to the phone's screen.
- Game logic runs at a fixed 60 ticks per second. The original's distance counter counted rendered frames, so this keeps scores comparable.
- High score is saved to `user://compress.cfg`.

## Test
`tests/playtest.tscn` plays a run end to end (menu → ready → jump → death → replay) and checks the state along the way:

```
godot --path . res://tests/playtest.tscn -- /tmp/shots   # optional folder for screenshots
```
