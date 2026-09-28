## Overlap tests matching libGDX's Intersector.
class_name Geo
extends RefCounted


static func circles_overlap(c1: Vector2, r1: float, c2: Vector2, r2: float) -> bool:
	return c1.distance_squared_to(c2) < (r1 + r2) * (r1 + r2)


static func circle_rect_overlap(c: Vector2, r: float, rect: Rect2) -> bool:
	var closest := Vector2(
		clampf(c.x, rect.position.x, rect.end.x),
		clampf(c.y, rect.position.y, rect.end.y))
	return c.distance_squared_to(closest) < r * r
