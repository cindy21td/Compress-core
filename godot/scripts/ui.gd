## Drawing helpers for the interface: text, panels, buttons and icons.
## Everything is in world units (204 x 136), drawn onto the given CanvasItem.
class_name Ui
extends RefCounted

const FONT_PX := 72  # glyphs are rasterized at this size, then scaled down
const INK := Color(0.08, 0.07, 0.1)
const PAPER := Color(1, 0.98, 0.93)
const RED := Color(0.83, 0.2, 0.13)
const GOLD := Color(1, 0.78, 0.15)

enum Icon { NONE, PLAY, PAUSE, REPLAY, HOME, SOUND_ON, SOUND_OFF }


## Text whose cap height is roughly `size` units, with an outline.
static func text(ci: CanvasItem, s: String, pos: Vector2, size: float, color := INK,
		align := HORIZONTAL_ALIGNMENT_LEFT, outline_color := Color.WHITE, outline := 0.14) -> void:
	var font: Font = Assets.ui_font
	var scale := size / FONT_PX * 1.35
	var w := font.get_string_size(s, HORIZONTAL_ALIGNMENT_LEFT, -1, FONT_PX).x * scale
	var x := pos.x
	if align == HORIZONTAL_ALIGNMENT_CENTER:
		x -= w / 2
	elif align == HORIZONTAL_ALIGNMENT_RIGHT:
		x -= w
	ci.draw_set_transform(Vector2(x, pos.y), 0.0, Vector2(scale, scale))
	var base := Vector2(0, font.get_ascent(FONT_PX) * 0.8)
	if outline > 0 and outline_color.a > 0:
		ci.draw_string_outline(font, base, s, HORIZONTAL_ALIGNMENT_LEFT, -1, FONT_PX,
			int(FONT_PX * outline), outline_color)
	ci.draw_string(font, base, s, HORIZONTAL_ALIGNMENT_LEFT, -1, FONT_PX, color)
	ci.draw_set_transform(Vector2.ZERO)


static func text_width(s: String, size: float) -> float:
	return Assets.ui_font.get_string_size(s, HORIZONTAL_ALIGNMENT_LEFT, -1, FONT_PX).x * size / FONT_PX * 1.35


## Rounded card with an ink border and a drop shadow.
static func panel(ci: CanvasItem, r: Rect2, fill := PAPER, radius := 4.0, border := 1.0, shadow := 1.5) -> void:
	if shadow > 0:
		_round_rect(ci, Rect2(r.position + Vector2(0, shadow), r.size), radius, Color(0, 0, 0, 0.3))
	_round_rect(ci, r, radius, INK)
	_round_rect(ci, r.grow(-border), maxf(radius - border, 0.5), fill)


static func _round_rect(ci: CanvasItem, r: Rect2, radius: float, color: Color) -> void:
	radius = minf(radius, minf(r.size.x, r.size.y) / 2)
	var pts := PackedVector2Array()
	var corners := [
		[r.position + Vector2(r.size.x - radius, radius), -PI / 2],
		[r.end - Vector2(radius, radius), 0.0],
		[r.position + Vector2(radius, r.size.y - radius), PI / 2],
		[r.position + Vector2(radius, radius), PI],
	]
	for c in corners:
		for i in 7:
			pts.append(c[0] + Vector2.from_angle(c[1] + PI / 2 * i / 6.0) * radius)
	ci.draw_colored_polygon(pts, color)


static func icon(ci: CanvasItem, kind: Icon, center: Vector2, s: float, color := INK) -> void:
	match kind:
		Icon.PLAY:
			ci.draw_colored_polygon(PackedVector2Array([center + Vector2(-0.35, -0.45) * s,
				center + Vector2(0.5, 0) * s, center + Vector2(-0.35, 0.45) * s]), color)
		Icon.PAUSE:
			ci.draw_rect(Rect2(center + Vector2(-0.4, -0.45) * s, Vector2(0.28, 0.9) * s), color)
			ci.draw_rect(Rect2(center + Vector2(0.12, -0.45) * s, Vector2(0.28, 0.9) * s), color)
		Icon.REPLAY:
			ci.draw_arc(center, 0.38 * s, -PI * 0.35, PI * 1.35, 20, color, 0.16 * s)
			var tip := center + Vector2.from_angle(-PI * 0.35) * 0.38 * s
			ci.draw_colored_polygon(PackedVector2Array([tip + Vector2(-0.05, -0.28) * s,
				tip + Vector2(0.3, 0.02) * s, tip + Vector2(-0.12, 0.2) * s]), color)
		Icon.HOME:
			ci.draw_colored_polygon(PackedVector2Array([center + Vector2(-0.5, -0.02) * s,
				center + Vector2(0, -0.48) * s, center + Vector2(0.5, -0.02) * s]), color)
			ci.draw_rect(Rect2(center + Vector2(-0.34, -0.04) * s, Vector2(0.68, 0.5) * s), color)
		Icon.SOUND_ON, Icon.SOUND_OFF:
			ci.draw_colored_polygon(PackedVector2Array([center + Vector2(-0.45, -0.15) * s,
				center + Vector2(-0.22, -0.15) * s, center + Vector2(0.05, -0.42) * s,
				center + Vector2(0.05, 0.42) * s, center + Vector2(-0.22, 0.15) * s,
				center + Vector2(-0.45, 0.15) * s]), color)
			if kind == Icon.SOUND_ON:
				ci.draw_arc(center + Vector2(0.05, 0) * s, 0.22 * s, -0.9, 0.9, 8, color, 0.09 * s)
				ci.draw_arc(center + Vector2(0.05, 0) * s, 0.4 * s, -0.9, 0.9, 10, color, 0.09 * s)
			else:
				ci.draw_line(center + Vector2(0.18, -0.2) * s, center + Vector2(0.48, 0.2) * s, color, 0.1 * s)
				ci.draw_line(center + Vector2(0.18, 0.2) * s, center + Vector2(0.48, -0.2) * s, color, 0.1 * s)


static func ease_out_back(p: float) -> float:
	var c1 := 1.70158
	return 1 + (c1 + 1) * pow(p - 1, 3) + c1 * pow(p - 1, 2)


static func ease_out_quad(p: float) -> float:
	return -p * (p - 2)


static func ease_in_out_quad(p: float) -> float:
	return 2 * p * p if p < 0.5 else -1 + (4 - 2 * p) * p
