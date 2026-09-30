## Screenshot tour of a long run with boss fights (not part of the test suite).
## Run: godot --path . res://tests/boss_tour.tscn -- <screenshot dir>
extends Node

var main: Node2D
var shot_dir := ""
var n := 0


func _ready() -> void:
	shot_dir = OS.get_cmdline_user_args()[0]
	main = load("res://main.tscn").instantiate()
	add_child(main)
	await _run()
	get_tree().quit()


func _wait(s: float) -> void:
	await get_tree().create_timer(s, true, true).timeout


func _shot(name: String) -> void:
	await RenderingServer.frame_post_draw
	n += 1
	get_viewport().get_texture().get_image().save_png(shot_dir.path_join("%02d_%s.png" % [n, name]))
	var w: GameWorld = main.world
	print("shot %02d_%s  %d m, score %d, boss phase %d hp %d pos %s" % [n, name, w.get_distance(), w.score, w.scroller.boss.phase, w.scroller.boss.health, w.scroller.boss.position])


func _autoplay() -> void:
	var w: GameWorld = main.world
	for e in w.scroller.enemies:
		if e.is_visible and e.alive and e.position.x > 32 and e.position.x < 58 and not w.hero.jumped:
			main._touch_down(Vector2(-1, -1))
			main._touch_up(Vector2(-1, -1))
			return


func _play(seconds: float, until := func(): return false) -> void:
	var t := 0.0
	while t < seconds and not until.call():
		_autoplay()
		await _wait(0.05)
		t += 0.05


func _feed_soul() -> void:
	var w: GameWorld = main.world
	var e: Enemy = w.scroller.wizards[0]
	if not e.soul.is_visible:
		e.soul.position = w.hero.position
		e.soul.velocity = Vector2(-59, -120)
		e.soul.is_visible = true


func _summon() -> void:
	var s: ScrollHandler = main.world.scroller
	s.boss.reset()
	s.boss_cooldown = 0
	s.hidden.erase(s.summoner)
	s.summoner.alive = true
	s.summoner.is_visible = true
	s.summoner.position.x = -80
	await _play(0.2)


func _run() -> void:
	var w: GameWorld = main.world
	var s := w.scroller
	await _wait(2.5)
	main._touch_down(Vector2(100, 60)); main._touch_up(Vector2(100, 60))
	await _wait(0.5)
	main._touch_down(Vector2(100, 60)); main._touch_up(Vector2(100, 60))
	w.invincible = true
	s.boss_cooldown = 999  # no surprise bosses before the scripted ones
	await _play(12.0)
	await _shot("run")

	# Fight 1: the player wins.
	await _summon()
	var t := 0.0
	var shots := {1.5: "boss_arrives", 4.5: "boss_fight", 8.0: "boss_fight", 11.0: "boss_fight"}
	while s.boss.phase == Boss.Phase.CHASE and t < 30.0:
		if fmod(t, 2.0) < 0.05:
			_feed_soul()  # on top of the autoplay's own stomps
		await _play(0.05)
		t += 0.05
		for at in shots:
			if t >= at and t < at + 0.05:
				await _shot(shots[at])
	await _play(0.3)
	await _shot("boss_defeated")
	await _play(0.6)
	await _shot("boss_retreats")
	s.boss_cooldown = 999

	await _play(14.0)
	await _shot("run_later_stage")

	# Fight 2: nobody stops it and it drops.
	await _summon()
	await _play(3.0)
	await _shot("boss_2_arrives")
	# Let this one get through: no souls reach it.
	await _play(20.0, func():
		for e in s.enemies:
			e.soul.is_visible = false
		return s.boss.phase != Boss.Phase.CHASE)
	await _play(0.15)
	await _shot("boss_2_drops")
	await _play(1.0, func(): return s.boss.phase == Boss.Phase.LANDED)
	await _play(0.1)
	await _shot("boss_2_slam")

	# A third one, played for real: no invincibility.
	await _play(3.0)
	await _summon()
	w.invincible = false
	await _play(30.0, func(): return not w.hero.alive)
	await _play(0.1)
	await _shot("death")
	await _wait(2.0)
	await _shot("game_over")
