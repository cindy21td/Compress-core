## Automated smoke test: plays through splash, menu, a run, death and replay.
## Run: godot --path . res://tests/playtest.tscn [-- <screenshot dir>]
extends Node

var main: Node2D
var failures := 0
var shot_dir := ""


func _ready() -> void:
	var args := OS.get_cmdline_user_args()
	shot_dir = args[0] if args.size() > 0 else ""
	main = load("res://main.tscn").instantiate()
	add_child(main)
	await _run()
	print("RESULT: ", "PASS" if failures == 0 else "FAIL (%d)" % failures)
	get_tree().quit(1 if failures else 0)


func _check(cond: bool, what: String) -> void:
	print(("ok   " if cond else "FAIL ") + what)
	if not cond:
		failures += 1


func _wait(seconds: float) -> void:
	await get_tree().create_timer(seconds, true, true).timeout


func _shot(name: String) -> void:
	if shot_dir == "":
		return
	await RenderingServer.frame_post_draw
	get_viewport().get_texture().get_image().save_png(shot_dir.path_join(name + ".png"))


## A real mouse click, so the screen-to-world transform is exercised too.
func _click(world_pos: Vector2) -> void:
	var screen_pos: Vector2 = get_viewport().get_screen_transform() * get_viewport().get_canvas_transform() * world_pos
	for pressed in [true, false]:
		var ev := InputEventMouseButton.new()
		ev.button_index = MOUSE_BUTTON_LEFT
		ev.pressed = pressed
		ev.position = screen_pos
		ev.global_position = screen_pos
		Input.parse_input_event(ev)
		await get_tree().process_frame
		await get_tree().process_frame


func _run() -> void:
	var w: GameWorld = main.world
	_check(Assets._sounds.size() == 4, "all four sounds loaded (%s)" % ", ".join(Assets._sounds.keys()))
	_check(Assets.font is FontFile, "font loaded")
	_check(main.screen == main.Screen.SPLASH, "starts on splash")
	await _wait(1.0)
	await _shot("1_splash")
	await _wait(1.3)
	_check(main.screen == main.Screen.MENU, "menu shown after splash")
	await _shot("2_menu")

	await _click(Vector2(100, 60))
	_check(main.screen == main.Screen.GAME and w.state == GameWorld.State.READY, "tap on menu -> ready")
	await _wait(1.6)
	await _shot("3_ready")

	await _click(Vector2(100, 60))
	_check(w.state == GameWorld.State.RUNNING, "tap -> running")
	_check(not w.hero.action_disabled, "first tap only enables control")

	await _click(Vector2(100, 60))
	_check(w.hero.jumped, "tap -> jump")
	await _wait(0.15)
	await _shot("4_jump")
	await _wait(1.5)
	_check(not w.hero.jumped and is_equal_approx(w.hero.position.y, 104.0), "hero lands on ground")
	_check(w.get_distance() > 0, "distance increases (%d)" % w.get_distance())

	# Play until the hero dies (jumping whenever an enemy gets close).
	var elapsed := 0.0
	var peak_score := 0
	while w.state == GameWorld.State.RUNNING and elapsed < 90.0:
		for e in w.scroller.enemies:
			if e.is_visible and e.alive and e.position.x > 30 and e.position.x < 60 and not w.hero.jumped:
				main._touch_down(Vector2(-1, -1))
				main._touch_up(Vector2(-1, -1))
		peak_score = maxi(peak_score, w.score)
		await _wait(0.05)
		elapsed += 0.05
		if int(elapsed * 20) == 100:
			await _shot("5_running")
	print("     survived %.1fs, score %d, distance %d" % [elapsed, w.score, w.get_distance()])
	_check(w.is_game_over(), "hero eventually dies")
	_check(not w.hero.alive, "hero marked dead")
	await _wait(0.5)
	await _shot("6_game_over")

	var total := w.total_score
	_check(total == floori(w.distance / 200.0) + w.score, "total score formula")
	_check(Assets.get_pref("highScore") >= total, "high score saved")

	var b: SimpleButton = main.replay_button
	await _click(b.rect.get_center())
	_check(w.state == GameWorld.State.READY, "replay button -> ready")
	_check(w.score == 0 and w.distance == 0 and w.hero.alive, "state reset on replay")
	var visible := 0
	for e in w.scroller.enemies:
		if e.is_visible:
			visible += 1
	_check(visible == 3, "three enemies active after restart")
