## Ghost that floats up from a defeated enemy.
class_name Soul
extends Scrollable

var circle_center := Vector2.ZERO
var circle_radius := 0.0


func _init() -> void:
	super(0, 0, 15, 15, -59)
	velocity = Vector2(-59, -120)


func update(delta: float) -> void:
	if is_visible:
		position += velocity * delta
	if position.x + width < 0:
		is_scrolled_left = true
	if position.x + width < 0 or position.y + height < 0:
		is_visible = false
	circle_center = position + Vector2(7, 3)
	circle_radius = 4.0


func reset_soul() -> void:
	is_visible = false
	is_scrolled_left = false
