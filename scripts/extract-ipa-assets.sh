#!/usr/bin/env bash
# Extracts Run Hero Run's game assets from an App Store .ipa into ./assets
# Usage: scripts/extract-ipa-assets.sh path/to/RunHeroRun.ipa
set -euo pipefail

ipa="${1:?usage: $0 path/to/app.ipa}"
root="$(cd "$(dirname "$0")/.." && pwd)"
out="$root/assets"
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

unzip -q "$ipa" -d "$tmp"
app="$(find "$tmp/Payload" -maxdepth 1 -name '*.app' -type d | head -n 1)"
[ -n "$app" ] || { echo "No Payload/*.app found in $ipa" >&2; exit 1; }

mkdir -p "$out/sound"

# Files AssetLoader.java loads via Gdx.files.internal(...)
expected=(
  "texture.png"
  "libGdx.png"
  "Trash3.fnt"
  "sound/Death Sound.m4a"
  "sound/Hit Sound.m4a"
  "sound/Jump Sound.m4a"
  "sound/Knight Attack.m4a"
  "sound/theme.m4a"
  "sound/Wizard Attack.m4a"
)

missing=0
for f in "${expected[@]}"; do
  if [ -f "$app/$f" ]; then
    cp "$app/$f" "$out/$f"
    echo "ok       $f"
  else
    echo "MISSING  $f" >&2
    missing=1
  fi
done

# Bitmap font page images referenced by Trash3.fnt (e.g. Trash3.png)
if [ -f "$out/Trash3.fnt" ]; then
  grep -o 'file="[^"]*"' "$out/Trash3.fnt" | cut -d'"' -f2 | while read -r page; do
    if [ -f "$app/$page" ]; then cp "$app/$page" "$out/$page"; echo "ok       $page"
    else echo "MISSING  $page" >&2; fi
  done
fi

# Keep the icons and launch images too
mkdir -p "$out/ios-bundle"
find "$app" -maxdepth 1 \( -name '*.png' -o -name 'Info.plist' -o -name '*.car' \) \
  -exec cp {} "$out/ios-bundle/" \;

echo "Assets written to $out"
exit $missing
