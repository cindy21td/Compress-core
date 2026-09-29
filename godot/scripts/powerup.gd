## A floating pickup that scrolls with the world.
class_name PowerUp
extends Scrollable

enum Kind { SHIELD, SLOWMO, DOUBLE, MAGNET }

const RADIUS := 6.0

var kind := Kind.SHIELD
var bob := 0.0


func _init() -> void:
	super(-50, 0, 12, 12, ScrollHandler.SCROLL_SPEED)


func spawn(k: Kind) -> void:
	kind = k
	position = Vector2(212, randf_range(48, 92))
	velocity = Vector2(ScrollHandler.SCROLL_SPEED, 0)
	is_visible = true
	bob = 0.0


func update(delta: float) -> void:
	if not is_visible:
		return
	bob += delta
	position.x += velocity.x * delta
	if position.x < -20:
		is_visible = false


func center() -> Vector2:
	return position + Vector2(6, 6 + sin(bob * 4) * 2)


func collides(hero: Hero) -> bool:
	return is_visible and hero.alive and hero.body_center.distance_to(center()) < hero.body_radius + RADIUS
