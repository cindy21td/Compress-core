## Automated smoke test and screenshot tour.
## Run: godot --path . res://tests/playtest.tscn [-- <screenshot dir>]
## Plays through the menu, a tutorial start, pause, a long invincible demo run
## (to capture later stages), the Pig, a power-up, two Devourer boss fights,
## a real death, the score screen, replay and menu.
extends Node

var main: Node2D
var failures := 0
var shot_dir := ""
var shot_locale := "en"


func _ready() -> void:
	var args := OS.get_cmdline_user_args()
	shot_dir = args[0] if args.size() > 0 else ""
	shot_locale = args[1] if args.size() > 1 else "en"  # second arg: screens in another language
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
	_check(Assets._sounds.size() == Assets.SOUND_FILES.size(), "all sounds loaded")
	_check(main.screen == main.Screen.SPLASH, "starts on splash")
	_fit_check()
	await _wait(1.0)
	await _shot("01_splash")
	await _wait(1.3)
	_check(main.screen == main.Screen.MENU, "menu shown after splash")
	await _shot("02_menu")

	var muted: bool = Assets.muted
	await _click(main.sound_button.rect.get_center() + main.ui_view.position)
	_check(Assets.muted != muted, "sound button toggles mute")
	await _click(main.sound_button.rect.get_center() + main.ui_view.position)
	_check(Assets.muted == muted, "sound button toggles back")

	# Lessons replay from the title's "?" button.
	await _click(main.cues_button.rect.get_center() + main.ui_view.position)
	_check(not main.coach.jump_taught and not main.coach.double_taught and Assets.get_pref("taughtJump") == 0, "? button replays the lessons")
	await _click(Vector2(100, 60))
	_check(main.screen == main.Screen.GAME and w.state == GameWorld.State.RUNNING, "tap on title -> running (no ready card)")
	await _teaching()

	w.invincible = true  # random enemies must not end the scripted steps early
	await _click(Vector2(100, 60))
	_check(w.hero.jumped, "tap -> jump")
	await _wait(0.12)
	await _shot("04_jump")
	await _wait(1.5)
	_check(not w.hero.jumped and is_equal_approx(w.hero.position.y, 104.0), "hero lands on ground")

	await _click(main.pause_button.rect.get_center() + main.ui_view.position)
	_check(main.paused, "pause button pauses")
	var d := w.distance
	await _wait(0.3)
	_check(w.distance == d, "world frozen while paused")
	await _shot("05_paused")
	await _click(main.resume_button.rect.get_center() + main.ui_view.position)
	_check(not main.paused, "resume button resumes")
	main.propagate_notification(Node.NOTIFICATION_APPLICATION_PAUSED)
	_check(main.paused, "leaving the app pauses the run")
	await _click(main.resume_button.rect.get_center() + main.ui_view.position)
	_check(not main.paused, "resume after returning")

	# Long demo run: still invincible, so it reaches later stages.
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

	await _gameplay_extras()

	# Now play for real until the hero dies, starting with a shield.
	var broke := [false]
	w.shield_broken.connect(func(): broke[0] = true)
	w._collect(PowerUp.Kind.SHIELD)
	w.invincible = false
	t = 0.0
	while w.state == GameWorld.State.RUNNING and t < 90.0:
		_autoplay()
		await _wait(0.05)
		t += 0.05
	_check(w.is_game_over(), "hero eventually dies")
	_check(main.killer_name != "", "game over names the killer (%s)" % main.killer_name)
	_check(broke[0], "shield absorbed a hit before death")
	await _wait(0.3)
	await _shot("10_game_over_anim")
	await _wait(1.6)
	await _shot("11_game_over")
	_check(w.total_score == floori(w.distance / 200.0) + w.score, "total score formula")
	_check(Assets.get_pref("highScore") >= w.total_score, "high score saved")

	await _click(main.replay_button.rect.get_center() + main.ui_view.position)
	_check(w.state == GameWorld.State.RUNNING, "replay button -> running")
	_check(w.score == 0 and w.distance < 30 and w.hero.alive, "state reset on replay (run restarts at once)")
	_check(main.backdrop.stage == 0, "back to first stage")
	var visible := 0
	for e in w.scroller.enemies:
		if e.is_visible:
			visible += 1
	_check(visible == 3, "three enemies active after restart")

	await _click(main.pause_button.rect.get_center() + main.ui_view.position)
	await _click(main.pause_menu_button.rect.get_center() + main.ui_view.position)
	_check(main.screen == main.Screen.MENU and w.state == GameWorld.State.MENU, "pause -> menu returns to title")
	await _wait(0.3)
	await _shot("12_menu_with_best")


