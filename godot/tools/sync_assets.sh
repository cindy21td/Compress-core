#!/usr/bin/env bash
# Copies recovered assets from ../assets (the original libGDX layout) into
# this Godot project, converting sounds to Ogg (Godot can't play .m4a).
# Needs ffmpeg on PATH. Run from anywhere: godot/tools/sync_assets.sh
set -euo pipefail
here="$(cd "$(dirname "$0")/.." && pwd)"
src="$here/../assets"
dst="$here/assets"
mkdir -p "$dst/sound"

for f in texture.png; do
  if [ -f "$src/$f" ]; then cp "$src/$f" "$dst/$f"; echo "copied   $f"
  else echo "missing  $f" >&2; fi
done

for f in "Death Sound" "Hit Sound" "Jump Sound" theme "Boss Theme" "Boss Fall"; do
  if [ -f "$src/sound/$f.m4a" ]; then
    ffmpeg -loglevel error -y -i "$src/sound/$f.m4a" -c:a libvorbis -q:a 5 "$dst/sound/$f.ogg"
    echo "converted sound/$f.ogg"
  else
    echo "missing  sound/$f.m4a" >&2
  fi
done
