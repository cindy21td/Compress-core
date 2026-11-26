## Screens (splash -> menu -> game), input, and drawing of the world and UI.
## Game rules live in GameWorld and friends (ported from the Java source);
## this file adds presentation: parallax, effects, HUD and menus.
## The world is 204x136 units with y pointing down.
extends Node2D

enum Screen { SPLASH, MENU, GAME }

const WIDTH := 204.0
const HEIGHT := 136.0
const GROUND_Y := 127.5  # where feet meet the ground strip
const SPLASH_TIME := 2.0
const GAME_OVER_INPUT_DELAY := 0.6
const KEY_TAP := Vector2(-10000, -10000)  # a keyboard tap: no button under it

var screen := Screen.SPLASH
var screen_time := 0.0  # animation clock, reset per screen like the original
var paused := false

var world: GameWorld
var backdrop: Backdrop
var fx := Fx.new()
var world_view: Node2D
var ui_view: Node2D
var vignette: GradientTexture2D

# Buttons
var sound_button := UiButton.new(Rect2(184, 116, 16, 16), "", Ui.Icon.SOUND_ON)
var pause_button := UiButton.new(Rect2(186, 3, 15, 15), "", Ui.Icon.PAUSE)
var resume_button := UiButton.new(Rect2(64, 40, 76, 17), "Resume", Ui.Icon.PLAY, Ui.GOLD)
var restart_button := UiButton.new(Rect2(64, 61, 76, 17), "Restart", Ui.Icon.REPLAY)
var pause_menu_button := UiButton.new(Rect2(64, 82, 76, 17), "Menu", Ui.Icon.HOME)
# Game over: two matching icon buttons, Run again (gold) and Menu.
var replay_button := UiButton.new(Rect2(54, 78, 46, 22), "", Ui.Icon.REPLAY, Ui.GOLD)
var menu_button := UiButton.new(Rect2(104, 78, 46, 22), "", Ui.Icon.HOME)
var cues_button := UiButton.new(Rect2(164, 116, 16, 16), "?")  # replays the in-run lessons
var coach := Coach.new()
var cues_flash := 99.0

# Presentation state
var game_over_time := 0.0
var score_pop := 1.0  # seconds since the score last changed
var shown_score := 0
var banner_text := ""
var banner_time := 99.0
var banner_color := Ui.PAPER
var hero_scale := Vector2.ONE
var dust_timer := 0.0
var hit_stop := 0.0
var was_rush := false
var last_stage := 0

# Layout (full bleed): the world fills the screen; the UI is laid out in a
# 204-wide frame centred in the safe area, and edge controls move out by pad.
var view_w := WIDTH
var pad := 0.0     # frame edge to safe edge, each side
var safe_b := 0.0  # bottom inset (home indicator)
var boss_named := false
var stamp_buzzed := false
var killer_rect := Rect2()  # what ended the run, marked on the game-over screen
var orb_mark := 0.0   # seconds the first power-up orb has been marked
var boss_mark := 0.0  # seconds the first Devourer has been marked

# Full-screen fade (white flash on death, fade from black on entering the game)
var transition_color := Color.BLACK
var transition_alpha := 0.0
var transition_duration := 1.0
var transition_time := 0.0


func _ready() -> void:
	randomize()
	world = GameWorld.new(self)
	backdrop = Backdrop.new()
	world.stomped.connect(_on_stomp)
	world.died.connect(_on_death)
	world.boss_summoned.connect(_on_boss_summoned)
	world.boss_hit.connect(_on_boss_hit)
	world.boss_defeated.connect(_on_boss_defeated)
	world.boss_slammed.connect(_on_boss_slammed)
	world.powerup_collected.connect(_on_powerup)
	world.shield_broken.connect(_on_shield_broken)

	world_view = _add_view(_draw_world)
	ui_view = _add_view(_draw_ui)

	vignette = GradientTexture2D.new()
	vignette.fill = GradientTexture2D.FILL_RADIAL
	vignette.fill_from = Vector2(0.5, 0.5)
	vignette.fill_to = Vector2(1.05, 1.05)
	vignette.gradient = Gradient.new()
	vignette.gradient.set_color(0, Color(0, 0, 0, 0))
	vignette.gradient.set_color(1, Color(0, 0, 0, 0.45))
	vignette.gradient.add_point(0.6, Color(0, 0, 0, 0))
	coach.load_flags()


func _add_view(painter: Callable) -> Node2D:
	var v := Node2D.new()
	v.set_script(preload("res://scripts/view.gd"))
	v.painter = painter
	add_child(v)
	return v


# ---------------------------------------------------------------- update

func _update_layout() -> void:
	view_w = get_viewport_rect().size.x
	Geo.view_width = view_w
	var safe_l := 0.0
	var safe_r := 0.0
	safe_b = 0.0
	if OS.has_feature("mobile"):
		var win := Vector2(DisplayServer.window_get_size())
		var sa := Rect2(DisplayServer.get_display_safe_area())
		if win.y > 0 and sa.size.x > 0:
			var k := HEIGHT / win.y
			safe_l = sa.position.x * k
			safe_r = maxf(0, (win.x - sa.end.x) * k)
			safe_b = maxf(0, (win.y - sa.end.y) * k)
	# Centre the frame between the insets, but never let it cross one.
	pad = maxf(0, (view_w - safe_l - safe_r - WIDTH) / 2)
	ui_view.position.x = safe_l + pad
	pause_button.rect.position.x = 186 + pad
	sound_button.rect.position = Vector2(184 + pad, 116 - safe_b)
	cues_button.rect.position = Vector2(164 + pad, 116 - safe_b)


