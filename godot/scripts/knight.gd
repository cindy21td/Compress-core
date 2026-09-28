## Runs along the ground faster than the scroll and swings a sword.
class_name Knight
extends Enemy

var sword_thrust := false
var attacking := false
var attack_time_start := 2.0
var action_duration := 1.5
var attack_duration := 0.8
var slots: Array[int]
var curr_pos := -1


func _init(w: int, h: int, scroll_speed: float, shared_slots: Array[int]) -> void:
	super(w, h, scroll_speed, Type.KNIGHT)
	slots = shared_slots
	position.x = ran_pos_x()
	position.y = ran_pos_y()


func update(delta: float) -> void:
	_scroll(delta)
	_set_bounds()
	if alive and attacking:
		if attack_time_start - action_duration > attack_duration:
			attacking = false
			attack_time_start = 0
		else:
			attack_time_start += delta
	elif alive and attack_time_start > action_duration:
		attacking = true
	else:
		attack_time_start += delta


func _set_bounds() -> void:
	if sword_thrust:
		rect = Rect2(position.x + 1, position.y + 17, 7, 2)
	else:
		rect = Rect2(position.x + 3, position.y + 17, 7, 2)
	circle_center = position + Vector2(13, 15)
	circle_radius = 7.0


func reset_enemy(new_vel_x: float, start_over: bool) -> void:
	attacking = false
	sword_thrust = false
	attack_time_start = 1.5
	position.y = ran_pos_y()
	velocity.x = -59 - new_vel_x
	alive = true
	position.x = ran_pos_x()
	is_scrolled_left = false
	is_visible = false
	if start_over:
		soul.reset_soul()


func collides(hero: Hero) -> bool:
	if not (alive and is_visible):
		return false
	var hit_by_sword := attacking and Geo.circle_rect_overlap(hero.body_center, hero.body_radius, rect)
	return hit_by_sword or _body_hit(hero)


func is_attacking() -> bool:
	return attacking


func set_sword_thrust(value: bool) -> void:
	sword_thrust = value


func set_is_visible(check: bool) -> void:
	attack_time_start = 2
	is_visible = check


func ran_pos_x() -> float:
	curr_pos = Enemy.take_slot(slots, curr_pos)
	return 204 + curr_pos * 5


func ran_pos_y() -> float:
	return randi_range(95, 105)
