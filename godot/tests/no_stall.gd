## No lesson can stall: a player who never taps after the title, and one who
## only ever single-taps, both reach the free run with the hero alive.
## Run: godot --headless --fixed-fps 60 --path . res://tests/no_stall.tscn
extends Node

var main: Node2D
var failures := 0


func _ready() -> void:
	main = load("res://main.tscn").instantiate()
	add_child(main)
	await get_tree().create_timer(0.5).timeout
	for single_taps in [false, true]:
		await _play(single_taps)
	print("RESULT: ", "PASS" if failures == 0 else "FAIL (%d)" % failures)
	get_tree().quit(1 if failures else 0)


func _play(single_taps: bool) -> void:
	for f in Coach.FLAGS:
		Assets.set_pref(f, 0)
	main._start_run()
	var c: Coach = main.coach
	var w: GameWorld = main.world
	var t := 0.0
	var tap_t := 0.0
	var seen: Array = []
	while c.phase() != Coach.Phase.FREE and t < 120.0 and w.hero.alive:
		await get_tree().process_frame
		var d := get_process_delta_time()
		t += d
		if not seen.has(c.phase()):
			seen.append(c.phase())
		tap_t += d
		if single_taps and tap_t > 1.1 and not w.hero.jumped:
			tap_t = 0.0
			main._touch_down(Vector2(-1, -1))
			main._touch_up(Vector2(-1, -1))
	var who := "single-tapper" if single_taps else "non-tapper"
	var ok := c.phase() == Coach.Phase.FREE and w.hero.alive
	print(("ok   " if ok else "FAIL ") + "%s reaches the free run (%.0f s, phases %s, alive %s)" % [who, t, str(seen), w.hero.alive])
	if not ok:
		failures += 1
	main._to_menu()
	await get_tree().create_timer(0.5).timeout
