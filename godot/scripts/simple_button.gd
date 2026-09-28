## Image button with a generous touch area (10 units on every side).
class_name SimpleButton
extends RefCounted

var rect: Rect2
var bounds: Rect2
var up: Rect2
var down: Rect2
var pressed := false


func _init(x: float, y: float, w: float, h: float, region_up: Rect2, region_down: Rect2) -> void:
	rect = Rect2(x, y, w, h)
	bounds = rect.grow(10)
	up = region_up
	down = region_down


func region() -> Rect2:
	return down if pressed else up


func touch_down(p: Vector2) -> bool:
	if bounds.has_point(p):
		pressed = true
		return true
	return false


## Counts only if the press also started on this button.
func touch_up(p: Vector2) -> bool:
	var hit := pressed and bounds.has_point(p)
	pressed = false
	return hit
