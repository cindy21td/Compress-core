# Run Hero Run: Godot port

A Godot 4.4 port of the 2015 libGDX game in `../Compress-core`. The rules, timings, spawn logic, hitboxes and screen layout follow the Java source line by line. The files map one to one: `Hero.java` → `scripts/hero.gd`, `GameRenderer` + screens + `InputHandler` → `scripts/main.gd`, and so on.

## Run it
1. Open this folder in Godot 4.4 or newer (Project Manager → Import → `project.godot`).
2. Press F5. Click or tap to play; Space/Enter also works as a tap.

## Assets
The sprite sheet (`texture.png`), font page (`Trash3.png`), theme music and death sound are included. The game runs without the rest, but:
- **`Trash3.fnt` missing**: text uses Godot's default font instead of the game's hand-drawn one.
- **`libGdx.png` missing**: the splash screen is skipped.
- **`Hit Sound`, `Jump Sound` missing**: those effects are silent.

To add them, download the files listed in `../RECOVERY.md` into `../assets/`, then run `tools/sync_assets.sh`. It copies them here and converts the sounds to `.ogg`, since Godot can't play `.m4a`.

## Differences from the original
- Ads and the "Rate" button do nothing: the store listing is gone, and the desktop build of the original disabled them too.
- The window keeps a 3:2 aspect ratio with letterboxing. The original stretched to the phone's screen.
- Game logic runs at a fixed 60 ticks per second. The original's distance counter counted rendered frames, so this keeps scores comparable.
- High score is saved to `user://compress.cfg`.

## Test
`tests/playtest.tscn` plays a run end to end (menu → ready → jump → death → replay) and checks the state along the way:

```
godot --path . res://tests/playtest.tscn -- /tmp/shots   # optional folder for screenshots
```
