## Base for anything that scrolls left with the world.
class_name Scrollable
extends RefCounted

var position: Vector2
var velocity: Vector2
var width: int
var height: int
var is_scrolled_left := false
var is_visible := false


func _init(x: float, y: float, w: int, h: int, scroll_speed: float) -> void:
	position = Vector2(x, y)
	velocity = Vector2(scroll_speed, 0)
	width = w
	height = h


func update(delta: float) -> void:
	position += velocity * delta
	if position.x + width < 0:
		is_scrolled_left = true
		is_visible = false


func reset_x(new_x: float) -> void:
	position.x = new_x
	is_scrolled_left = false
	is_visible = false


func stop() -> void:
	velocity.x = 0


func tail_x() -> float:
	return position.x + width