## The whole screen in the given layer's coordinates (with a margin for shake).
func _full(ci: CanvasItem) -> Rect2:
	var o: Vector2 = (ci as Node2D).position
	return Rect2(-o - Vector2(4, 4), Vector2(view_w + 8, HEIGHT + 8))


func _physics_process(delta: float) -> void:
	_update_layout()
	screen_time += delta
	match screen:
		Screen.SPLASH:
			if screen_time >= SPLASH_TIME:
				_set_screen(Screen.MENU)
		Screen.MENU:
			backdrop.update(delta, 0, 0)
			cues_flash += delta
		Screen.GAME:
			if not paused:
				_update_game(delta)
	world_view.position = Vector2.ZERO if coach.reduce_motion else fx.shake_offset()
	world_view.queue_redraw()
	ui_view.queue_redraw()


func _update_game(delta: float) -> void:
	fx.update(delta)
	var h := world.hero
	var running := world.state == GameWorld.State.RUNNING
	if hit_stop > 0:
		hit_stop -= delta
	else:
		var was_jumping := h.jumped
		var boss_phase := world.scroller.boss.phase
		world.update(delta)
		if boss_phase == Boss.Phase.CHASE and world.scroller.boss.phase == Boss.Phase.DROP:
			Assets.play_sound("boss_fall", 0.5)
		_update_knight_swords()
		if world.state == GameWorld.State.RUNNING and coach.update(delta, world):
			_buzz(25)  # a spared enemy walked past: the missed arc is drawn
		if running and was_jumping and not h.jumped and h.alive:
			hero_scale = Vector2(1.25, 0.78)
			fx.dust(_feet(), 5, 14)

	running = world.state == GameWorld.State.RUNNING
	_tick_first_sight(delta)
	_apply_slowmo(running)
	var ground_speed := -world.scroller.bg_front.velocity.x if running and hit_stop <= 0 else 0.0
	backdrop.update(delta, ground_speed, world.get_distance())
	hero_scale = hero_scale.lerp(Vector2.ONE, minf(delta * 12, 1))

	if running:
		_ambient_effects(delta)
	if backdrop.stage != last_stage:
		last_stage = backdrop.stage
		if running:
			_show_banner("STAGE %d" % (backdrop.stage + 1), Ui.PAPER)
	var rush := world.scroller.state == ScrollHandler.RunningState.RUSH
	if rush and not was_rush and running:
		_show_banner("RUSH!", Ui.RED)
	was_rush = rush

	if world.score != shown_score:
		shown_score = world.score
		score_pop = 0.0
	score_pop += delta
	banner_time += delta
	if world.is_game_over():
		game_over_time += delta
	if transition_alpha > 0:
		transition_time += delta
		transition_alpha = 1.0 - Ui.ease_out_quad(minf(transition_time / transition_duration, 1.0))


func _ambient_effects(delta: float) -> void:
	var h := world.hero
	if h.alive and not h.jumped and not h.action_disabled:
		dust_timer -= delta
		if dust_timer <= 0:
			fx.dust(_feet() + Vector2(-4, 0), 1, 6)
			dust_timer = 0.09
	for e in world.scroller.enemies:
		for p in e.get_projectiles():
			if p.is_visible and p.is_moving and randf() < 0.6:
				fx.ember(p.position + Vector2(5, 5))
		if not e.alive and e.soul.is_visible and randf() < 0.35:
			fx.sparkle(e.soul.position + Vector2(7, 7))
	if world.scroller.state == ScrollHandler.RunningState.RUSH and randf() < 0.22:
		fx.speedline()


## Bullet time while the slow-mo power-up lasts (the rules see less game time).
func _apply_slowmo(running: bool) -> void:
	var slow := running and not paused and (world.slowmo_time > 0 or coach.slow(world))
	var target := 0.55 if slow else 1.0
	if not is_equal_approx(Engine.time_scale, target):
		Engine.time_scale = target
		Assets.set_music_pitch(0.8 if slow else 1.0)


func _feet() -> Vector2:
	return world.hero.position + Vector2(13, 24)


## The sword's hitbox reaches further on the last frame of the swing.
func _update_knight_swords() -> void:
	for k in world.scroller.knights:
		if k.alive and k.is_visible and k.is_attacking():
			k.set_sword_thrust(Assets.knight_attack_anim.key_frame_index(screen_time) == Assets.KNIGHT_SWING_FRAME)


func _on_stomp(enemy: Enemy, points: int, combo: int) -> void:
	fx.stomp(enemy.circle_center, "+%d" % points)
	hit_stop = 0.05
	hero_scale = Vector2(1.2, 0.85)
	_buzz(15)
	if coach.on_stomp(enemy):
		hit_stop = 0.12  # the first stomp holds for a beat while its arc is drawn
		_buzz(25)
		Assets.play_sound("combo", 0.35, 1.3)
	if combo >= 2:
		Assets.play_sound("combo", 0.35, 1.0 + 0.12 * mini(combo, 6))
		fx.popup("COMBO x%d" % mini(combo, GameWorld.MAX_COMBO_MULTIPLIER), enemy.circle_center + Vector2(0, -14), Ui.GOLD, 1.2)
		if combo >= 3:
			fx.shake(1.5 + combo * 0.3, 0.15)


func _on_boss_summoned() -> void:
	_show_banner("THE DEVOURER!", Ui.RED)
	fx.shake(2.5, 0.6)


