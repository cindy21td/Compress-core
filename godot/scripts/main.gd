## Screens (splash -> menu -> game), input and drawing
## (port of the libGDX screens, GameRenderer and InputHandler).
## The world is 204x136 units, y pointing down.
extends Node2D

enum Screen { SPLASH, MENU, GAME }

const WIDTH := 204.0
const HEIGHT := 136.0
const FONT_SIZE := 72

var screen := Screen.SPLASH
var screen_time := 0.0  # animation clock, reset per screen like the original

var world: GameWorld
var replay_button: SimpleButton
var rate_button: SimpleButton
var show_buttons := false
var rate_prompt := false

# Full-screen fade (white flash on death, fade from black on entering the game)
var transition_color := Color.BLACK
var transition_alpha := 0.0
var transition_duration := 1.0
var transition_time := 0.0


func _ready() -> void:
	randomize()
	world = GameWorld.new(self)
	replay_button = SimpleButton.new(140 / 3.0, 220 / 3.0, 100 / 3.0, 70 / 3.0,
		Assets.play_button_up, Assets.play_button_down)
	rate_button = SimpleButton.new(380 / 3.0, 217 / 3.0, 100 / 3.0, 70 / 3.0,
		Assets.rate_button_up, Assets.rate_button_down)
	# Prepared now, but only runs once the game screen is showing.
	prepare_transition(Color.BLACK, 1.5)


func _physics_process(delta: float) -> void:
	screen_time += delta
	match screen:
		Screen.SPLASH:
			if screen_time >= 2.0:
				_set_screen(Screen.MENU)
		Screen.GAME:
			world.update(delta)
			_update_knight_swords()
			if transition_alpha > 0:
				transition_time += delta
				var p := minf(transition_time / transition_duration, 1.0)
				transition_alpha = 1.0 - _ease_out_quad(p)
	queue_redraw()


func _set_screen(s: Screen) -> void:
	screen = s
	screen_time = 0.0


## The sword's hitbox reaches further on the last frame of the swing.
func _update_knight_swords() -> void:
	for k in world.scroller.knights:
		if k.alive and k.is_visible and k.is_attacking():
			k.set_sword_thrust(Assets.knight_attack_anim.key_frame_index(screen_time) == Assets.KNIGHT_SWING_FRAME)


func prepare_transition(color: Color, duration: float) -> void:
	transition_color = color
	transition_alpha = 1.0
	transition_duration = duration
	transition_time = 0.0


func show_rate_prompt(value: bool) -> void:
	rate_prompt = value


# ---------------------------------------------------------------- input

func _unhandled_input(event: InputEvent) -> void:
	if event is InputEventMouseButton and event.button_index == MOUSE_BUTTON_LEFT:
		var p: Vector2 = make_input_local(event).position
		if event.pressed:
			_touch_down(p)
		else:
			_touch_up(p)
	elif event.is_action("ui_accept") and not event.is_echo():
		# Keyboard: Space/Enter acts as a tap (and as Replay on the score board).
		# The off-screen point keeps it from pressing the buttons directly.
		var p := Vector2(-100, -100)
		if event.is_pressed():
			_touch_down(p)
		else:
			_touch_up(p)


func _touch_down(p: Vector2) -> void:
	if screen == Screen.GAME and world.is_game_over():
		replay_button.touch_down(p)
		rate_button.touch_down(p)


func _touch_up(p: Vector2) -> void:
	match screen:
		Screen.SPLASH:
			return
		Screen.MENU:
			world.enter_ready()
			show_buttons = true
			_set_screen(Screen.GAME)
			return

	if world.state == GameWorld.State.READY:
		world.start()
	if world.state == GameWorld.State.RUNNING:
		world.hero.on_click()
	if world.is_game_over():
		if replay_button.touch_up(p):
			world.restart()
		elif rate_button.touch_up(p):
			pass  # The store listing is gone; the original did nothing here on desktop either.
		elif p.x < 0:
			world.restart()  # keyboard shortcut


# ---------------------------------------------------------------- drawing

func _draw() -> void:
	match screen:
		Screen.SPLASH:
			_draw_splash()
		Screen.MENU:
			_draw_menu()
		Screen.GAME:
			_draw_game()


func _region(region: Rect2, x: float, y: float, w: float, h: float, modulate := Color.WHITE) -> void:
	draw_texture_rect_region(Assets.texture, Rect2(x, y, w, h), region, modulate)


## libGDX draws text from its top-left corner.
func _text(text: String, x: float, y: float) -> void:
	draw_set_transform(Vector2(x, y), 0.0, Vector2(Assets.font_scale, Assets.font_scale))
	if Assets.font_outline > 0:
		draw_string_outline(Assets.font, Vector2(0, Assets.font.get_ascent(FONT_SIZE)), text,
			HORIZONTAL_ALIGNMENT_LEFT, -1, FONT_SIZE, Assets.font_outline, Color.WHITE)
	draw_string(Assets.font, Vector2(0, Assets.font.get_ascent(FONT_SIZE)), text,
		HORIZONTAL_ALIGNMENT_LEFT, -1, FONT_SIZE, Assets.font_color)
	draw_set_transform(Vector2.ZERO)


