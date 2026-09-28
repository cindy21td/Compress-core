## Wizard fireball: hovers as a spark for 0.6s, then flies.
class_name Projectile
extends Scrollable

var circle_center := Vector2.ZERO
var circle_radius := 0.0
var is_moving := false
var delay_time := 0.0


func _init(x: float, y: float, scroll_speed: float) -> void:
	super(x, y + 5, 10, 10, scroll_speed)


func update(delta: float) -> void:
	super.update(delta)
	circle_center = position + Vector2(3, 6)
	circle_radius = 3.0
	if delay_time > 0.6:
		move()
		delay_time = 0
	if is_visible and not is_moving:
		delay_time += delta


func collides(hero: Hero) -> bool:
	if is_visible and position.x < hero.position.x + hero.width:
		return Geo.circles_overlap(hero.body_center, hero.body_radius, circle_center, circle_radius)
	return false


func move() -> void:
	velocity.x -= 10
	is_moving = true
