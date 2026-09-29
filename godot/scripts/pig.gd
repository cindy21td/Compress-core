## The Pig: a ground enemy from the 2016 art backup that never shipped.
## Charges in faster than the scroll and hops at random.
class_name Pig
extends Enemy

const GROUND_Y := 107.0
const HOP_SPEED := -115.0
const GRAVITY := 400.0

var hop_timer := 1.0
var vy := 0.0


func _init() -> void:
	super(20, 20, ScrollHandler.SCROLL_SPEED - 40, Type.PIG)
	position = Vector2(randi_range(204, 240), GROUND_Y)


func update(delta: float) -> void:
	_scroll(delta)
	if alive:
		hop_timer -= delta
		if hop_timer <= 0 and is_equal_approx(position.y, GROUND_Y):
			vy = HOP_SPEED
			hop_timer = randf_range(0.7, 1.5)
		if vy != 0 or position.y < GROUND_Y:
			vy += GRAVITY * delta
			position.y += vy * delta
			if position.y >= GROUND_Y:
				position.y = GROUND_Y
				vy = 0
	circle_center = position + Vector2(10, 11)
	circle_radius = 7.0


func is_hopping() -> bool:
	return position.y < GROUND_Y - 0.5


func reset_enemy(_new_vel_x: float, start_over: bool) -> void:
	position = Vector2(randi_range(204, 240), GROUND_Y)
	velocity.x = -59 - randi_range(35, 55)
	vy = 0
	hop_timer = randf_range(0.4, 1.2)
	alive = true
	is_scrolled_left = false
	is_visible = false
	if start_over:
		soul.reset_soul()


func collides(hero: Hero) -> bool:
	return alive and is_visible and _body_hit(hero)
