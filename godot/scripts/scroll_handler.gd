## Owns the scrolling background and the enemy pool. Three enemies are on
## screen at a time; when one leaves, a random hidden one takes its place.
## Additions beyond the original: the Pig (unlocked later in a run), the
## Devourer boss, power-ups, and souls that keep flying after their enemy
## has left the screen (so they can reach the boss).
class_name ScrollHandler
extends RefCounted

enum RunningState { NORMAL, RUSH }

const SCROLL_SPEED := -59

var state := RunningState.NORMAL

var wizards: Array[Enemy] = []
var knights: Array[Enemy] = []
var summoner: Enemy
var pig: Enemy
var enemies: Array[Enemy] = []  # all eight, in draw order
var hidden: Array[Enemy] = []  # pool waiting to be shown

var bg_front: Background
var bg_back: Background

var boss := Boss.new()
var boss_cooldown := 0.0  # seconds before another boss can be summoned
var powerup := PowerUp.new()
var powerup_timer := 12.0
var pig_unlocked := false
var magnet := false  # souls home in on the boss

# Events for GameWorld to read after update() (cleared each update).
var boss_summoned := false
var boss_hits: Array[Vector2] = []
var boss_landed := false

const POWERUP_INTERVAL := Vector2(14, 24)
const BOSS_COOLDOWN := 25.0


func _init() -> void:
	var wizard_slots: Array[int] = [0, 1, 2, 3, 4, 5, 6]
	var knight_slots: Array[int] = wizard_slots.duplicate()

	for i in 3:
		wizards.append(Wizard.new(20, 20, SCROLL_SPEED, wizard_slots))
	for i in 3:
		knights.append(Knight.new(22, 22, SCROLL_SPEED - ran_vel_x(), knight_slots))
	summoner = Summoner.new(18, 18, SCROLL_SPEED + 10)
	pig = Pig.new()
	enemies.assign(wizards + knights + [summoner, pig])
	hidden = enemies.duplicate()
	for i in 3:
		show_random_enemy()

	bg_front = Background.new(0, 0, 204, 136, SCROLL_SPEED)
	bg_back = Background.new(204, 0, 204, 136, SCROLL_SPEED)


func show_random_enemy() -> void:
	var pool := hidden
	if not pig_unlocked:
		pool = hidden.filter(func(e): return e != pig)
	var e: Enemy = pool[randi_range(0, pool.size() - 1)]
	hidden.erase(e)
	e.set_is_visible(true)


func update(delta: float) -> void:
	bg_front.update(delta)
	bg_back.update(delta)
	if bg_front.is_scrolled_left:
		bg_front.reset_x(bg_back.tail_x())
	elif bg_back.is_scrolled_left:
		bg_back.reset_x(bg_front.tail_x())

	boss_summoned = false
	boss_hits.clear()
	boss_landed = false

	for e in enemies:
		_update_enemy(delta, e)
		if not e.is_visible and e.soul.is_visible:
			e.soul.update(delta)  # keep flying after the enemy leaves

	boss_cooldown = maxf(boss_cooldown - delta, 0.0)
	if boss.active():
		var was := boss.phase
		boss.update(delta)
		if was == Boss.Phase.DROP and boss.phase == Boss.Phase.LANDED:
			boss_landed = true
		for e in enemies:
			if boss.phase == Boss.Phase.CHASE and e.soul.is_visible:
				# Souls are drawn to the jaw; the magnet makes it a beeline.
				var pull := (boss.jaw_target() - e.soul.circle_center).normalized() * 150
				e.soul.velocity = pull if magnet else e.soul.velocity.lerp(pull, minf(1.0, 2.5 * delta))
			if boss.try_eat(e.soul):
				boss_hits.append(e.soul.circle_center)
		if not boss.active():
			boss_cooldown = BOSS_COOLDOWN

	powerup.update(delta)
	powerup_timer -= delta
	if powerup_timer <= 0 and not powerup.is_visible:
		powerup.spawn(randi() % 4 as PowerUp.Kind)
		powerup_timer = randf_range(POWERUP_INTERVAL.x, POWERUP_INTERVAL.y)


func _update_enemy(delta: float, e: Enemy) -> void:
	if not e.is_visible:
		return
	e.update(delta)
	if e.is_scrolled_left:
		match e.type:
			Enemy.Type.WIZARD:
				e.reset_enemy(0, false)
			Enemy.Type.SUMMONER:
				# An escaped Summoner calls in the Devourer.
				if e.alive and not boss.active() and boss_cooldown <= 0:
					boss.summon()
					boss_summoned = true
				e.reset_enemy(-10, false)
			_:
				e.reset_enemy(ran_vel_x(), false)
		hidden.append(e)
		show_random_enemy()


func stop() -> void:
	for e in enemies:
		e.stop()
	boss.stop()
	powerup.stop()
	bg_front.stop()
	bg_back.stop()


func on_restart() -> void:
	for w in wizards:
		w.reset_enemy(0, true)
	for k in knights:
		k.reset_enemy(ran_vel_x(), true)
	summoner.reset_enemy(-10, true)
	pig.reset_enemy(0, true)
	pig_unlocked = false
	boss.reset()
	boss_cooldown = 0.0
	powerup.is_visible = false
	powerup_timer = 12.0
	magnet = false
	hidden = enemies.duplicate()
	for i in 3:
		show_random_enemy()
	bg_front.on_restart()
	bg_back.on_restart()


## The enemy the hero stomped this frame, if any (at most one counts).
func stomped_enemy(hero: Hero) -> Enemy:
	for e in enemies:
		if e.is_hit(hero):
			return e
	return null


## Whatever would kill the hero this frame: an Enemy, the Boss, or null.
func colliding(hero: Hero) -> Object:
	for e in enemies:
		if e.collides(hero):
			return e
	if boss.collides(hero):
		return boss
	return null


func collides(hero: Hero) -> bool:
	return colliding(hero) != null


## Extra leftward speed for knights; much faster during a rush.
func ran_vel_x() -> float:
	if state == RunningState.RUSH:
		return randi_range(50, 71)
	return randi_range(10, 41)


func change_stage(new_state: RunningState) -> void:
	state = new_state
