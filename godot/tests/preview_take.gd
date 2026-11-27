## App Store preview take (not part of the test suite or the export; tests/* is excluded).
## Plays the real game with scripted taps: a short run-in, the Devourer summoned and
## left to creep in close (pre-roll, cut away), then knight stomps through the fight until it goes down, then more stomps.
## Record with Movie Maker at the iPhone 6.9" preview size, game audio included:
##   Godot --path . --write-movie <dir>/f.png --fixed-fps 30 --resolution 1920x886 res://tests/preview_take.tscn
## Prints "mark <name> <frame>" so the cut can be made on exact frames
## (see docs/release-2026-11/preview-storyboard.md).
extends Node

var main: Node2D
var frame := 0


func _ready() -> void:
	main = load("res://main.tscn").instantiate()
	add_child(main)
	seed(1127)
	await _run()
	get_tree().quit()


func _process(_d: float) -> void:
	frame += 1


func _mark(name: String) -> void:
	var w: GameWorld = main.world
	print("mark %s %d  %d m score %d boss phase %d hp %d" % [name, frame, w.get_distance(), w.score, w.scroller.boss.phase, w.scroller.boss.health])


func _wait(s: float) -> void:
	await get_tree().create_timer(s, true, true).timeout


func _tap() -> void:
	main._touch_down(Vector2(-1, -1))
	main._touch_up(Vector2(-1, -1))


## Jump when an enemy is in stomping range (same rule as the boss tour).
func _autoplay() -> void:
	var w: GameWorld = main.world
	for e in w.scroller.enemies:
		if e.is_visible and e.alive and e.position.x > 32 and e.position.x < 58 and not w.hero.jumped:
			_tap()
			return


func _play(seconds: float, until := func(): return false) -> void:
	var t := 0.0
	while t < seconds and not until.call():
		_autoplay()
		await _wait(0.05)
		t += 0.05


func _run() -> void:
	var w: GameWorld = main.world
	var s := w.scroller
	await _wait(0.3)
	main._start_run()
	# Lessons are for a first run; the preview shows a player who knows the game.
	main.coach.jump_taught = true
	main.coach.double_taught = true
	main.coach.summoner_taught = true
	w.invincible = true  # scripted taps miss now and then; keep the take going
	s.boss_cooldown = 999
	_mark("run_start")
	await _play(4.0)

	# The Summoner walks in and calls the Devourer.
	s.boss.reset()
	s.boss_cooldown = 0
	s.hidden.erase(s.summoner)
	s.summoner.alive = true
	s.summoner.is_visible = true
	s.summoner.position.x = -80
	_mark("summoner_in")
	await _play(12.0, func(): return s.boss.phase == Boss.Phase.CHASE)
	_mark("boss_chase")
	# Pre-roll (cut away): no souls reach it for ~5 s, so the Devourer has crept
	# in close when the clip opens. From the cut on, every stomp's soul lands.
	await _play(4.0, func():
		for e in s.enemies:
			e.soul.is_visible = false
		return false)
	_mark("clip_in")
	var t := 0.0
	while s.boss.phase == Boss.Phase.CHASE and t < 25.0:
		await _play(0.05)
		t += 0.05
	_mark("boss_down")
	s.boss_cooldown = 999
	await _play(6.0)
	_mark("end")
