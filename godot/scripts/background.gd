class_name Background
extends Scrollable

var scroll_speed: float


func _init(x: float, y: float, w: int, h: int, speed: float) -> void:
	super(x, y, w, h, speed)
	scroll_speed = speed


func on_restart() -> void:
	velocity.x = scroll_speed