func _on_boss_hit(pos: Vector2) -> void:
	fx.burst(pos, Color(0.75, 0.55, 1.0), 12)
	fx.popup("+%d" % GameWorld.BOSS_HIT_POINTS, pos + Vector2(10, -4), Color.WHITE)
	fx.shake(2.0, 0.2)
	Assets.play_sound("hit", 0.4, 0.55)


func _on_boss_defeated() -> void:
	boss_named = true  # the bar's name label only shows at the first fight
	var b := world.scroller.boss
	for i in 4:
		fx.burst(b.position + Vector2(randf_range(30, 140), randf_range(40, 110)), Color(1, 0.6, 0.2), 16)
	fx.shake(4.0, 0.6)
	_show_banner("DEVOURER DOWN! +%d" % GameWorld.BOSS_DEFEAT_BONUS, Ui.GOLD)
	Assets.play_sound("boss_ground", 0.5, 1.4)


func _on_boss_slammed() -> void:
	fx.shake(5.0, 0.6)
	Assets.play_sound("boss_ground", 0.7)
	_buzz(40)
	for x in range(0, 110, 10):
		fx.dust(Vector2(x, GROUND_Y), 2, 30)


func _on_powerup(kind: PowerUp.Kind) -> void:
	Assets.play_sound("power_up", 0.45)
	var names := {PowerUp.Kind.SHIELD: "SHIELD", PowerUp.Kind.SLOWMO: "SLOW-MO",
		PowerUp.Kind.DOUBLE: "DOUBLE POINTS", PowerUp.Kind.MAGNET: "SOUL MAGNET"}
	fx.burst(world.hero.body_center, Ui.powerup_color(kind), 14)
	_show_banner(names[kind], Ui.powerup_color(kind).lightened(0.35))


func _on_shield_broken() -> void:
	fx.burst(world.hero.body_center, Ui.powerup_color(PowerUp.Kind.SHIELD), 18)
	fx.shake(2.5, 0.25)
	Assets.play_sound("hit", 0.45, 0.7)
	_buzz(40)


func _on_death() -> void:
	banner_time = 99.0  # no banners over the score screen
	killer_rect = Rect2()
	var k: Object = world.killer
	if k is Boss:
		var j := (k as Boss).jaw()
		killer_rect = Rect2(j.end.x - 30, j.position.y - 22, 30, 30)
	elif k is Scrollable:
		var sc := k as Scrollable
		killer_rect = Rect2(sc.position, Vector2(sc.width, sc.height))
	fx.death(world.hero.body_center)
	game_over_time = 0.0
	stamp_buzzed = false
	_buzz(80)


func _show_banner(text: String, color: Color) -> void:
	banner_text = text
	banner_color = color
	banner_time = 0.0


func _set_screen(s: Screen) -> void:
	screen = s
	screen_time = 0.0


func prepare_transition(color: Color, duration: float) -> void:
	transition_color = color
	transition_alpha = 1.0
	transition_duration = duration
	transition_time = 0.0


func show_rate_prompt(_value: bool) -> void:
	pass  # the store listing is gone, so the "rate me" bubble is retired


## The title sits on the live stage, so the tap that dismisses it is the
## first step of the run: no ready card, no fade to black.
func _start_run() -> void:
	world.enter_ready()
	_reset_presentation()
	_set_screen(Screen.GAME)
	_go()


func _go() -> void:
	coach.start_run()
	world.start()
	world.hero.on_click()


func _restart() -> void:
	Engine.time_scale = 1.0
	Assets.set_music_pitch(1.0)
	world.restart()
	_reset_presentation()
	_go()


func _to_menu() -> void:
	Engine.time_scale = 1.0
	Assets.set_music_pitch(1.0)
	Assets.stop_theme()
	world.to_menu()
	_reset_presentation()
	paused = false
	_set_screen(Screen.MENU)


func _reset_presentation() -> void:
	backdrop.reset()
	fx.clear()
	last_stage = 0
	was_rush = false
	killer_rect = Rect2()
	orb_mark = 0.0
	boss_mark = 0.0
	shown_score = 0
	banner_time = 99.0
	game_over_time = 0.0
	hero_scale = Vector2.ONE


## A run never dies behind a phone call: leaving the app pauses it, and the
## pause card is waiting on return.
func _notification(what: int) -> void:
	if what == NOTIFICATION_APPLICATION_PAUSED or what == NOTIFICATION_APPLICATION_FOCUS_OUT:
		pause_for_background()


func pause_for_background() -> void:
	if screen == Screen.GAME and world.state == GameWorld.State.RUNNING and not paused:
		_set_paused(true)


## Felt reactions (principle 13), off with the sound toggle.
func _buzz(ms: int) -> void:
	if not Assets.muted:
		Input.vibrate_handheld(ms)


func _set_paused(value: bool) -> void:
	paused = value
	Assets.pause_theme(value)
	Engine.time_scale = 1.0 if value else Engine.time_scale


# ---------------------------------------------------------------- input

func _unhandled_input(event: InputEvent) -> void:
	if event is InputEventMouseButton and event.button_index == MOUSE_BUTTON_LEFT:
		var p: Vector2 = make_input_local(event).position
		p -= ui_view.position  # buttons live in the UI frame
		if event.pressed:
			_touch_down(p)
		else:
			_touch_up(p)
	elif event.is_action("ui_cancel") and event.is_pressed() and not event.is_echo():
		if screen == Screen.GAME and world.state == GameWorld.State.RUNNING:
			_set_paused(not paused)
	elif event.is_action("ui_accept") and not event.is_echo():
		# Keyboard: Space/Enter acts as a tap (and as Replay on the score board).
		# The off-screen point keeps it from pressing buttons directly.
		var p := KEY_TAP
		if event.is_pressed():
			_touch_down(p)
		else:
			_touch_up(p)


