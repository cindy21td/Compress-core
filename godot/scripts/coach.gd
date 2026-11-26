## Teaches the run inside the run (DESIGN.md principle 13): no card, no
## sentence, no stall. The first enemy slows the world and a ring pulses on
## the hero; enemies walk past harmlessly until the first stomp (three at
## most). The first stomp freezes for a beat and draws the arc the hero just
## flew; a miss draws the arc it should have flown. The double jump gets a
## ring at the top of a jump, and the first Summoner an arrow toward the exit
## it must not reach. Each lesson is saved once learned and can be replayed
## from the title (the "?" button). Reduce Motion stops the pulsing.
class_name Coach
extends RefCounted

const MAX_SPARED := 3
const SLOW_WINDOW := 72.0  # px ahead of the hero where the world slows
const CUE_WINDOW := 110.0  # px ahead where the ring starts
const FLAGS := ["taughtJump", "taughtDouble", "taughtSummoner"]

var jump_taught := false
var double_taught := false
var summoner_taught := false
var spared := 0
var target: Enemy
var reduce_motion := false

var t := 0.0
var arc := PackedVector2Array()  # feet positions of the jump in progress
var shown_arc := PackedVector2Array()
var arc_time := 99.0
var miss_from := Vector2.ZERO
var miss_to := Vector2.ZERO
var miss_time := 99.0
var tick_pos := Vector2.ZERO
var tick_time := 99.0
var _summoner_was := false
# One short line per first lesson (Crane 2026-11-25: glyphs + 2-3 words).
# Source strings are the msgids of i18n/strings.csv.
var line := ""
var line_alpha := 0.0
const LINE_FADE := 0.6  # seconds to fade out once the lesson is done


func load_flags() -> void:
	jump_taught = Assets.get_pref(FLAGS[0]) > 0
	double_taught = Assets.get_pref(FLAGS[1]) > 0
	summoner_taught = Assets.get_pref(FLAGS[2]) > 0
	if DisplayServer.has_method("accessibility_should_reduce_animation"):
		reduce_motion = DisplayServer.accessibility_should_reduce_animation()


## From the title's "?" button: every lesson runs again on the next run.
func replay() -> void:
	for f in FLAGS + ["seenOrb", "seenBoss"]:
		Assets.set_pref(f, 0)
	load_flags()


func start_run() -> void:
	load_flags()
	spared = 0
	target = null
	arc.clear()
	shown_arc.clear()
	arc_time = 99.0
	miss_time = 99.0
	tick_time = 99.0
	_summoner_was = false
	line = ""
	line_alpha = 0.0


func teaching() -> bool:
	return not jump_taught or not double_taught or not summoner_taught


## Returns true on the frame a spared enemy walks past (main buzzes).
func update(delta: float, world: GameWorld) -> bool:
	t += delta
	arc_time += delta
	miss_time += delta
	tick_time += delta
	var h := world.hero
	if h.jumped:
		arc.append(h.position + Vector2(16, 21))
	elif not arc.is_empty():
		arc.clear()
	world.harmless = not jump_taught and spared < MAX_SPARED
	_update_line(delta, world)

	var s := world.scroller
	# The first Summoner's mark goes once it leaves, stomped or escaped.
	var sv := s.summoner.is_visible and s.summoner.alive
	if not summoner_taught and _summoner_was and not sv:
		_learn(2)
	_summoner_was = sv
	if jump_taught:
		target = null
		return false
	if target and (not target.is_visible or not target.alive):
		target = null
	var missed := false
	if target and target.position.x + target.width < h.position.x:
		spared += 1
		missed = true
		miss_from = h.position + Vector2(16, 24)
		miss_to = target.circle_center - Vector2(0, target.circle_radius)
		miss_time = 0.0
		target = null
	if target == null:
		var best := INF
		for e in s.enemies:
			if e.is_visible and e.alive and e.type != Enemy.Type.SUMMONER and e.position.x > h.position.x + h.width:
				if e.position.x < best:
					best = e.position.x
					target = e
	return missed


func _ahead(world: GameWorld) -> float:
	return target.position.x - world.hero.position.x if target else INF


## The world runs at half speed while the lesson's enemy closes in.
func slow(world: GameWorld) -> bool:
	var a := _ahead(world)
	return not jump_taught and not world.hero.jumped and a > 0 and a < SLOW_WINDOW


func jump_cue(world: GameWorld) -> bool:
	var a := _ahead(world)
	return not jump_taught and world.hero.alive and not world.hero.jumped and a > 0 and a < CUE_WINDOW


func double_cue(world: GameWorld) -> bool:
	var h := world.hero
	return jump_taught and not double_taught and arc_time > 0.9 and h.alive \
		and h.jumped and not h.double_jumped and h.velocity.y > -70


## First success of a lesson; true if this stomp taught something.
func on_stomp(enemy: Enemy) -> bool:
	if enemy.type == Enemy.Type.SUMMONER and not summoner_taught:
		_learn(2)
		_tick(enemy.circle_center + Vector2(0, -12))
		return true
	if jump_taught:
		return false
	_learn(0)
	shown_arc = arc.duplicate()
	arc_time = 0.0
	_tick(enemy.circle_center + Vector2(0, -12))
	return true


func on_double(hero: Hero) -> bool:
	if not jump_taught or double_taught:
		return false
	_learn(1)
	_tick(hero.position + Vector2(16, -6))
	return true


func _wanted_line(world: GameWorld) -> String:
	var h := world.hero
	if not h.alive or world.state != GameWorld.State.RUNNING:
		return ""
	var s := world.scroller
	if jump_cue(world):
		return "Tap to jump"
	if not jump_taught and h.jumped and target != null:
		return "Land on it"
	if double_cue(world):
		return "Tap again in the air"
	if not summoner_taught and s.summoner.is_visible and s.summoner.alive and s.summoner.position.x < Geo.view_width:
		return "Hit the Summoner"
	return ""