## W5: the run teaches itself. The first enemy slows the world and walks past
## harmlessly with a miss drawn; the first stomp is the jump lesson; a tap at
## the top of a jump is the double-jump lesson. Nothing waits for the player.
func _teaching() -> void:
	var w: GameWorld = main.world
	var c: Coach = main.coach
	var t := 0.0
	while not c.slow(w) and t < 8.0:
		await _wait(0.05)
		t += 0.05
	_check(c.slow(w) and Engine.time_scale < 0.6, "first enemy slows the world (%.2f)" % Engine.time_scale)
	_check(c.jump_cue(w), "ring cue on the hero before the first enemy")
	await _shot("03_jump_cue")
	t = 0.0
	while c.spared == 0 and t < 10.0:
		await _wait(0.05)
		t += 0.05
	_check(c.spared == 1 and w.hero.alive and w.state == GameWorld.State.RUNNING, "untapped enemy walks past harmlessly")
	_check(c.miss_time < 0.3, "the miss is drawn")
	_check(c.miss_word == "Tap sooner", "the miss says why (%s)" % c.miss_word)
	_check(c.line.begins_with("Tap to jump") and c.lesson_text() == "", "Tap to jump stays the lesson; it steps aside while the miss speaks")
	await _wait(0.15)
	await _shot("03b_missed")
	var back := 0.0
	while c.lesson_text() == "" and back < 3.0:
		await _wait(0.05)
		back += 0.05
	_check(c.lesson_text() != "" and c.line.begins_with("Tap to jump") and c.miss_time < 1.2, "the lesson line is back within a second (%s, %.2f s)" % [c.lesson_text(), c.miss_time])
	w.invincible = true
	t = 0.0
	while not c.jump_taught and t < 20.0:
		_autoplay()
		await _wait(0.03)
		t += 0.03
	_check(c.jump_taught and Assets.get_pref("taughtJump") == 1, "first stomp teaches the jump (saved)")
	_check(c.arc_time < 0.5 and c.shown_arc.size() > 2, "first stomp draws its arc")  # 0.5: frame timing on a loaded machine
	await _shot("03c_first_stomp")
	t = 0.0
	while not c.double_cue(w) and t < 15.0:
		if not w.hero.jumped and c.arc_time > 1.0:
			main._touch_down(Vector2(-1, -1))
			main._touch_up(Vector2(-1, -1))
		await _wait(0.03)
		t += 0.03
	_check(c.double_cue(w), "double-jump ring at the top of a jump")
	_check(c.slow(w), "the double-jump demo runs slowed")
	_check(c.line == "Watch: double jump" and not c.double_demoed, "the first cue only demonstrates, labelled (%s)" % c.line)
	await _wait(0.12)
	await _shot("03d0_double_demo")
	t = 0.0
	while not (c.double_cue(w) and c.double_demoed) and t < 15.0:
		if not w.hero.jumped and c.arc_time > 1.0:
			main._touch_down(Vector2(-1, -1))
			main._touch_up(Vector2(-1, -1))
		await _wait(0.03)
		t += 0.03
	await _wait(0.1)
	var wt := 0.0
	while c.double_cue(w) and c.lesson_text() == "" and wt < 0.6:  # a tick from a random stomp may be speaking
		await _wait(0.03)
		wt += 0.03
	_check(c.double_cue(w) and c.lesson_text() == tr("Tap again to go higher"), "the next jump asks, in the air (%s|%s|%.2f|%.2f)" % [c.lesson_text(), c.line, c.miss_time, c.tick_time])
	await _shot("03d_double_cue")
	main._touch_down(Vector2(-1, -1))
	main._touch_up(Vector2(-1, -1))
	_check(c.double_taught and Assets.get_pref("taughtDouble") == 1, "first double jump taught (saved)")
	_check(not c.slow(w) and not c.jump_cue(w), "no jump cue once learned")
	await _wait(1.5)
	# The first wizard: its fireball is ringed and slowed before it can kill.
	var s := w.scroller
	var wz: Wizard = s.wizards[0]
	c.fire_taught = false  # a random wizard may have taught it during the demo
	Assets.set_pref("taughtFire", 0)
	s.hidden.erase(wz)
	wz.reset_enemy(0, false)
	wz.set_is_visible(true)
	wz.position.x = 150
	t = 0.0
	while c.fire_ball(w) == null and t < 4.0:
		await _wait(0.05)
		t += 0.05
	_check(c.fire_ball(w) != null and w.fire_harmless, "first wizard's fireball is the fire lesson")
	t = 0.0
	while not c.slow(w) and t < 6.0:
		await _wait(0.03)
		t += 0.03
	_check(c.slow(w) and c.lesson_text() == tr("Jump the fire"), "fireball slows the world and says Jump the fire (%s)" % c.lesson_text())
	await _shot("03e_fire_lesson")
	t = 0.0
	while not c.fire_taught and t < 8.0:
		await _wait(0.05)
		t += 0.05
	_check(c.fire_taught and Assets.get_pref("taughtFire") == 1, "fire lesson learned once the fireball passes (saved)")
	await _wait(1.0)
	w.invincible = false