func _touch_down(p: Vector2) -> void:
	match screen:
		Screen.MENU:
			sound_button.touch_down(p)
			cues_button.touch_down(p)
		Screen.GAME:
			if paused:
				for b in [resume_button, restart_button, pause_menu_button]:
					b.touch_down(p)
			elif world.state == GameWorld.State.RUNNING:
				pause_button.touch_down(p)
			elif world.is_game_over() and game_over_time > GAME_OVER_INPUT_DELAY:
				replay_button.touch_down(p)
				menu_button.touch_down(p)


func _touch_up(p: Vector2) -> void:
	match screen:
		Screen.SPLASH:
			_set_screen(Screen.MENU)
			return
		Screen.MENU:
			if sound_button.touch_up(p):
				Assets.set_muted(not Assets.muted)
			elif cues_button.touch_up(p):
				coach.replay()
				cues_flash = 0.0
				_buzz(15)
			else:
				_start_run()
			return

	if paused:
		if resume_button.touch_up(p) or p == KEY_TAP:
			_set_paused(false)
		elif restart_button.touch_up(p):
			_set_paused(false)
			Assets.stop_theme()
			_restart()
		elif pause_menu_button.touch_up(p):
			_to_menu()
		return

	if world.state == GameWorld.State.RUNNING and pause_button.touch_up(p):
		_set_paused(true)
		return
	if world.state == GameWorld.State.READY:
		world.start()
	if world.state == GameWorld.State.RUNNING:
		_hero_click()
	if world.is_game_over() and game_over_time > GAME_OVER_INPUT_DELAY:
		if replay_button.touch_up(p) or p == KEY_TAP:
			_restart()
		elif menu_button.touch_up(p):
			_to_menu()


func _hero_click() -> void:
	var h := world.hero
	var jumped := h.jumped
	var doubled := h.double_jumped
	h.on_click()
	if h.jumped and not jumped:
		hero_scale = Vector2(0.8, 1.25)
		fx.dust(_feet(), 4, 10)
	elif h.double_jumped and not doubled:
		hero_scale = Vector2(0.85, 1.2)
		fx.air_jump(_feet())
		if coach.on_double(h):
			_buzz(25)
			Assets.play_sound("combo", 0.35, 1.45)


# ---------------------------------------------------------------- world layer

func _draw_world(ci: CanvasItem) -> void:
	match screen:
		Screen.SPLASH:
			_draw_splash(ci)
		Screen.MENU:
			_draw_menu(ci)
		Screen.GAME:
			_draw_game_world(ci)


func _region(ci: CanvasItem, region: Rect2, rect: Rect2, modulate := Color.WHITE) -> void:
	Assets.draw_sprite(ci, region, rect, modulate)


func _hires(ci: CanvasItem, region: Rect2, rect: Rect2, modulate := Color.WHITE) -> void:
	ci.draw_texture_rect_region(Assets.menu_texture, rect, Assets.hires(region), modulate)


func _draw_splash(ci: CanvasItem) -> void:
	ci.draw_rect(_full(ci), Color.WHITE)
	# Fade in 0.8s, hold 0.4s, fade out 0.8s.
	var t := screen_time
	var a := 1.0
	if t < 0.8:
		a = Ui.ease_in_out_quad(t / 0.8)
	elif t > 1.2:
		a = 1.0 - Ui.ease_in_out_quad(minf((t - 1.2) / 0.8, 1.0))
	var h := HEIGHT * 0.8 * (0.95 + 0.05 * a)
	var w := h * Assets.title_texture.get_width() / Assets.title_texture.get_height()
	ci.draw_texture_rect(Assets.title_texture, Rect2((view_w - w) / 2, (HEIGHT - h) / 2, w, h), false, Color(1, 1, 1, a))


## The title over the live stage 1: the hero idles where the run starts,
## the clouds drift, and the whole screen is the start button.
func _draw_menu(ci: CanvasItem) -> void:
	var t := screen_time
	backdrop.draw(ci, world.scroller.bg_front.position.x)
	_draw_shadows(ci)
	_draw_hero(ci, t)
	ci.draw_set_transform(Vector2(ui_view.position.x, 0))
	_draw_title(ci, 1.0, 0.0 if coach.reduce_motion else sin(t * 2.2) * 1.5)
	var pulse := 1.0 if coach.reduce_motion else 1.0 + sin(t * 5) * 0.04
	var r := Rect2(WIDTH / 2 - 52, 72, 104, 104 * 60 / 374.0)
	r = Rect2(r.get_center() - r.size * pulse / 2, r.size * pulse)
	_hires(ci, Assets.start_instruction_anim.frames[0], r)
	ci.draw_set_transform(Vector2.ZERO)


func _draw_title(ci: CanvasItem, alpha: float, bob: float) -> void:
	# Larger, with a dark offset copy behind it so it holds on the bright sky.
	var tw := 128.0
	var th := tw * Assets.title_texture.get_height() / Assets.title_texture.get_width()
	var r := Rect2(WIDTH / 2 - tw / 2 + 8, 2 + bob, tw, th)
	for o in [Vector2(1.4, 1.6), Vector2(-0.8, 1.2), Vector2(0.8, -0.6)]:
		ci.draw_texture_rect(Assets.title_texture, Rect2(r.position + o, r.size), false, Color(Ui.INK, 0.55 * alpha))
	ci.draw_texture_rect(Assets.title_texture, r, false, Color(1, 1, 1, alpha))


