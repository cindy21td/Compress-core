## Frame animation over regions of the sprite sheet (port of libGDX Animation).
class_name SpriteAnim
extends RefCounted

enum Mode { NORMAL, LOOP, LOOP_PINGPONG }

var frames: Array[Rect2]
var frame_duration: float
var mode: Mode


func _init(duration: float, regions: Array[Rect2], play_mode: Mode = Mode.NORMAL) -> void:
	frame_duration = duration
	frames = regions
	mode = play_mode


func key_frame_index(state_time: float) -> int:
	var n := frames.size()
	if n == 1:
		return 0
	var i := int(state_time / frame_duration)
	match mode:
		Mode.NORMAL:
			i = mini(n - 1, i)
		Mode.LOOP:
			i = i % n
		Mode.LOOP_PINGPONG:
			i = i % (n * 2 - 2)
			if i >= n:
				i = n - 2 - (i - n)
	return i


func key_frame(state_time: float) -> Rect2:
	return frames[key_frame_index(state_time)]
