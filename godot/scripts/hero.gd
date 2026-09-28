## The player. First tap enables control, then tap to jump and tap again to double jump.
class_name Hero
extends RefCounted

const GROUND_Y := 104.0
const JUMP_VELOCITY := -200.0
const GRAVITY := 460.0

var position: Vector2
var velocity := Vector2.ZERO
var acceleration := Vector2.ZERO
var width: int
var height: int
var center_y: float

var jumped := false
var double_jumped := false
var falling := false
var action_disabled := true
var alive := true

# Collision shapes
var body_center := Vector2.ZERO
var body_radius := 0.0
var feet := Rect2()


func _init(x: float, y: float, w: int, h: int) -> void:
	width = w
	height = h
	position = Vector2(x, y)
	center_y = y + floori(h / 2.0)


func update(delta: float) -> void:
	falling = velocity.y > 0

	# Landed
	if jumped and position.y + floori(height / 2.0) > center_y:
		jumped = false
		double_jumped = false
		acceleration = Vector2.ZERO
		velocity = Vector2.ZERO
		position.y = GROUND_Y

	velocity += acceleration * delta
	position += velocity * delta

	body_radius = 7.0
	if falling:
		body_center = position + Vector2(16, 11)
		feet = Rect2(position.x + 12, position.y + 19, 9, 2)
	elif jumped:
		body_center = position + Vector2(15, 10)
		feet = Rect2(position.x + 13, position.y + 19, 6, 2)
	else:
		body_center = position + Vector2(15, 13)


func on_restart() -> void:
	velocity = Vector2.ZERO
	acceleration = Vector2.ZERO
	position = Vector2(30, GROUND_Y)
	action_disabled = true
	falling = false
	jumped = false
	double_jumped = false
	alive = true


func on_click() -> void:
	if action_disabled:
		action_disabled = false
	elif alive and jumped and not double_jumped:
		double_jumped = true
		velocity.y = JUMP_VELOCITY
		Assets.play_sound("jump", 0.3)
	elif alive and not jumped:
		jumped = true
		velocity.y = JUMP_VELOCITY
		acceleration.y = GRAVITY
		Assets.play_sound("jump", 0.3)


## Bounce after stomping an enemy; also restores the double jump.
func hit_enemy() -> void:
	if alive:
		velocity.y = -100
		double_jumped = false
		Assets.play_sound("hit", 0.3)
