## Loads the sprite sheet regions, font, sounds and saved scores
## (port of AssetLoader.java). Coordinates are pixels in texture.png.
extends Node

const PREFS_PATH := "user://compress.cfg"
# Loaded as .ogg (converted originals) or, failing that, .wav
# (the synthesized replacements from tools/make_sounds.py).
const SOUND_FILES := {
	"death": "res://assets/sound/Death Sound",
	"hit": "res://assets/sound/Hit Sound",
	"jump": "res://assets/sound/Jump Sound",
	"theme": "res://assets/sound/theme",
	"boss": "res://assets/sound/Boss Theme",
	"boss_fall": "res://assets/sound/Boss Fall",
	"boss_ground": "res://assets/sound/Boss Ground",
	"power_up": "res://assets/sound/Power Up",
	"combo": "res://assets/sound/Combo",
}
const MUSIC := ["theme", "boss"]
const UI_FONT := "res://assets/fonts/FredokaOne-Regular.ttf"

var texture: Texture2D
var ui_font: Font  # Fredoka One

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

# Parallax layers and stages (built by tools/build_art.py) and hi-res art
# from the 2016 backup. menu_texture.png is the sprite sheet's lower-left
# block (x 0-918, y 408-1013) at 4/3 the resolution.
var sky: Texture2D
var hills: Texture2D
var ground: Texture2D
var clouds: Array[Texture2D] = []
var stages: Array[Texture2D] = []  # index 0 unused: stage 0 is sky + hills
var menu_texture: Texture2D
var title_texture: Texture2D
const MENU_TEX_ORIGIN := Vector2(0, 408)
const MENU_TEX_SCALE := 4.0 / 3.0

# enemy_texture.png is the enemy block of the sprite sheet (x 1224-1599,
# y 0-300) at 4/3 resolution, from the Enemy Sprite.pxm project.
var enemy_texture: Texture2D
const ENEMY_TEX_RECT := Rect2(1224, 0, 375, 300)

var muted := false

# Boss (Boss Texture.png: three 314x240 frames) and the Pig (Pig Sprite.png,
# two 100x100 frames), both from the 2016 backup.
var boss_texture: Texture2D
var boss_anim := SpriteAnim.new(0.2, [Rect2(0, 0, 314, 240), Rect2(314, 0, 314, 240), Rect2(0, 240, 314, 240)], SpriteAnim.Mode.LOOP_PINGPONG)
var boss_fire := Rect2(0, 240, 314, 240)
var pig_texture: Texture2D
var pig_anim := SpriteAnim.new(0.12, [Rect2(0, 0, 100, 100), Rect2(100, 0, 100, 100)], SpriteAnim.Mode.LOOP)

var _sounds := {}
var _music := {}  # key -> AudioStreamPlayer
var current_music := ""
var _prefs := ConfigFile.new()


static func _enemy_anim(y: int) -> SpriteAnim:
	return SpriteAnim.new(0.2, [Rect2(1224, y, 75, 75), Rect2(1299, y, 75, 75), Rect2(1374, y, 75, 75)], SpriteAnim.Mode.LOOP)


func _ready() -> void:
	texture = load("res://assets/texture.png")
	sky = load("res://assets/art/sky.png")
	hills = load("res://assets/art/hills.png")
	ground = load("res://assets/art/ground.png")
	for i in 4:
		clouds.append(load("res://assets/art/cloud_%d.png" % i))
	stages.append(null)
	for i in range(1, 5):
		stages.append(load("res://assets/art/stage_%d.png" % i))
	menu_texture = load("res://assets/art/menu_texture.png")
	title_texture = load("res://assets/art/Title.png")
	enemy_texture = load("res://assets/art/enemy_texture.png")
	boss_texture = load("res://assets/art/boss_texture.png")
	if ResourceLoader.exists("res://assets/art/pig.png"):
		pig_texture = load("res://assets/art/pig.png")
	ui_font = load(UI_FONT)

	for key in SOUND_FILES:
		for ext in [".ogg", ".wav"]:
			var path: String = SOUND_FILES[key] + ext
			if ResourceLoader.exists(path):
				_sounds[key] = load(path)
				break
	for key in MUSIC:
		if _sounds.has(key):
			var stream: AudioStream = _sounds[key].duplicate()
			stream.set("loop", true)
			var player := AudioStreamPlayer.new()
			player.stream = stream
			add_child(player)
			_music[key] = player

	_prefs.load(PREFS_PATH)
	set_muted(_prefs.get_value("settings", "muted", false))


func play_sound(key: String, volume: float, pitch := 1.0) -> void:
	if not _sounds.has(key):
		return
	var player := AudioStreamPlayer.new()
	player.stream = _sounds[key]
	player.volume_db = linear_to_db(volume)
	player.pitch_scale = pitch
	player.finished.connect(player.queue_free)
	add_child(player)
	player.play()


## Plays one music track on loop, stopping any other.
func loop_music(key: String, volume: float) -> void:
	for k in _music:
		if k != key:
			_music[k].stop()
	current_music = key
	if _music.has(key):
		var p: AudioStreamPlayer = _music[key]
		p.volume_db = linear_to_db(volume)
		p.stream_paused = false
		p.play()


func loop_theme(volume: float) -> void:
	loop_music("theme", volume)


func stop_theme() -> void:
	for k in _music:
		_music[k].stop()
	current_music = ""


func pause_theme(paused: bool) -> void:
	for k in _music:
		_music[k].stream_paused = paused


func set_music_pitch(pitch: float) -> void:
	for k in _music:
		_music[k].pitch_scale = pitch


func set_muted(value: bool) -> void:
	muted = value
	AudioServer.set_bus_mute(0, muted)
	_prefs.set_value("settings", "muted", muted)
	_prefs.save(PREFS_PATH)


## Draws a sprite-sheet region, using the hi-res enemy art when it covers it.
func draw_sprite(ci: CanvasItem, region: Rect2, rect: Rect2, modulate := Color.WHITE) -> void:
	if ENEMY_TEX_RECT.encloses(region):
		var r := Rect2((region.position - ENEMY_TEX_RECT.position) * MENU_TEX_SCALE, region.size * MENU_TEX_SCALE)
		ci.draw_texture_rect_region(enemy_texture, rect, r, modulate)
	else:
		ci.draw_texture_rect_region(texture, rect, region, modulate)


## Maps a sprite-sheet region inside the menu block to menu_texture.png.
func hires(region: Rect2) -> Rect2:
	return Rect2((region.position - MENU_TEX_ORIGIN) * MENU_TEX_SCALE, region.size * MENU_TEX_SCALE)


func get_pref(key: String) -> int:
	return _prefs.get_value("scores", key, 0)


func set_pref(key: String, value: int) -> void:
	_prefs.set_value("scores", key, value)
	_prefs.save(PREFS_PATH)
