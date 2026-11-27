#!/bin/sh
# Builds the RatingAsk iOS GDExtension (arm64 device, dynamic framework) into
# res://addons/rating_ask/ios/RatingAsk.framework, which is committed so a
# game export never needs this step. Rebuild after changing the C or Swift
# source, or when the project moves to a new Godot minor version.
#
#   native/rating_ask/build.sh            (from godot/, needs Xcode)
#
# Inputs: rating_ask_extension.c (GDExtension entry, plain C), RatingAsk.swift
# (StoreKit call), and gdextension_interface.h dumped from the Godot binary
# below, so the header always matches the engine the game ships with.
set -eu

HERE=$(cd "$(dirname "$0")" && pwd)
GODOT_DIR=$(cd "$HERE/../.." && pwd)
GODOT_BIN=${GODOT_BIN:-"/Applications/Godot 4.7.app/Contents/MacOS/Godot"}
MIN_IOS=17.0  # matches application/min_ios_version in export_presets.cfg
OUT="$GODOT_DIR/addons/rating_ask/ios/RatingAsk.framework"
WORK="$GODOT_DIR/build/rating_ask"
SDK=$(xcrun --sdk iphoneos --show-sdk-path)
TARGET="arm64-apple-ios$MIN_IOS"

rm -rf "$WORK"
mkdir -p "$WORK"
(cd "$WORK" && "$GODOT_BIN" --headless --dump-gdextension-interface >/dev/null 2>&1)
[ -f "$WORK/gdextension_interface.h" ] || { echo "could not dump gdextension_interface.h from $GODOT_BIN"; exit 1; }
"$GODOT_BIN" --version

xcrun --sdk iphoneos clang -target "$TARGET" -isysroot "$SDK" -std=c11 -O2 \
	-Wall -Wextra -Werror -fvisibility=hidden -I "$WORK" \
	-c "$HERE/rating_ask_extension.c" -o "$WORK/rating_ask_extension.o"

rm -rf "$OUT"
mkdir -p "$OUT"
xcrun --sdk iphoneos swiftc -target "$TARGET" -sdk "$SDK" -swift-version 5 -O \
	-warnings-as-errors -parse-as-library -emit-library \
	"$HERE/RatingAsk.swift" "$WORK/rating_ask_extension.o" \
	-module-name RatingAsk -o "$OUT/RatingAsk" \
	-Xlinker -install_name -Xlinker @rpath/RatingAsk.framework/RatingAsk

cat > "$OUT/Info.plist" <<PLIST
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>CFBundleDevelopmentRegion</key><string>en</string>
	<key>CFBundleExecutable</key><string>RatingAsk</string>
	<key>CFBundleIdentifier</key><string>com.crane.compress.ratingask</string>
	<key>CFBundleInfoDictionaryVersion</key><string>6.0</string>
	<key>CFBundleName</key><string>RatingAsk</string>
	<key>CFBundlePackageType</key><string>FMWK</string>
	<key>CFBundleShortVersionString</key><string>1.0</string>
	<key>CFBundleVersion</key><string>1</string>
	<key>CFBundleSupportedPlatforms</key><array><string>iPhoneOS</string></array>
	<key>MinimumOSVersion</key><string>$MIN_IOS</string>
</dict>
</plist>
PLIST

# The entry symbol must be exported, and the binary must be arm64 iOS only.
nm -gU "$OUT/RatingAsk" | grep -q " _rating_ask_init$" || { echo "rating_ask_init not exported"; exit 1; }
xcrun vtool -show-build "$OUT/RatingAsk" | grep -q "platform IOS$" || { echo "not an iOS binary"; exit 1; }
lipo -archs "$OUT/RatingAsk"
otool -L "$OUT/RatingAsk"
echo "built $OUT"

# macOS editor stub: same class, request() does nothing. It keeps the editor
# and the headless tests free of "No GDExtension library found" errors and
# lets tests/rating_test.gd call the registered class for real. Never exported
# (the only preset is iOS).
STUB="$GODOT_DIR/addons/rating_ask/macos/librating_ask_stub.dylib"
mkdir -p "$(dirname "$STUB")"
xcrun --sdk macosx clang -arch arm64 -arch x86_64 -mmacosx-version-min=11.0 -std=c11 -O2 \
	-Wall -Wextra -Werror -fvisibility=hidden -DRATING_ASK_STUB -I "$WORK" \
	-dynamiclib -install_name @rpath/librating_ask_stub.dylib \
	"$HERE/rating_ask_extension.c" -o "$STUB"
nm -gU "$STUB" | grep -q " _rating_ask_init$" || { echo "stub: rating_ask_init not exported"; exit 1; }
echo "built $STUB"