func _draw_game_world(ci: CanvasItem) -> void:
	var t := screen_time
	var s := world.scroller
	backdrop.draw(ci, s.bg_front.position.x)
	_draw_shadows(ci)
	_draw_boss(ci, t)
	for e in s.enemies:
		_draw_enemy(ci, t, e)
	_draw_powerup(ci, t)
	_draw_hero(ci, t)
	if world.state == GameWorld.State.RUNNING:
		coach.draw(ci, world)
		_first_sight(ci, t)
	elif world.is_game_over() and killer_rect.has_area():
		_killer_mark(ci, t)
	fx.draw(ci)
	fx.draw_popups(ci)
	if world.slowmo_time > 0 and world.state == GameWorld.State.RUNNING:
		ci.draw_rect(_full(ci), Color(0.35, 0.3, 1.0, 0.12))


func _tick_first_sight(delta: float) -> void:
	if world.state != GameWorld.State.RUNNING:
		return
	var p := world.scroller.powerup
	if Assets.get_pref("seenOrb") == 0:
		if p.is_visible:
			orb_mark += delta
		elif orb_mark > 0:  # collected or scrolled off: seen
			Assets.set_pref("seenOrb", 1)
			orb_mark = 0.0
	if world.scroller.boss.phase == Boss.Phase.CHASE and Assets.get_pref("seenBoss") == 0:
		boss_mark += delta
		if boss_mark >= 4.0:
			Assets.set_pref("seenBoss", 1)


## What ended the run: a red ring with a cross badge over it.
func _killer_mark(ci: CanvasItem, t: float) -> void:
	var c := killer_rect.get_center()
	var r := maxf(killer_rect.size.x, killer_rect.size.y) * 0.62 + 2
	var k := clampf(game_over_time / 0.3, 0, 1)
	r *= lerpf(1.6, 1.0, Ui.ease_out_quad(k))
	ci.draw_arc(c, r, 0, TAU, 40, Color(Ui.INK, 0.9 * k), 3.2)
	ci.draw_arc(c, r, 0, TAU, 40, Color(Ui.RED, k), 1.8)
	var b := c + Vector2(r * 0.7, -r * 0.7)
	ci.draw_circle(b, 5.2, Color(Ui.INK, k))
	ci.draw_circle(b, 4.4, Color(Ui.RED, k))
	ci.draw_line(b + Vector2(-2, -2), b + Vector2(2, 2), Color(1, 1, 1, k), 1.3)
	ci.draw_line(b + Vector2(-2, 2), b + Vector2(2, -2), Color(1, 1, 1, k), 1.3)


## First sight of a power-up orb and of the Devourer: a ring and a "!"
## badge, once per install (prefs seenOrb, seenBoss).
func _first_sight(ci: CanvasItem, t: float) -> void:
	var p := world.scroller.powerup
	if p.is_visible and Assets.get_pref("seenOrb") == 0:
		_sight_mark(ci, p.center(), PowerUp.RADIUS + 5, orb_mark, Ui.powerup_color(p.kind))
	var b := world.scroller.boss
	if b.phase == Boss.Phase.CHASE and boss_mark < 4.0 and Assets.get_pref("seenBoss") == 0:
		_sight_mark(ci, b.jaw_target() + Vector2(0, -10), 14, boss_mark, Ui.RED)


func _sight_mark(ci: CanvasItem, c: Vector2, r: float, age: float, color: Color) -> void:
	var pulse := 0.0 if coach.reduce_motion else 0.5 + 0.5 * sin(age * 6)
	var rr := r + 2 * pulse
	ci.draw_arc(c, rr, 0, TAU, 36, Color(Ui.INK, 0.9), 3.0)
	ci.draw_arc(c, rr, 0, TAU, 36, color.lightened(0.3), 1.6)
	var bpos := c + Vector2(0, -rr - 6)
	ci.draw_circle(bpos, 5.0, Ui.INK)
	ci.draw_circle(bpos, 4.2, Ui.GOLD)
	ci.draw_line(bpos + Vector2(0, -2.4), bpos + Vector2(0, 0.8), Ui.INK, 1.3)
	ci.draw_circle(bpos + Vector2(0, 2.2), 0.7, Ui.INK)


func _shadow(ci: CanvasItem, x: float, ground: float, height_above: float, width: float) -> void:
	var k := clampf(height_above / 60.0, 0, 1)
	ci.draw_set_transform(Vector2(x, ground), 0, Vector2(1, 0.28))
	ci.draw_circle(Vector2.ZERO, width * (1 - 0.5 * k), Color(0, 0, 0, 0.28 * (1 - 0.7 * k)))
	ci.draw_set_transform(Vector2.ZERO)


func _draw_shadows(ci: CanvasItem) -> void:
	var h := world.hero
	if h.alive:
		_shadow(ci, h.position.x + 13, GROUND_Y, GROUND_Y - (h.position.y + 24), 7)
	for e in world.scroller.enemies:
		if not (e.alive and e.is_visible):
			continue
		match e.type:
			Enemy.Type.KNIGHT:
				_shadow(ci, e.position.x + 11, e.position.y + 21, 0, 7)
			Enemy.Type.PIG:
				_shadow(ci, e.position.x + 10, Pig.GROUND_Y + 19, Pig.GROUND_Y - e.position.y, 7)
			_:
				_shadow(ci, e.position.x + e.width / 2.0, GROUND_Y, GROUND_Y - e.position.y - e.height, 5)


