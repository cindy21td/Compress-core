## Loads the sprite sheet regions, font, sounds and saved scores
## (port of AssetLoader.java). Coordinates are pixels in texture.png.
extends Node

const PREFS_PATH := "user://compress.cfg"
const SOUND_FILES := {
	"death": "res://assets/sound/Death Sound.ogg",
	"hit": "res://assets/sound/Hit Sound.ogg",
	"jump": "res://assets/sound/Jump Sound.ogg",
	"theme": "res://assets/sound/theme.ogg",
}

var texture: Texture2D
var logo: Texture2D  # optional splash image (libGdx.png)
var font: Font
var font_color := Color.WHITE
var font_scale := 0.15  # Trash3.fnt is 72px; the original drew it at 0.15

# Background
var bg_front := Rect2(0, 0, 612, 408)
var bg_back := Rect2(612, 0, 612, 408)

# Main menu
var title := Rect2(918, 633, 442, 380)
var background_menu_anim := SpriteAnim.new(0.4, [Rect2(0, 408, 459, 306), Rect2(459, 408, 459, 306)], SpriteAnim.Mode.LOOP)
var start_instruction_anim := SpriteAnim.new(0.4, [Rect2(640, 790, 278, 40), Rect2(459, 714, 278, 40)], SpriteAnim.Mode.LOOP)
var still_anim := SpriteAnim.new(0.3, [Rect2(0, 714, 280, 300), Rect2(300, 714, 280, 300)], SpriteAnim.Mode.LOOP)

# Hero
var hero_run_anim := SpriteAnim.new(0.1, [Rect2(918, 408, 75, 75), Rect2(993, 408, 75, 75), Rect2(1068, 408, 75, 75), Rect2(1143, 408, 75, 75)], SpriteAnim.Mode.LOOP_PINGPONG)
var hero_still_anim := SpriteAnim.new(0.3, [Rect2(1068, 483, 75, 75), Rect2(1143, 483, 75, 75)], SpriteAnim.Mode.LOOP_PINGPONG)
var hero_jump := Rect2(918, 483, 75, 75)
var hero_fall := Rect2(993, 483, 75, 75)

# Enemies
var wizard_anim := _enemy_anim(0)
var knight_anim := _enemy_anim(75)
var summoner_anim := _enemy_anim(150)
var knight_attack_anim := SpriteAnim.new(0.2, [Rect2(1449, 75, 75, 75), Rect2(1449, 75, 75, 75), Rect2(1449, 75, 75, 75), Rect2(1524, 75, 75, 75)], SpriteAnim.Mode.LOOP)
const KNIGHT_SWING_FRAME := 3
var flame_anim := SpriteAnim.new(0.2, [Rect2(1449, 0, 37, 37), Rect2(1486, 0, 37, 37), Rect2(1449, 37, 37, 37)], SpriteAnim.Mode.LOOP_PINGPONG)
var light_anim := SpriteAnim.new(0.2, [Rect2(1524, 0, 37, 37), Rect2(1561, 0, 37, 37), Rect2(1524, 37, 37, 37)])
var soul_anim := SpriteAnim.new(0.2, [Rect2(1224, 225, 75, 75), Rect2(1299, 225, 75, 75), Rect2(1374, 225, 75, 75)], SpriteAnim.Mode.LOOP)

# HUD
var kill_icon := Rect2(768, 714, 75, 75)
var distance_icon := Rect2(843, 714, 75, 75)
var scoreboard := Rect2(1224, 300, 426, 200)
var highscore_mark := Rect2(735, 840, 183, 173)
var play_button_up := Rect2(580, 965, 75, 45)
var play_button_down := Rect2(655, 965, 75, 45)
var rate_button_up := Rect2(580, 920, 75, 45)
var rate_button_down := Rect2(655, 920, 75, 45)
var rate_prompt_anim := SpriteAnim.new(0.4, [Rect2(1545, 790, 110, 110), Rect2(1545, 900, 110, 110)], SpriteAnim.Mode.LOOP)

# Medals
var empty_medal := Rect2(1544, 690, 100, 100)
var bronze_medal := Rect2(1430, 690, 100, 100)
var silver_medal := Rect2(1544, 570, 100, 100)
var gold_medal := Rect2(1430, 570, 100, 100)
var medal_glow_anim := SpriteAnim.new(0.6, [Rect2(1430, 795, 100, 100), Rect2(1430, 906, 100, 100)], SpriteAnim.Mode.LOOP)

var _sounds := {}
var _theme_player: AudioStreamPlayer
var _prefs := ConfigFile.new()


static func _enemy_anim(y: int) -> SpriteAnim:
	return SpriteAnim.new(0.2, [Rect2(1224, y, 75, 75), Rect2(1299, y, 75, 75), Rect2(1374, y, 75, 75)], SpriteAnim.Mode.LOOP)


func _ready() -> void:
	texture = load("res://assets/texture.png")
	if ResourceLoader.exists("res://assets/libGdx.png"):
		logo = load("res://assets/libGdx.png")

	# The game's bitmap font, or Godot's default font until Trash3.fnt is added.
	if ResourceLoader.exists("res://assets/Trash3.fnt"):
		font = load("res://assets/Trash3.fnt")
	else:
		font = ThemeDB.fallback_font
		font_color = Color.BLACK
		font_scale = 0.11

	for key in SOUND_FILES:
		if ResourceLoader.exists(SOUND_FILES[key]):
			_sounds[key] = load(SOUND_FILES[key])
	if _sounds.has("theme"):
		var theme: AudioStream = _sounds["theme"].duplicate()
		theme.set("loop", true)
		_theme_player = AudioStreamPlayer.new()
		_theme_player.stream = theme
		add_child(_theme_player)

	_prefs.load(PREFS_PATH)


func play_sound(key: String, volume: float) -> void:
	if not _sounds.has(key):
		return
	var player := AudioStreamPlayer.new()
	player.stream = _sounds[key]
	player.volume_db = linear_to_db(volume)
	player.finished.connect(player.queue_free)
	add_child(player)
	player.play()


func loop_theme(volume: float) -> void:
	if _theme_player:
		_theme_player.volume_db = linear_to_db(volume)
		_theme_player.play()


func stop_theme() -> void:
	if _theme_player:
		_theme_player.stop()


func get_pref(key: String) -> int:
	return _prefs.get_value("scores", key, 0)


func set_pref(key: String, value: int) -> void:
	_prefs.set_value("scores", key, value)
	_prefs.save(PREFS_PATH)
