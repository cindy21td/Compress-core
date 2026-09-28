## Owns the scrolling background and the enemy pool. Three enemies are on
## screen at a time; when one leaves, a random hidden one takes its place.
class_name ScrollHandler
extends RefCounted

enum RunningState { NORMAL, RUSH }

const SCROLL_SPEED := -59

var state := RunningState.NORMAL

var wizards: Array[Enemy] = []
var knights: Array[Enemy] = []
var summoner: Enemy
var enemies: Array[Enemy] = []  # all seven, in draw order
var hidden: Array[Enemy] = []  # pool waiting to be shown

var bg_front: Background
var bg_back: Background


func _init() -> void:
	var wizard_slots: Array[int] = [0, 1, 2, 3, 4, 5, 6]
	var knight_slots: Array[int] = wizard_slots.duplicate()

	for i in 3:
		wizards.append(Wizard.new(20, 20, SCROLL_SPEED, wizard_slots))
	for i in 3:
		knights.append(Knight.new(22, 22, SCROLL_SPEED - ran_vel_x(), knight_slots))
	summoner = Summoner.new(18, 18, SCROLL_SPEED + 10)
	enemies.assign(wizards + knights + [summoner])
	hidden = enemies.duplicate()
	for i in 3:
		show_random_enemy()

	bg_front = Background.new(0, 0, 204, 136, SCROLL_SPEED)
	bg_back = Background.new(204, 0, 204, 136, SCROLL_SPEED)


func show_random_enemy() -> void:
	var e: Enemy = hidden[randi_range(0, hidden.size() - 1)]
	hidden.erase(e)
	e.set_is_visible(true)


func update(delta: float) -> void:
	bg_front.update(delta)
	bg_back.update(delta)
	if bg_front.is_scrolled_left:
		bg_front.reset_x(bg_back.tail_x())
	elif bg_back.is_scrolled_left:
		bg_back.reset_x(bg_front.tail_x())

	for e in enemies:
		_update_enemy(delta, e)


func _update_enemy(delta: float, e: Enemy) -> void:
	if not e.is_visible:
		return
	e.update(delta)
	if e.is_scrolled_left:
		match e.type:
			Enemy.Type.WIZARD:
				e.reset_enemy(0, false)
			Enemy.Type.SUMMONER:
				e.reset_enemy(-10, false)
			_:
				e.reset_enemy(ran_vel_x(), false)
		hidden.append(e)
		show_random_enemy()


func stop() -> void:
	for e in enemies:
		e.stop()
	bg_front.stop()
	bg_back.stop()


func on_restart() -> void:
	for w in wizards:
		w.reset_enemy(0, true)
	for k in knights:
		k.reset_enemy(ran_vel_x(), true)
	summoner.reset_enemy(-10, true)
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


func collides(hero: Hero) -> bool:
	for e in enemies:
		if e.collides(hero):
			return true
	return false


## Extra leftward speed for knights; much faster during a rush.
func ran_vel_x() -> float:
	if state == RunningState.RUSH:
		return randi_range(50, 71)
	return randi_range(10, 41)


func change_stage(new_state: RunningState) -> void:
	state = new_state
