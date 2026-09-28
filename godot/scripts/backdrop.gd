## Parallax background: sky, drifting clouds and hills (stage 0), or one of
## the four stage backdrops; the ground strip scrolls with the gameplay.
## Stages crossfade as the run gets longer.
class_name Backdrop
extends RefCounted

const WIDTH := 204.0
const HEIGHT := 136.0
const PX_PER_UNIT := 4.0  # layer art is 1632 px for 408 units
const GROUND_TOP := 120.5
const STAGE_LENGTH := 150  # displayed distance per stage
const FADE_TIME := 1.5

const SKY_SPEED := 0.05
const CLOUD_SPEED := 0.2
const HILLS_SPEED := 0.45
const STAGE_SPEED := 0.35
const WIND := 3.0  # clouds drift even when the world is still

var travelled := 0.0  # world units scrolled this run
var stage := 0
var prev_stage := 0
var fade := 1.0  # 0..1 crossfade from prev_stage to stage
var clouds: Array[Dictionary] = []


func _init() -> void:
	for i in 5:
		clouds.append(_new_cloud(randf_range(0, WIDTH + 40)))


func _new_cloud(x: float) -> Dictionary:
	var depth := randf_range(0.6, 1.0)
	return {
		"tex": Assets.clouds[randi() % Assets.clouds.size()],
		"x": x, "y": randf_range(4, 45) * (1.2 - depth * 0.4),
		"scale": depth, "speed": depth,
	}


## ground_speed is how fast the world scrolls (59, or 0 when stopped).
func update(delta: float, ground_speed: float, distance: int) -> void:
	travelled += ground_speed * delta
	for c in clouds:
		c.x -= (WIND + ground_speed * CLOUD_SPEED) * c.speed * delta
		var w: float = c.tex.get_width() / PX_PER_UNIT * c.scale
		if c.x + w < 0:
			var fresh := _new_cloud(WIDTH + randf_range(5, 40))
			c.merge(fresh, true)

	var target := floori(distance / float(STAGE_LENGTH)) % 5
	if target != stage:
		prev_stage = stage
		stage = target
		fade = 0.0
	fade = minf(fade + delta / FADE_TIME, 1.0)


func reset() -> void:
	travelled = 0.0
	stage = 0
	prev_stage = 0
	fade = 1.0


func draw(ci: CanvasItem, ground_x: float) -> void:
	if fade < 1.0:
		_draw_stage(ci, prev_stage, 1.0)
	_draw_stage(ci, stage, fade if fade < 1.0 else 1.0)
	_tile(ci, Assets.ground, fmod(ground_x, 408.0), GROUND_TOP, 1.0)


func _draw_stage(ci: CanvasItem, index: int, alpha: float) -> void:
	if index == 0:
		_tile(ci, Assets.sky, -fmod(travelled * SKY_SPEED, 408.0), 0, alpha)
		for c in clouds:
			var tex: Texture2D = c.tex
			var size: Vector2 = tex.get_size() / PX_PER_UNIT * c.scale
			ci.draw_texture_rect(tex, Rect2(Vector2(c.x, c.y), size), false, Color(1, 1, 1, alpha))
		_tile(ci, Assets.hills, -fmod(travelled * HILLS_SPEED, 408.0), 0, alpha)
	else:
		_tile(ci, Assets.stages[index], -fmod(travelled * STAGE_SPEED, 816.0), 0, alpha)
		# A light veil pushes the busy pattern back behind the characters.
		ci.draw_rect(Rect2(0, 0, WIDTH, GROUND_TOP), Color(1, 1, 1, 0.22 * alpha))


## Draws tex repeatedly across the screen starting at x (<= 0).
func _tile(ci: CanvasItem, tex: Texture2D, x: float, y: float, alpha: float) -> void:
	var size := tex.get_size() / PX_PER_UNIT
	if x > 0:
		x -= size.x
	while x < WIDTH:
		ci.draw_texture_rect(tex, Rect2(Vector2(x, y), size), false, Color(1, 1, 1, alpha))
		x += size.x - 0.02  # tiny overlap hides seams from filtering