func _play_for(seconds: float, until := func(): return false) -> float:
	var t := 0.0
	while t < seconds and not until.call():
		_autoplay()
		await _wait(0.05)
		t += 0.05
	return t


func _gameplay_extras() -> void:
	var w: GameWorld = main.world
	var s := w.scroller
	_check(w.best_combo >= 1, "combo tracked (best %d)" % w.best_combo)

	# The Pig unlocks at 150 m.
	_check(s.pig_unlocked, "pig unlocked past %d m" % GameWorld.PIG_UNLOCK_DISTANCE)
	if not s.pig.is_visible:
		s.pig.reset_enemy(0, false)
		s.pig.is_visible = true
	s.pig.position.x = 150
	await _play_for(0.6)
	await _shot("13_pig")
	await _play_for(5.0, func(): return not s.pig.is_visible or not s.pig.alive)

	# Magnet power-up, placed just ahead of the hero.
	s.powerup.spawn(PowerUp.Kind.MAGNET)
	await _play_for(3.0, func(): return not w.hero.jumped)
	s.powerup.position = w.hero.body_center + Vector2(30, -6)
	await _wait(0.1)
	await _shot("14_powerup")
	await _wait(0.4)
	_check(w.magnet_time > 0, "magnet collected")

	# Boss fight: stomped souls home in on the jaw and beat it back.
	var events := {"summoned": 0, "hits": 0, "defeated": 0, "slammed": 0, "stomps": 0}
	w.stomped.connect(func(_e, _p, _c): events.stomps += 1)
	w.boss_summoned.connect(func(): events.summoned += 1)
	w.boss_hit.connect(func(_p): events.hits += 1)
	w.boss_defeated.connect(func(): events.defeated += 1)
	w.boss_slammed.connect(func(): events.slammed += 1)
	var score := w.score
	s.boss.reset()  # in case the demo run already called one
	await _play_for(0.1)
	s.boss_cooldown = 0
	s.hidden.erase(s.summoner)
	s.summoner.alive = true
	s.summoner.is_visible = true
	s.summoner.position.x = -80  # an escaping Summoner calls the boss
	await _play_for(0.2)
	_check(events.summoned == 1 and s.boss.phase == Boss.Phase.CHASE, "escaped summoner summons the boss")
	_check(Assets.current_music == "boss", "boss music plays")
	await _play_for(5.0)
	await _shot("15_boss")
	# Release souls from the hero until it's beaten (autoplay stomps too rarely
	# to be deterministic).
	var t := 0.0
	while s.boss.phase == Boss.Phase.CHASE and t < 20.0:
		var e: Enemy = s.wizards[0]
		if not e.soul.is_visible:
			e.soul.position = w.hero.position
			e.soul.velocity = Vector2(-59, -120)
			e.soul.is_visible = true
		await _play_for(0.3)
		t += 0.3
	print("     boss: %d stomps, %d hits, health %d" % [events.stomps, events.hits, s.boss.health])
	_check(events.hits > 0 and w.score > score, "souls hurt the boss and score")
	_check(events.defeated == 1 and s.boss.defeated, "boss beaten back")

	# Second boss: let it reach mid-screen so it drops.
	await _play_for(8.0, func(): return not s.boss.active())
	s.boss_cooldown = 0
	var slams: int = events.slammed
	s.boss.summon()
	s.boss.position.x = Boss.DROP_AT - Boss.W - 1
	await _play_for(3.0, func():
		for e in s.enemies:
			e.soul.is_visible = false  # no hits, so it gets through
		return s.boss.phase == Boss.Phase.LANDED)
	await _shot("16_boss_drop")
	_check(events.slammed == slams + 1, "boss drops and slams the ground")
	await _play_for(6.0, func(): return not s.boss.active())
	_check(not s.boss.active(), "boss leaves after landing")
	await _play_for(0.5)
	_check(Assets.current_music == "theme", "theme music returns")


