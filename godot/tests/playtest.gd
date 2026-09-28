## Automated smoke test and screenshot tour.
## Run: godot --path . res://tests/playtest.tscn [-- <screenshot dir>]
## Plays through the menu, a tutorial start, pause, a long invincible demo run
## (to capture later stages), a real death, the score screen, replay and menu.
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
	print("     shot ", name)


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


## Jumps when an enemy gets close; stomps plenty, dies eventually.
func _autoplay() -> void:
	var w: GameWorld = main.world
	for e in w.scroller.enemies:
		if e.is_visible and e.alive and e.position.x > 32 and e.position.x < 58 and not w.hero.jumped:
			main._touch_down(Vector2(-1, -1))
			main._touch_up(Vector2(-1, -1))
			return


func _run() -> void:
	var w: GameWorld = main.world
	_check(Assets._sounds.size() == 4, "all four sounds loaded")
	_check(main.screen == main.Screen.SPLASH, "starts on splash")
	await _wait(1.0)
	await _shot("01_splash")
	await _wait(1.3)
	_check(main.screen == main.Screen.MENU, "menu shown after splash")
	await _shot("02_menu")

	var muted: bool = Assets.muted
	await _click(main.sound_button.rect.get_center())
	_check(Assets.muted != muted, "sound button toggles mute")
	await _click(main.sound_button.rect.get_center())
	_check(Assets.muted == muted, "sound button toggles back")

	await _click(Vector2(100, 60))
	_check(main.screen == main.Screen.GAME and w.state == GameWorld.State.READY, "tap on menu -> ready")
	await _wait(1.6)
	await _shot("03_tutorial")

	await _click(Vector2(100, 60))
	_check(w.state == GameWorld.State.RUNNING, "tap -> running")
	await _click(Vector2(100, 60))
	_check(w.hero.jumped, "tap -> jump")
	await _wait(0.12)
	await _shot("04_jump")
	await _wait(1.5)
	_check(not w.hero.jumped and is_equal_approx(w.hero.position.y, 104.0), "hero lands on ground")

	await _click(main.pause_button.rect.get_center())
	_check(main.paused, "pause button pauses")
	var d := w.distance
	await _wait(0.3)
	_check(w.distance == d, "world frozen while paused")
	await _shot("05_paused")
	await _click(main.resume_button.rect.get_center())
	_check(not main.paused, "resume button resumes")

	# Long demo run: invincible so it reaches later stages.
	w.invincible = true
	var shots := {6.0: "06_running", 24.0: "07_stage2", 44.0: "09_stage3"}
	var rush_shot := false
	var was_rush := true  # ignore a rush already underway
	var t := 0.0
	while t < 46.0:
		_autoplay()
		await _wait(0.05)
		t += 0.05
		for at in shots:
			if t >= at and t < at + 0.05:
				await _shot(shots[at])
		var rush := w.scroller.state == ScrollHandler.RunningState.RUSH
		if rush and not was_rush and not rush_shot:
			await _wait(0.45)
			await _shot("08_rush")
			rush_shot = true
		was_rush = rush
	print("     demo run: %.0fs, %d stomps, %d m, stage %d" % [t, w.score, w.get_distance(), main.backdrop.stage + 1])
	_check(w.score > 0, "stomps scored during demo")
	_check(main.backdrop.stage >= 2, "stages advance with distance")

	# Now play for real until the hero dies.
	w.invincible = false
	t = 0.0
	while w.state == GameWorld.State.RUNNING and t < 90.0:
		_autoplay()
		await _wait(0.05)
		t += 0.05
	_check(w.is_game_over(), "hero eventually dies")
	await _wait(0.3)
	await _shot("10_game_over_anim")
	await _wait(1.6)
	await _shot("11_game_over")
	_check(w.total_score == floori(w.distance / 200.0) + w.score, "total score formula")
	_check(Assets.get_pref("highScore") >= w.total_score, "high score saved")

	await _click(main.replay_button.rect.get_center())
	_check(w.state == GameWorld.State.READY, "replay button -> ready")
	_check(w.score == 0 and w.distance == 0 and w.hero.alive, "state reset on replay")
	_check(main.backdrop.stage == 0, "back to first stage")
	var visible := 0
	for e in w.scroller.enemies:
		if e.is_visible:
			visible += 1
	_check(visible == 3, "three enemies active after restart")

	await _click(Vector2(100, 60))
	await _click(main.pause_button.rect.get_center())
	await _click(main.pause_menu_button.rect.get_center())
	_check(main.screen == main.Screen.MENU and w.state == GameWorld.State.MENU, "pause -> menu returns to title")
	await _wait(0.3)
	await _shot("12_menu_with_best")
