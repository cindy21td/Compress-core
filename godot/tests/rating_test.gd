## Headless checks for the App Store rating ask (RatingPolicy, RatingAsk and
## the title-screen wiring in main.gd).
## Run: godot --headless --path . res://tests/rating_test.tscn
extends Node

const TEST_PATH := "user://rating_test.cfg"

var failures := 0


func _ready() -> void:
	_policy()
	_wrapper()
	await _wiring()
	DirAccess.remove_absolute(ProjectSettings.globalize_path(TEST_PATH))
	print("RESULT: ", "PASS" if failures == 0 else "FAIL (%d)" % failures)
	get_tree().quit(1 if failures else 0)


func _check(cond: bool, what: String) -> void:
	print(("ok   " if cond else "FAIL ") + what)
	if not cond:
		failures += 1


func _fresh() -> RatingPolicy:
	DirAccess.remove_absolute(ProjectSettings.globalize_path(TEST_PATH))
	return RatingPolicy.new(TEST_PATH)


## A policy past its first session, with nothing due.
func _second_session() -> RatingPolicy:
	var p := _fresh()
	p.begin_session()
	p = RatingPolicy.new(TEST_PATH)
	p.begin_session()
	return p


func _policy() -> void:
	var best := RatingPolicy.MIN_BEST
	var p := _fresh()
	_check(not p.asked and p.sessions == 0, "fresh install: never asked, no sessions")

	p.begin_session()
	p.note_run_end(best + 50, best, 1, false)
	_check(not p.should_ask(), "first session: even a boss win and a new best don't ask")

	p = RatingPolicy.new(TEST_PATH)
	_check(p.sessions == 1, "session count is saved")
	p.begin_session()
	p.note_run_end(best + 50, best, 1, true)
	_check(not p.should_ask(), "lessons still teaching: no ask")

	p = _second_session()
	p.note_run_end(best - 1, 0, 0, false)
	_check(not p.should_ask(), "new best under MIN_BEST: no ask")
	p.note_run_end(best + 5, best + 10, 0, false)
	_check(not p.should_ask(), "good score but not a new best: no ask")
	p.note_run_end(best + 10, best + 10, 0, false)
	_check(not p.should_ask(), "tying the best is not a new best")
	p.note_run_end(best, best - 1, 0, false)
	_check(p.should_ask(), "new best at MIN_BEST: ask due")

	p = _second_session()
	p.note_run_end(3, 500, 1, false)
	_check(p.should_ask(), "beating the Devourer: ask due even with a low score")

	p = _second_session()
	p.note_run_end(best + 10, best, 0, false)
	p = RatingPolicy.new(TEST_PATH)
	p.begin_session()
	_check(not p.should_ask(), "a due ask doesn't carry into the next launch")

	p = _second_session()
	p.note_run_end(best + 10, best, 0, false)
	p.mark_asked()
	_check(not p.should_ask(), "asked: nothing due")
	p.note_run_end(best + 100, best + 10, 2, false)
	_check(not p.should_ask(), "asked: a later good run doesn't ask again")
	p = RatingPolicy.new(TEST_PATH)
	p.begin_session()
	p.note_run_end(best + 200, best + 100, 3, false)
	_check(p.asked and not p.should_ask(), "asked is saved: never again on a later launch")


func _wrapper() -> void:
	_check(not RatingAsk.available(), "wrapper: unavailable off iOS / in debug builds")
	_check(not RatingAsk.request(), "wrapper: request() is a no-op here")
	# The macOS editor stub registers the same class as the iOS framework.
	_check(ClassDB.class_exists(RatingAsk.NATIVE_CLASS), "extension: %s is registered" % RatingAsk.NATIVE_CLASS)
	ClassDB.class_call_static(RatingAsk.NATIVE_CLASS, "request")
	_check(true, "extension: static request() callable")


## main.gd: a good run asks on the title reached from the score board, not on
## the score board, and not after a run that wasn't good.
func _wiring() -> void:
	var main: Node2D = load("res://main.tscn").instantiate()
	main.rating = _second_session()
	add_child(main)
	await get_tree().process_frame
	var w: GameWorld = main.world
	main.rating.sessions = RatingPolicy.MIN_SESSIONS  # begin_session ran again in _ready
	main.rating.save_state()

	main._start_run()
	# Lessons done (start_run reloads them from the saved prefs).
	main.coach.jump_taught = true
	main.coach.double_taught = true
	main.coach.summoner_taught = true
	main.coach.fire_taught = true
	w.state = GameWorld.State.HIGHSCORE  # the run just ended on a new best
	w.total_score = RatingPolicy.MIN_BEST + 20
	w.previous_best = RatingPolicy.MIN_BEST
	w.bosses_defeated = 0
	main.game_over_time = 5.0
	await _wait(1.2)
	_check(main.rating_tries == 0, "wiring: no ask on the score board")

	_tap_title(main)
	_check(main.screen == main.Screen.MENU, "wiring: Title goes to the title screen")
	_check(main.rating_tries == 0, "wiring: not the moment the title appears")
	await _wait(1.2)
	_check(main.rating_tries == 1, "wiring: asks once the title settles")
	_check(not main.rating.asked, "wiring: not marked asked when StoreKit wasn't reached")

	# A run that wasn't good: back to the title, nothing more.
	main.rating.due = false
	main._start_run()
	w.state = GameWorld.State.GAMEOVER
	w.total_score = 5
	w.previous_best = RatingPolicy.MIN_BEST
	main.game_over_time = 5.0
	_tap_title(main)
	await _wait(1.2)
	_check(main.rating_tries == 1, "wiring: a weak run doesn't ask")
	main.queue_free()
	await get_tree().process_frame


func _wait(seconds: float) -> void:
	await get_tree().create_timer(seconds, true, true).timeout


func _tap_title(main: Node2D) -> void:
	var at: Vector2 = main.menu_button.rect.get_center()
	main._touch_down(at)
	main._touch_up(at)