func _draw_hero(ci: CanvasItem, t: float) -> void:
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
	# Squash and stretch around the feet.
	var foot := h.position + Vector2(h.width / 2.0, h.height)
	ci.draw_set_transform(foot, 0, hero_scale)
	_region(ci, region, Rect2(-h.width / 2.0, -h.height, h.width, h.height))
	ci.draw_set_transform(Vector2.ZERO)
	if h.alive and (world.shield or world.grace > 0):
		var c := Ui.powerup_color(PowerUp.Kind.SHIELD)
		var blink := world.grace > 0 and fmod(t, 0.16) < 0.08
		if not blink:
			ci.draw_circle(h.body_center, 12, Color(c, 0.18))
			ci.draw_arc(h.body_center, 12, 0, TAU, 32, Color(c.lightened(0.4), 0.8), 0.8)


func _draw_enemy(ci: CanvasItem, t: float, e: Enemy) -> void:
	if e.soul.is_visible:
		_region(ci, Assets.soul_anim.key_frame(t), Rect2(e.soul.position, Vector2(15, 15)))
	if e.alive and e.is_visible and e.type == Enemy.Type.PIG:
		var fr := Assets.pig_anim.key_frame(t * (0.3 if (e as Pig).is_hopping() else 1.0))
		ci.draw_texture_rect_region(Assets.pig_texture, Rect2(e.position, Vector2(e.width, e.height)), fr)
	elif e.alive and e.is_visible:
		var region: Rect2
		match e.type:
			Enemy.Type.WIZARD:
				region = Assets.wizard_anim.key_frame(t)
			Enemy.Type.KNIGHT:
				region = Assets.knight_attack_anim.key_frame(t) if e.is_attacking() else Assets.knight_anim.key_frame(t)
			_:
				region = Assets.summoner_anim.key_frame(t)
		_region(ci, region, Rect2(e.position, Vector2(e.width, e.height)))

	for p in e.get_projectiles():
		if not p.is_visible:
			continue
		if p.is_moving:
			_glow(ci, p.position + Vector2(5, 5), 7, Color(1, 0.5, 0.1))
			_region(ci, Assets.flame_anim.key_frame(t), Rect2(p.position, Vector2(p.width, p.height)))
		else:
			var c := p.position + Vector2(3.5, 6.5)
			_glow(ci, c, 4 + sin(t * 20) * 0.8, Color(1, 0.95, 0.4))
			_region(ci, Assets.light_anim.key_frame(t), Rect2(p.position + Vector2(1, 4), Vector2(p.width - 5, p.height - 5)))


func _draw_boss(ci: CanvasItem, t: float) -> void:
	var b := world.scroller.boss
	if not b.active():
		return
	var region := Assets.boss_anim.key_frame(t)
	if b.phase in [Boss.Phase.DROP, Boss.Phase.LANDED]:
		region = Assets.boss_fire
	elif b.phase == Boss.Phase.RETREAT:
		region = Assets.boss_anim.frames[0]
	var tint := Color(1, 0.45, 0.45) if b.hurt_time > 0 else Color.WHITE
	var pos := b.position
	if b.phase == Boss.Phase.RETREAT and b.defeated:
		# Beaten: flickers and shudders on the way out.
		tint = Color(1, 0.6, 0.5, 0.8) if int(t * 12) % 2 == 0 else Color(1, 1, 1, 0.8)
		pos += Vector2(sin(t * 70) * 1.5, 0)
	# A dark rim lifts the Devourer off any backdrop.
	for o in [Vector2(-1, 0), Vector2(1, 0), Vector2(0, -1), Vector2(0, 1)]:
		ci.draw_texture_rect_region(Assets.boss_texture, Rect2(pos + o, Vector2(b.width, b.height)), region, Color(Ui.INK, 0.85 * tint.a))
	ci.draw_texture_rect_region(Assets.boss_texture, Rect2(pos, Vector2(b.width, b.height)), region, tint)
	if b.phase == Boss.Phase.CHASE:
		# Its weak point: the jaw that stomped souls fly into.
		var j := b.jaw_target()
		var pr := 0.0 if coach.reduce_motion else sin(t * 6) * 1.2
		ci.draw_arc(j, 6 + pr, 0, TAU, 28, Color(Ui.INK, 0.8), 2.6)
		ci.draw_arc(j, 6 + pr, 0, TAU, 28, Ui.GOLD, 1.3)


func _draw_powerup(ci: CanvasItem, t: float) -> void:
	var p := world.scroller.powerup
	if not p.is_visible:
		return
	var c := p.center()
	var col := Ui.powerup_color(p.kind)
	_glow(ci, c, 9 + sin(t * 6), col)
	ci.draw_circle(c, PowerUp.RADIUS + 0.8, Ui.INK)
	ci.draw_circle(c, PowerUp.RADIUS, col)
	ci.draw_circle(c + Vector2(-2, -2), 1.6, Color(1, 1, 1, 0.7))
	Ui.powerup_icon(ci, p.kind, c, 7)


func _glow(ci: CanvasItem, pos: Vector2, r: float, color: Color) -> void:
	for i in 3:
		ci.draw_circle(pos, r * (1 - i * 0.28), Color(color, 0.12 + i * 0.08))


# ---------------------------------------------------------------- UI layer

