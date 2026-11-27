#!/bin/sh
# Godot names the iOS localisation folders after its own locale codes
# (zh_Hans.lproj, pt_BR.lproj). The App Store reads Apple's language IDs
# (zh-Hans, pt-BR), so rename them inside the exported archive, list every
# language in CFBundleLocalizations, re-sign the app with its own identity
# and re-export the .ipa from the fixed archive.
#
#   tools/fix_ios_lproj.sh [build/ios/RunHeroRun.xcarchive]
set -e
ARCHIVE="${1:-build/ios/RunHeroRun.xcarchive}"
APP=$(find "$ARCHIVE/Products/Applications" -maxdepth 1 -name "*.app" | head -1)
[ -d "$APP" ] || { echo "FAIL: no .app in $ARCHIVE"; exit 1; }
PB=/usr/libexec/PlistBuddy
changed=0
for d in "$APP"/*_*.lproj; do
	[ -d "$d" ] || continue
	new="$(dirname "$d")/$(basename "$d" | tr '_' '-')"
	rm -rf "$new"
	mv "$d" "$new"
	echo "renamed $(basename "$d") -> $(basename "$new")"
	changed=1
done
"$PB" -c "Delete :CFBundleLocalizations" "$APP/Info.plist" 2>/dev/null || true
"$PB" -c "Add :CFBundleLocalizations array" "$APP/Info.plist"
i=0
for d in "$APP"/*.lproj; do
	lang=$(basename "$d" .lproj)
	[ "$lang" = "Base" ] && continue
	"$PB" -c "Add :CFBundleLocalizations:$i string $lang" "$APP/Info.plist"
	i=$((i + 1))
done
echo "CFBundleLocalizations: $("$PB" -c "Print :CFBundleLocalizations" "$APP/Info.plist" | tr -d '\n' | tr -s ' ')"
ID=$(codesign -dvv "$APP" 2>&1 | sed -n 's/^Authority=\(Apple .*\)$/\1/p' | head -1)
[ -n "$ID" ] || { echo "FAIL: no signing identity on $APP"; exit 1; }
codesign -f -s "$ID" --preserve-metadata=entitlements,requirements,flags,runtime "$APP"
codesign --verify --deep --strict "$APP" && echo "re-signed: $ID"
DIR=$(dirname "$ARCHIVE")
if [ -f "$DIR/ExportOptions.plist" ]; then
	rm -rf "$DIR/ipa-fixed"
	xcodebuild -exportArchive -archivePath "$ARCHIVE" -exportOptionsPlist "$DIR/ExportOptions.plist" -exportPath "$DIR/ipa-fixed" > "$DIR/export-fixed.log" 2>&1 \
		|| { echo "FAIL: re-export (see $DIR/export-fixed.log)"; tail -5 "$DIR/export-fixed.log"; exit 1; }
	IPA=$(find "$DIR/ipa-fixed" -name "*.ipa" | head -1)
	mv -f "$IPA" "$DIR/RunHeroRun.ipa"
	unzip -l "$DIR/RunHeroRun.ipa" | grep -o '[A-Za-z_-]*\.lproj/' | sort -u | tr '\n' ' '; echo
fi
