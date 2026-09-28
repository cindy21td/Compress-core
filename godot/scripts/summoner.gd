## Drifts slightly slower than the scroll at a random height.
class_name Summoner
extends Enemy


func _init(w: int, h: int, scroll_speed: float) -> void:
	super(w, h, scroll_speed, Type.SUMMONER)
	position.x = randi_range(204, 235)
	position.y = randi_range(50, 112)


func update(delta: float) -> void:
	_scroll(delta)
	circle_center = position + Vector2(9, 10)
	circle_radius = 5.0


func reset_enemy(new_vel_x: float, start_over: bool) -> void:
	position.y = randi_range(50, 112)
	velocity.x = -59 - new_vel_x
	alive = true
	position.x = randi_range(204, 235)
	is_scrolled_left = false
	is_visible = false
	if start_over:
		soul.reset_soul()


func collides(hero: Hero) -> bool:
	return alive and is_visible and _body_hit(hero)