func _draw_splash() -> void:
	draw_rect(Rect2(0, 0, WIDTH, HEIGHT), Color.WHITE)
	# Fade in 0.8s, hold 0.4s, fade out 0.8s.
	var t := screen_time
	var a := 0.0
	if t < 0.8:
		a = _ease_in_out_quad(t / 0.8)
	elif t < 1.2:
		a = 1.0
	else:
		a = 1.0 - _ease_in_out_quad(minf((t - 1.2) / 0.8, 1.0))
	# The original showed the libGDX logo here; this port shows the game's title art.
	var h := HEIGHT * 0.8
	var w := h * Assets.title.size.x / Assets.title.size.y
	_region(Assets.title, (WIDTH - w) / 2, (HEIGHT - h) / 2, w, h, Color(1, 1, 1, a))


func _draw_menu() -> void:
	var t := screen_time
	_region(Assets.background_menu_anim.key_frame(t), 0, 0, WIDTH, HEIGHT)
	_region(Assets.title, 250 / 3.0, 0, 350 / 3.0, 278 / 3.0)
	_region(Assets.still_anim.key_frame(t), -110 / 3.0, -10 / 3.0, 280 / 1.8, 300 / 1.8)
	_region(Assets.start_instruction_anim.key_frame(t), 100 / 3.0, 100, 374 / 3.0, 60 / 3.0)


func _draw_game() -> void:
	var t := screen_time
	var s := world.scroller
	_region(Assets.bg_front, s.bg_front.position.x, 0, WIDTH, HEIGHT)
	_region(Assets.bg_back, s.bg_back.position.x, 0, WIDTH, HEIGHT)

	_draw_hero(t)
	for e in s.enemies:
		_draw_enemy(t, e)
	_draw_score(t)

	if transition_alpha > 0:
		draw_rect(Rect2(0, 0, WIDTH, HEIGHT), Color(transition_color, transition_alpha))


func _draw_hero(t: float) -> void:
	var h := world.hero
	var region: Rect2
	if not h.alive:
		region = Assets.soul_anim.key_frame(t)
	elif h.action_disabled:
		region = Assets.hero_still_anim.key_frame(t)
	elif h.falling:
		region = Assets.hero_fall
	elif h.jumped:
		region = Assets.hero_jump
	else:
		region = Assets.hero_run_anim.key_frame(t)
	_region(region, h.position.x, h.position.y, h.width, h.height)


func _draw_enemy(t: float, e: Enemy) -> void:
	if not e.alive:
		_region(Assets.soul_anim.key_frame(t), e.soul.position.x, e.soul.position.y, 15, 15)
	elif e.is_visible:
		var region: Rect2
		match e.type:
			Enemy.Type.WIZARD:
				region = Assets.wizard_anim.key_frame(t)
			Enemy.Type.KNIGHT:
				region = Assets.knight_attack_anim.key_frame(t) if e.is_attacking() else Assets.knight_anim.key_frame(t)
			_:
				region = Assets.summoner_anim.key_frame(t)
		_region(region, e.position.x, e.position.y, e.width, e.height)

	for p in e.get_projectiles():
		if not p.is_visible:
			continue
		if p.is_moving:
			_region(Assets.flame_anim.key_frame(t), p.position.x, p.position.y, p.width, p.height)
		else:
			_region(Assets.light_anim.key_frame(t), p.position.x + 1, p.position.y + 4, p.width - 5, p.height - 5)


func _draw_score(t: float) -> void:
	if world.state == GameWorld.State.READY:
		_text("--Instructions--", 65, 25)
		_text("1. Tap Once to Jump", 70, 35)
		_text("2. Tap Twice to Double Jump", 70, 45)
		_text("3. Jump on Enemies for Points", 70, 55)
		_text("4. Dodge Enemies", 70, 65)
		_text("--> Tap To Begin <--", 60, 85)
		return

	if world.is_game_over():
		_region(Assets.scoreboard, 33, 0, 426 / 3.0, 200 / 3.0)
		var total := world.total_score
		_text(str(total), 75, 47)
		_text(str(Assets.get_pref("highScore")), 125, 47)
		if world.state == GameWorld.State.HIGHSCORE:
			_region(Assets.highscore_mark, 80, 43, 183 / 4.0, 173 / 4.0)

		var medal := Assets.empty_medal
		if total > 150:
			medal = Assets.gold_medal
		elif total > 75:
			medal = Assets.silver_medal
		elif total > 25:
			medal = Assets.bronze_medal
		if total > 25:
			_region(Assets.medal_glow_anim.key_frame(t), 87, 73, 100 / 3.0, 100 / 3.0)
		_region(medal, 87, 75, 100 / 3.0, 100 / 3.0)

		if rate_prompt:
			_region(Assets.rate_prompt_anim.key_frame(t), 144, 42, 110 / 3.0, 110 / 3.0)
		if show_buttons:
			for b in [replay_button, rate_button]:
				_region(b.region(), b.rect.position.x, b.rect.position.y, b.rect.size.x, b.rect.size.y)

	_region(Assets.kill_icon, 0, 2, 13, 13)
	_region(Assets.distance_icon, -2, 13, 13, 13)
	_text(str(world.score), 15, 5)
	_text(str(world.get_distance()), 15, 15)


static func _ease_out_quad(p: float) -> float:
	return -p * (p - 2)


static func _ease_in_out_quad(p: float) -> float:
	return 2 * p * p if p < 0.5 else -1 + (4 - 2 * p) * p