## Every translation fits its box at the longest language (checked per locale).
func _fit_check() -> void:
	var cols := ["en", "es", "pt_BR", "fr", "de", "ja", "ko", "zh_Hans"]
	var lessons := ["Tap to jump", "Tap to jump on it", "Land on it", "Watch: double jump", "Tap again to go higher",
		"Jump the fire", "Stop the ghost"]
	var words := ["Tap sooner", "Land on top", "Landed on it", "Stopped it", "Double jump", "Cleared it", "Jump the fire"]
	var killers := ["Hit by a wizard", "Hit by a knight", "Hit by a ghost", "Hit by a pig", "Caught by the Devourer"]
	var banners := ["STAGE %d", "RUSH", "THE DEVOURER", "DEVOURER DOWN +%d", "SHIELD", "SLOW-MO", "DOUBLE POINTS", "SOUL MAGNET"]
	var bad := []
	var widest := {}
	for loc in cols:
		TranslationServer.set_locale(loc)
		var tx := func(k: String) -> String: return String(TranslationServer.translate(k))
		_check(loc == "en" or tx.call("Tap to jump") != "Tap to jump", "translation loaded for %s" % loc)
		for k in lessons:
			if Ui.text_width(tx.call(k), 7.5) + 14 > main.WIDTH - 24: bad.append([loc, k])
		for k in words:
			if Ui.text_width(tx.call(k), 8) + 12 > main.WIDTH - 40: bad.append([loc, k])
		for k in killers:
			var hint := "Stomp to push it back" if k == "Caught by the Devourer" else ("Jump the fire" if k == "Hit by a wizard" else "Jump on it")
			var lw := maxf(Ui.text_width(tx.call(k), 7.5), Ui.text_width(tx.call(hint), 6.5)) + 14
			widest[loc] = maxf(widest.get(loc, 0.0), lw)
			if lw > main.WIDTH - 8: bad.append([loc, k])
		for k in banners:
			if Ui.text_width(tx.call(k) % 2 if "%d" in k else tx.call(k), 11) + 24 > main.WIDTH - 8: bad.append([loc, k])
		for b in [main.replay_button, main.menu_button, main.resume_button, main.restart_button, main.pause_menu_button]:
			var h: float = b.rect.size.y
			if Ui.text_width(tx.call(b.label), h * 0.42) + h * 0.6 > b.rect.size.x - 4: bad.append([loc, b.label])
		if Ui.text_width(tx.call("TAP TO START"), 9.0 * 1.04) + 19 > main.WIDTH - 16: bad.append([loc, "TAP TO START"])
		if Ui.text_width(tx.call("PAUSED"), 9) > 96: bad.append([loc, "PAUSED"])
	TranslationServer.set_locale(shot_locale)
	print("     widest killer pill per locale: ", widest)
	_check(bad.is_empty(), "every string fits at every locale %s" % str(bad))
