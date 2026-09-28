## Floats in the air and fires projectiles every 1.5s.
class_name Wizard
extends Enemy

var attack_time_start := 1.0
var action_duration := 1.5
var slots: Array[int]
var curr_pos := -1
var projectiles: Array[Projectile] = []


func _init(w: int, h: int, scroll_speed: float, shared_slots: Array[int]) -> void:
	super(w, h, scroll_speed, Type.WIZARD)
	slots = shared_slots
	position.x = ran_pos_x()
	position.y = ran_pos_y()


func update(delta: float) -> void:
	_scroll(delta)
	circle_center = position + Vector2(10, 12)
	circle_radius = 6.0
	if alive and attack_time_start > action_duration:
		shoot()
		attack_time_start = 0
	else:
		attack_time_start += delta
	for p in projectiles:
		p.update(delta)


func reset_enemy(new_vel_x: float, start_over: bool) -> void:
	attack_time_start = 1.5
	projectiles.clear()
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
	var hit_by_projectile := false
	for p in projectiles:
		if p.collides(hero):
			hit_by_projectile = true
	return hit_by_projectile or _body_hit(hero)


func shoot() -> void:
	var p := Projectile.new(position.x, position.y + 5, velocity.x)
	p.is_visible = true
	projectiles.append(p)


func get_projectiles() -> Array[Projectile]:
	return projectiles


func set_is_visible(check: bool) -> void:
	attack_time_start = 1
	is_visible = check
	if check:
		position.y = ran_pos_y()


func ran_pos_x() -> float:
	curr_pos = Enemy.take_slot(slots, curr_pos)
	return 204 + curr_pos * 5


func ran_pos_y() -> float:
	return randi_range(50, 81)