func _draw_ui(ci: CanvasItem) -> void:
	if screen == Screen.SPLASH:
		return
	ci.draw_texture_rect(vignette, _full(ci), false)
	if screen == Screen.MENU:
		_draw_menu_ui(ci)
		return

	match world.state:
		GameWorld.State.RUNNING:
			if screen_time < 0.45:  # the title lifts off as the run begins
				_draw_title(ci, 1.0 - screen_time / 0.45, -screen_time * 30)
			_draw_hud(ci)
			pause_button.draw(ci)
		_:
			if world.is_game_over():
				_draw_game_over(ci)
	_draw_banner(ci)
	if paused:
		_draw_pause(ci)
	if transition_alpha > 0:
		ci.draw_rect(_full(ci), Color(transition_color, transition_alpha))


func _draw_menu_ui(ci: CanvasItem) -> void:
	var best := Assets.get_pref("highScore")
	if best > 0:
		var label := "BEST  %d" % best
		var w := Ui.text_width(label, 5.5) + 10
		Ui.panel(ci, Rect2(4 - pad, 4, w, 12), Ui.PAPER, 6)
		Ui.text(ci, label, Vector2(9 - pad, 6.6), 5.5)
	sound_button.icon = Ui.Icon.SOUND_OFF if Assets.muted else Ui.Icon.SOUND_ON
	sound_button.draw(ci)
	cues_button.draw(ci)
	if cues_flash < 1.0:  # lessons reset: a tick on the button
		var p := cues_button.rect.get_center() + Vector2(0, -12)
		var a := 1.0 - maxf(0, cues_flash - 0.6) / 0.4
		ci.draw_circle(p, 5.5, Color(Ui.INK, a))
		ci.draw_circle(p, 4.7, Color(Ui.GOLD, a))
		ci.draw_polyline(PackedVector2Array([p + Vector2(-2.4, 0), p + Vector2(-0.6, 1.9), p + Vector2(2.6, -1.9)]), Color(Ui.INK, a), 1.2)


## Calm HUD: score (large, top centre, outlined, no card) and metres under it.
## Pause sits top trailing; power-up rings top leading and the boss bar only
## while active; the combo pill rides on the hero.
func _draw_hud(ci: CanvasItem) -> void:
	var cx := WIDTH / 2
	var size := 11.0 * (1.0 + 0.35 * maxf(0, 1 - score_pop / 0.25))
	Ui.text(ci, str(world.score), Vector2(cx, 2.5 - (size - 11.0) * 0.5), size,
		Ui.GOLD if score_pop < 0.25 else Color.WHITE, HORIZONTAL_ALIGNMENT_CENTER, Ui.INK, 0.2)
	Ui.text(ci, "%d m" % world.get_distance(), Vector2(cx, 15.5), 5, Color.WHITE, HORIZONTAL_ALIGNMENT_CENTER, Ui.INK, 0.22)

	# The lesson line: one short instruction on a dark pill, first lessons only.
	var lesson := coach.lesson_text()
	if lesson != "":
		var la := coach.line_alpha
		var lw := Ui.text_width(lesson, 7.5) + 14
		var boss_bar := world.scroller.boss.phase in [Boss.Phase.CHASE, Boss.Phase.DROP]
		var lr := Rect2(cx - lw / 2, 40 if boss_bar else 31, lw, 14)
		Ui.panel(ci, lr, Color(Ui.INK, 0.86 * la), 7, 0, 0)
		Ui.text(ci, lesson, Vector2(cx, lr.position.y + 2.6), 7.5, Color(1, 1, 1, la), HORIZONTAL_ALIGNMENT_CENTER, Color(0, 0, 0, 0))

	# Combo pill on the hero
	if world.combo >= 2:
		var label := "x%d" % mini(world.combo, GameWorld.MAX_COMBO_MULTIPLIER)
		var w := Ui.text_width(label, 5.5) + 6
		var hp := world.hero.position + Vector2(13, -6) - ui_view.position
		Ui.panel(ci, Rect2(hp.x - w / 2, hp.y, w, 9), Ui.GOLD, 4, 1, 1)
		Ui.text(ci, label, Vector2(hp.x, hp.y + 1.2), 5.5, Ui.INK, HORIZONTAL_ALIGNMENT_CENTER)

	# Active power-ups: icon with a draining ring
	var x := 10.0 - pad
	var effects := [[PowerUp.Kind.SHIELD, 1.0 if world.shield else 0.0],
		[PowerUp.Kind.SLOWMO, world.slowmo_time / GameWorld.SLOWMO_TIME],
		[PowerUp.Kind.DOUBLE, world.double_time / GameWorld.DOUBLE_TIME],
		[PowerUp.Kind.MAGNET, world.magnet_time / GameWorld.MAGNET_TIME]]
	for eff in effects:
		if eff[1] <= 0:
			continue
		var c := Vector2(x, 10)
		ci.draw_circle(c, 6.4, Ui.INK)
		ci.draw_circle(c, 5.6, Ui.powerup_color(eff[0]))
		Ui.powerup_icon(ci, eff[0], c, 6)
		ci.draw_arc(c, 7.6, -PI / 2, -PI / 2 + TAU * eff[1], 24, Color.WHITE, 1.2)
		x += 16

	# Boss health bar
	var b := world.scroller.boss
	if b.phase in [Boss.Phase.CHASE, Boss.Phase.DROP]:
		var bar := Rect2(cx - 40, 24, 80, 5)
		var danger := clampf((b.position.x + b.width - 40) / (Boss.DROP_AT - 40), 0, 1)
		var flash := danger > 0.75 and fmod(screen_time, 0.3) < 0.15
		Ui.panel(ci, bar.grow(1), Ui.RED if flash else Ui.INK, 3, 0, 1)
		ci.draw_rect(Rect2(bar.position, Vector2(bar.size.x * b.health / Boss.MAX_HEALTH, bar.size.y)), Color(0.75, 0.2, 0.25))
		if not boss_named:
			Ui.text(ci, "DEVOURER", Vector2(bar.get_center().x, bar.end.y + 1.5), 4.2, Color.WHITE, HORIZONTAL_ALIGNMENT_CENTER, Ui.INK, 0.22)