func _update_line(delta: float, world: GameWorld) -> void:
	var w := _wanted_line(world)
	if w != "":
		if w != line:
			line_alpha = 0.0 if line_alpha < 0.05 else line_alpha
		line = w
		line_alpha = minf(line_alpha + delta / 0.15, 1.0)
	else:
		line_alpha = maxf(line_alpha - delta / LINE_FADE, 0.0)


## The lesson line, translated, for the UI layer (empty when none shows).
func lesson_text() -> String:
	return String(TranslationServer.translate(line)) if line_alpha > 0.0 and line != "" else ""


func _learn(i: int) -> void:
	match i:
		0: jump_taught = true
		1: double_taught = true
		2: summoner_taught = true
	Assets.set_pref(FLAGS[i], 1)


func _tick(pos: Vector2) -> void:
	tick_pos = pos
	tick_time = 0.0


func _pulse(speed := 6.0) -> float:
	return 0.0 if reduce_motion else 0.5 + 0.5 * sin(t * speed)


# ---------------------------------------------------------------- drawing (world space)

func draw(ci: CanvasItem, world: GameWorld) -> void:
	var h := world.hero
	var s := world.scroller
	if jump_cue(world):
		_ring(ci, h.body_center, 13.0)
		_tap_glyph(ci, h.position + Vector2(16, -9), 1)
	elif double_cue(world):
		_ring(ci, h.body_center, 13.0)
		_tap_glyph(ci, h.position + Vector2(16, -9), 2)
		# Ghost of the second arc, from where the hero is now.
		var f := h.position + Vector2(16, 21)
		_dotted(ci, _parabola(f, f + Vector2(46, 4), 26.0), Color(1, 1, 1, 0.75))
	if not summoner_taught and s.summoner.is_visible and s.summoner.alive and s.summoner.position.x < Geo.view_width:
		var c := s.summoner.circle_center
		_ring(ci, c, 11.0, Ui.RED)
		var tip := c + Vector2(-19 - 3 * _pulse(5), 0)
		ci.draw_line(tip + Vector2(8, 0), tip + Vector2(1, 0), Ui.RED, 1.6)
		ci.draw_colored_polygon(PackedVector2Array([tip, tip + Vector2(4, -3.5), tip + Vector2(4, 3.5)]), Ui.RED)

	if arc_time < 0.9 and shown_arc.size() > 2:
		var a := 1.0 - arc_time / 0.9
		_dotted(ci, shown_arc, Color(Ui.GOLD, a), 3)
	if miss_time < 1.3:
		var a := 1.0 - maxf(0, miss_time - 0.8) / 0.5
		_dotted(ci, _parabola(miss_from, miss_to, 30.0), Color(1, 1, 1, 0.85 * a))
		var x := miss_to + Vector2(0, -4)
		ci.draw_line(x + Vector2(-3.5, -3.5), x + Vector2(3.5, 3.5), Color(Ui.RED, a), 1.8)
		ci.draw_line(x + Vector2(-3.5, 3.5), x + Vector2(3.5, -3.5), Color(Ui.RED, a), 1.8)
	if tick_time < 0.9:
		var a := 1.0 - maxf(0, tick_time - 0.5) / 0.4
		var p := tick_pos + Vector2(0, -6 * minf(tick_time / 0.3, 1))
		ci.draw_circle(p, 5.5, Color(Ui.INK, a))
		ci.draw_circle(p, 4.7, Color(Ui.GOLD, a))
		ci.draw_polyline(PackedVector2Array([p + Vector2(-2.4, 0), p + Vector2(-0.6, 1.9), p + Vector2(2.6, -1.9)]), Color(Ui.INK, a), 1.2)


func _ring(ci: CanvasItem, c: Vector2, r: float, color := Color.WHITE) -> void:
	var p := _pulse()
	var rr := r + 2.0 * p
	ci.draw_arc(c, rr, 0, TAU, 40, Color(Ui.INK, 0.9), 3.2)
	ci.draw_arc(c, rr, 0, TAU, 40, Color(color, 1.0 - 0.25 * p), 1.8)
	ci.draw_arc(c, r - 3.5, 0, TAU, 32, Color(Ui.INK, 0.55), 1.4)
	ci.draw_arc(c, r - 3.5, 0, TAU, 32, Color(color, 0.6), 0.7)


## A fingertip dot with one ripple per tap it asks for.
func _tap_glyph(ci: CanvasItem, c: Vector2, taps: int) -> void:
	var p := _pulse(7)
	for i in taps:
		var o := c + Vector2((i - (taps - 1) / 2.0) * 7, 0)
		ci.draw_circle(o, 2.6, Ui.INK)
		ci.draw_circle(o, 2.0, Color.WHITE)
		ci.draw_arc(o, 3.5 + 1.5 * p, 0, TAU, 16, Color(1, 1, 1, 0.9 - 0.5 * p), 0.8)


func _parabola(a: Vector2, b: Vector2, height: float) -> PackedVector2Array:
	var pts := PackedVector2Array()
	for i in 13:
		var k := i / 12.0
		pts.append(a.lerp(b, k) + Vector2(0, -height * 4 * k * (1 - k)))
	return pts


func _dotted(ci: CanvasItem, pts: PackedVector2Array, color: Color, every := 1) -> void:
	for i in range(0, pts.size(), every):
		ci.draw_circle(pts[i], 0.9, Color(Ui.INK, color.a))
		ci.draw_circle(pts[i], 0.6, color)
