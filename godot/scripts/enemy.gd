## Base enemy. Jumping on it (feet vs. its circle) kills it and scores a point.
class_name Enemy
extends Scrollable

enum Type { WIZARD, KNIGHT, SUMMONER, PIG }

var alive := true
var type: Type
var rect := Rect2()
var circle_center := Vector2.ZERO
var circle_radius := 0.0
var soul := Soul.new()


func _init(w: int, h: int, scroll_speed: float, enemy_type: Type) -> void:
	super(0, 0, w, h, scroll_speed)
	type = enemy_type


func is_hit(hero: Hero) -> bool:
	if hero.alive and alive and is_visible and position.x < hero.position.x + hero.width:
		if hero.jumped and Geo.circle_rect_overlap(circle_center, circle_radius, hero.feet):
			alive = false
			hero.hit_enemy()
			soul.position = position
			soul.velocity = Vector2(-59, -120)
			soul.is_visible = true
			return true
	return false


## Shared movement for all enemy types.
func _scroll(delta: float) -> void:
	if soul.is_visible:
		soul.update(delta)
	position += velocity * delta
	if position.x + width < 0:
		is_scrolled_left = true
		is_visible = false


func _body_hit(hero: Hero) -> bool:
	return position.x < hero.position.x + hero.width \
		and Geo.circles_overlap(hero.body_center, hero.body_radius, circle_center, circle_radius)


# Overridden by subclasses.
func reset_enemy(_new_vel_x: float, _start_over: bool) -> void:
	pass


func collides(_hero: Hero) -> bool:
	return false


func set_is_visible(check: bool) -> void:
	is_visible = check


func get_projectiles() -> Array[Projectile]:
	return []


func is_attacking() -> bool:
	return false


func set_sword_thrust(_value: bool) -> void:
	pass


## Picks a free spawn slot from the list shared by enemies of the same kind.
static func take_slot(slots: Array[int], current: int) -> int:
	var i := randi_range(0, slots.size() - 1)
	var slot := slots[i]
	slots.remove_at(i)
	if current != -1:
		slots.append(current)
	return slot