func _draw_banner(ci: CanvasItem) -> void:
	const DURATION := 1.6
	if banner_time > DURATION:
		return
	var t := banner_time
	var x := 0.0
	if t < 0.3:
		x = lerpf(WIDTH, 0, Ui.ease_out_back(t / 0.3))
	elif t > DURATION - 0.3:
		x = lerpf(0, -WIDTH, Ui.ease_in_out_quad((t - DURATION + 0.3) / 0.3))
	var w := Ui.text_width(banner_text, 11) + 24
	var r := Rect2(WIDTH / 2 - w / 2 + x, 36, w, 20)
	Ui.panel(ci, r, banner_color, 4, 1.2, 2)
	var fg := Color.WHITE if banner_color == Ui.RED else Ui.INK
	Ui.text(ci, banner_text, Vector2(r.get_center().x, r.position.y + 3.6), 11, fg, HORIZONTAL_ALIGNMENT_CENTER,
		Ui.INK if fg == Color.WHITE else Color.WHITE, 0.12)


func _draw_game_over(ci: CanvasItem) -> void:
	var t := game_over_time
	var total := world.total_score
	var drop := lerpf(-80, 0, Ui.ease_out_back(clampf(t / 0.5, 0, 1)))

	# Five things (principle 12): the total counting up, the HIGH SCORE stamp
	# on it, one stats line, Run again and Menu.
	ci.draw_rect(_full(ci), Color(0, 0, 0, 0.35 * clampf(t / 0.4, 0, 1)))
	var counted := roundi(total * Ui.ease_out_quad(clampf((t - 0.4) / 0.7, 0, 1)))
	var cx := WIDTH / 2
	Ui.text(ci, str(counted), Vector2(cx, 22 + drop * 0.3), 24, Color.WHITE, HORIZONTAL_ALIGNMENT_CENTER, Ui.INK, 0.16)
	if t > 0.5:
		# Numbers as chips, no sentence: metres, best combo, bosses beaten.
		var chips: Array = [["%d m" % world.get_distance(), Ui.PAPER]]
		if world.best_combo >= 2:
			chips.append(["x%d" % mini(world.best_combo, GameWorld.MAX_COMBO_MULTIPLIER), Ui.GOLD])
		if world.bosses_defeated > 0:
			chips.append(["%d" % world.bosses_defeated, Ui.RED])
		var widths: Array[float] = []
		var total_w := 0.0
		for c in chips:
			var w := Ui.text_width(c[0], 6) + (20.0 if c[1] == Ui.RED else 10.0)
			widths.append(w)
			total_w += w + 4
		var x := cx - (total_w - 4) / 2
		for i in chips.size():
			var r := Rect2(x, 56, widths[i], 12)
			Ui.panel(ci, r, chips[i][1], 6, 1, 1)
			var fg := Color.WHITE if chips[i][1] == Ui.RED else Ui.INK
			if chips[i][1] == Ui.RED:  # a small crown for each Devourer beaten
				var k := Vector2(r.position.x + 7, r.get_center().y)
				ci.draw_colored_polygon(PackedVector2Array([k + Vector2(-3.5, 2.5), k + Vector2(-3.5, -2), k + Vector2(-1.5, 0), k + Vector2(0, -3), k + Vector2(1.5, 0), k + Vector2(3.5, -2), k + Vector2(3.5, 2.5)]), Ui.GOLD)
				Ui.text(ci, chips[i][0], Vector2(r.position.x + 12.5, r.position.y + 2.6), 6, fg)
			else:
				Ui.text(ci, chips[i][0], Vector2(r.get_center().x, r.position.y + 2.6), 6, fg, HORIZONTAL_ALIGNMENT_CENTER, Color(0, 0, 0, 0))
			x += widths[i] + 4

	# "High score!" stamp slams onto the number.
	if world.state == GameWorld.State.HIGHSCORE and t > 1.1:
		var k := clampf((t - 1.1) / 0.25, 0, 1)
		var sc := lerpf(2.2, 1.0, Ui.ease_out_quad(k))
		var w := Ui.text_width(str(total), 24)
		ci.draw_set_transform(Vector2(cx + w / 2 + 10, 26), -0.25, Vector2(sc, sc))
		_hires(ci, Assets.highscore_mark, Rect2(-15, -14, 30, 28), Color(1, 1, 1, k))
		ci.draw_set_transform(Vector2.ZERO)
		if k >= 1.0 and not stamp_buzzed:
			stamp_buzzed = true
			_buzz(25)

	# Buttons fade in once input is accepted.
	if t > GAME_OVER_INPUT_DELAY:
		var a := clampf((t - GAME_OVER_INPUT_DELAY) / 0.25, 0, 1)
		replay_button.draw(ci, 1.0, a)
		menu_button.draw(ci, 1.0, a)


func _draw_pause(ci: CanvasItem) -> void:
	ci.draw_rect(_full(ci), Color(0, 0, 0, 0.55))
	Ui.panel(ci, Rect2(52, 14, 100, 94), Ui.PAPER, 6)
	Ui.text(ci, "PAUSED", Vector2(102, 20), 9, Ui.RED, HORIZONTAL_ALIGNMENT_CENTER)
	for b in [resume_button, restart_button, pause_menu_button]:
		b.draw(ci)
