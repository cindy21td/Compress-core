## Visual effects in world space: particles, floating text and screen shake.
## Purely cosmetic; the game rules never read any of this.
class_name Fx
extends RefCounted

enum Kind { DUST, STAR, EMBER, SPARKLE, RING, SPEEDLINE, DEBRIS }

var particles: Array = []
var popups: Array = []
var shake_time := 0.0
var shake_strength := 0.0


func clear() -> void:
	particles.clear()
	popups.clear()
	shake_time = 0.0


func shake(strength: float, duration: float) -> void:
	shake_strength = maxf(shake_strength if shake_time > 0 else 0.0, strength)
	shake_time = maxf(shake_time, duration)


func shake_offset() -> Vector2:
	if shake_time <= 0:
		return Vector2.ZERO
	return Vector2(randf_range(-1, 1), randf_range(-1, 1)) * shake_strength * minf(shake_time * 8, 1.0)


func emit(kind: Kind, pos: Vector2, vel: Vector2, life: float, size: float, color: Color) -> void:
	particles.append({"kind": kind, "pos": pos, "vel": vel, "life": life, "max": life, "size": size, "color": color, "spin": randf() * TAU})


func dust(pos: Vector2, count: int, spread: float) -> void:
	for i in count:
		emit(Kind.DUST, pos + Vector2(randf_range(-2, 2), randf_range(-1, 0.5)),
			Vector2(randf_range(-spread, spread) - 20, randf_range(-10, -2)),
			randf_range(0.3, 0.5), randf_range(1.2, 2.2), Color(0.55, 0.42, 0.25, 0.7))


func stomp(pos: Vector2, text := "+1") -> void:
	emit(Kind.RING, pos, Vector2.ZERO, 0.3, 3, Color(1, 1, 1, 0.9))
	for i in 8:
		var a := TAU * i / 8.0 + randf_range(-0.2, 0.2)
		emit(Kind.STAR, pos, Vector2.from_angle(a) * randf_range(45, 70), 0.45, randf_range(1.6, 2.4), Color(1, 0.85, 0.15))
	popup(text, pos + Vector2(0, -6))
	shake(1.2, 0.12)


func popup(text: String, pos: Vector2, color := Color.WHITE, scale := 1.0) -> void:
	popups.append({"text": text, "pos": pos, "life": 0.8, "max": 0.8, "color": color, "scale": scale})


func burst(pos: Vector2, color: Color, count: int) -> void:
	emit(Kind.RING, pos, Vector2.ZERO, 0.35, 3, Color(color, 0.9))
	for i in count:
		emit(Kind.STAR, pos, Vector2.from_angle(randf() * TAU) * randf_range(30, 80), randf_range(0.35, 0.6),
			randf_range(1.2, 2.2), color)


func death(pos: Vector2) -> void:
	for i in 14:
		emit(Kind.DEBRIS, pos, Vector2.from_angle(randf() * TAU) * randf_range(30, 80), randf_range(0.4, 0.8),
			randf_range(1, 2), Color(0.15, 0.15, 0.15) if i % 2 else Color(0.8, 0.15, 0.1))
	emit(Kind.RING, pos, Vector2.ZERO, 0.35, 4, Color(1, 1, 1, 1))
	shake(3.0, 0.35)


func air_jump(pos: Vector2) -> void:
	emit(Kind.RING, pos, Vector2.ZERO, 0.25, 2, Color(1, 1, 1, 0.8))


func ember(pos: Vector2) -> void:
	emit(Kind.EMBER, pos + Vector2(randf_range(-1, 1), randf_range(-1, 1)), Vector2(randf_range(15, 30), randf_range(-8, 8)),
		randf_range(0.2, 0.35), randf_range(0.6, 1.1), Color(1, randf_range(0.4, 0.7), 0.1))


func sparkle(pos: Vector2) -> void:
	emit(Kind.SPARKLE, pos + Vector2(randf_range(-4, 4), randf_range(-4, 4)), Vector2(0, randf_range(-6, -2)),
		randf_range(0.3, 0.6), randf_range(0.6, 1.2), Color(1, 1, 1))


func speedline() -> void:
	emit(Kind.SPEEDLINE, Vector2(210, randf_range(8, 118)), Vector2(-randf_range(260, 380), 0),
		0.8, randf_range(8, 20), Color(1, 1, 1, randf_range(0.35, 0.6)))


func update(delta: float) -> void:
	shake_time = maxf(shake_time - delta, 0.0)
	for p in particles:
		p.life -= delta
		p.pos += p.vel * delta
		match p.kind:
			Kind.DUST:
				p.vel *= 0.9
			Kind.STAR, Kind.DEBRIS:
				p.vel.y += 160 * delta
				p.vel.x *= 0.97
				p.spin += delta * 10
	particles = particles.filter(func(p): return p.life > 0 and p.pos.x > -30)
	for t in popups:
		t.life -= delta
		t.pos.y -= 18 * delta
	popups = popups.filter(func(t): return t.life > 0)


func draw(ci: CanvasItem) -> void:
	for p in particles:
		var k: float = p.life / p.max  # 1 -> 0
		var c: Color = p.color
		match p.kind:
			Kind.DUST:
				ci.draw_circle(p.pos, p.size * (1.6 - k * 0.6), Color(c, c.a * k))
			Kind.RING:
				ci.draw_arc(p.pos, p.size + (1 - k) * 12, 0, TAU, 24, Color(c, c.a * k), 1.2 * k + 0.3)
			Kind.STAR, Kind.SPARKLE:
				_star(ci, p.pos, p.size * (0.5 + k * 0.5), p.spin, Color(c, minf(1, k * 1.5)))
			Kind.EMBER:
				ci.draw_circle(p.pos, p.size * k, Color(c, k))
			Kind.DEBRIS:
				ci.draw_rect(Rect2(p.pos - Vector2.ONE * p.size / 2, Vector2.ONE * p.size), Color(c, k))
			Kind.SPEEDLINE:
				ci.draw_line(p.pos, p.pos + Vector2(p.size, 0), Color(c, c.a * minf(1, k * 2)), 0.6)


## Floating score text; drawn on the world view after the particles.
func draw_popups(ci: CanvasItem) -> void:
	for t in popups:
		var k: float = t.life / t.max
		var pop := 1.0 + 0.4 * maxf(0, (k - 0.8) / 0.2)
		var c: Color = t.color
		Ui.text(ci, t.text, t.pos, 5.0 * t.scale * pop, Color(c, minf(1, k * 2)),
			HORIZONTAL_ALIGNMENT_CENTER, Color(Ui.INK, minf(1, k * 2)), 0.2)


func _star(ci: CanvasItem, pos: Vector2, r: float, rot: float, color: Color) -> void:
	var pts := PackedVector2Array()
	for i in 8:
		var rad := r if i % 2 == 0 else r * 0.4
		pts.append(pos + Vector2.from_angle(rot + TAU * i / 8.0) * rad)
	ci.draw_colored_polygon(pts, color)
