#!/bin/sh
# Pre-upload audit of an exported iOS archive, for the problems that pass
# upload and processing but bounce at App Review ("Invalid Binary") or break
# the unlock on device:
#   - any portrait orientation (the game is landscape-only), or no landscape at all
#   - a privacy manifest that claims tracking with no domains (ITMS-91064)
#   - nested bundles whose MinimumOSVersion disagrees with the app's
#   - ITSAppUsesNonExemptEncryption missing (blocks TestFlight until answered)
# Prints the version/build so a stale build number is caught before upload.
#
#   tools/check_ios_build.sh [build/ios/RunHeroRun.xcarchive]
# Exits non-zero on any problem.

ARCHIVE="${1:-build/ios/RunHeroRun.xcarchive}"
APP=$(find "$ARCHIVE/Products/Applications" -maxdepth 1 -name "*.app" | head -1)
PB=/usr/libexec/PlistBuddy
fail=0
bad() { echo "FAIL: $*"; fail=1; }
get() { "$PB" -c "Print :$2" "$1" 2>/dev/null; }

[ -d "$APP" ] || { echo "FAIL: no .app in $ARCHIVE"; exit 1; }
PLIST="$APP/Info.plist"
MIN_APP=$(get "$PLIST" MinimumOSVersion)
echo "$(get "$PLIST" CFBundleIdentifier) $(get "$PLIST" CFBundleShortVersionString) ($(get "$PLIST" CFBundleVersion)), min iOS $MIN_APP, device family $(get "$PLIST" UIDeviceFamily | tr -d '\n ')"

for key in UISupportedInterfaceOrientations "UISupportedInterfaceOrientations~ipad"; do
	if get "$PLIST" "$key" | grep -q Portrait; then
		bad "$key includes a portrait orientation"
	fi
done
get "$PLIST" UISupportedInterfaceOrientations | grep -q LandscapeLeft || bad "LandscapeLeft missing"
get "$PLIST" UISupportedInterfaceOrientations | grep -q LandscapeRight || bad "LandscapeRight missing"
[ "$(get "$PLIST" CFBundleShortVersionString)" = "1.2" ] || bad "CFBundleShortVersionString is not 1.2"

PRIV="$APP/PrivacyInfo.xcprivacy"
if [ ! -f "$PRIV" ]; then
	bad "no PrivacyInfo.xcprivacy in the app"
elif [ "$(get "$PRIV" NSPrivacyTracking)" = "true" ]; then
	get "$PRIV" NSPrivacyTrackingDomains | grep -q '\.' || bad "NSPrivacyTracking=true with no NSPrivacyTrackingDomains (ITMS-91064)"
fi

# (paths contain spaces — "Launch Screen.storyboardc" — so read line by line;
# the subshell can't set $fail, hence the marker file)
find "$APP" -mindepth 2 -name Info.plist | while IFS= read -r p; do
	m=$(get "$p" MinimumOSVersion)
	if [ -n "$m" ] && [ "$m" != "$MIN_APP" ]; then
		echo "FAIL: ${p#"$APP"/} MinimumOSVersion $m (app is $MIN_APP)"
		touch "$ARCHIVE/.check_failed"
	fi
done
[ -f "$ARCHIVE/.check_failed" ] && { rm -f "$ARCHIVE/.check_failed"; fail=1; }
if find "$APP" -name "*.bundle" | grep -q .; then
	bad "unexpected .bundle inside the app: $(find "$APP" -name '*.bundle' | tr '\n' ' ')"
fi
FW=$(ls "$APP/Frameworks" 2>/dev/null | tr '\n' ' ')
echo "frameworks: $FW"

[ "$(get "$PLIST" ITSAppUsesNonExemptEncryption)" = "false" ] || bad "ITSAppUsesNonExemptEncryption not false"

[ $fail -eq 0 ] && echo "PASS: no known Invalid Binary causes"
exit $fail
