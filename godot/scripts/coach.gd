## Teaches the run inside the run (DESIGN.md principle 13): no card, no
## sentence, no stall. The first enemy slows the world and a ring pulses on
## the hero; enemies walk past harmlessly until the first stomp (three at
## most). The first stomp freezes for a beat and draws the arc the hero just
## flew; a miss draws the arc it should have flown. The double jump gets a
## ring at the top of a jump, and the first Summoner (the grey ghost) an arrow toward the exit
## it must not reach. A miss says why at the enemy; a lesson tick says what
## the player just did. Each lesson is saved once learned and can be replayed
## from the title (the "?" button). Reduce Motion stops the pulsing.
class_name Coach
extends RefCounted

const MAX_SPARED := 3
const SLOW_WINDOW := 72.0  # px ahead of the hero where the world slows
const CUE_WINDOW := 110.0  # px ahead where the ring starts
# The first knight waits where a tap lands on it: the hero comes down onto an
# enemy about STOMP_LEAD s after the tap, so the hold is speed-dependent.
const STOMP_LEAD := 0.8
const HOLD_OFFSET := 4.0
const FLAGS := ["taughtJump", "taughtDouble", "taughtSummoner", "taughtFire"]

var jump_taught := false
var double_taught := false
var summoner_taught := false
var fire_taught := false  # the first wizard's fireball is shown before it can kill
var fire_spared := 0
var fire_passed: Projectile  # the last lesson fireball already judged
var spared := 0
var target: Enemy
var tapped := false  # the player has jumped once this run (the first cue waits for it)
var high_powerup := false  # the double-jump lesson's pickup, up where one jump can't reach
var reduce_motion := false

var t := 0.0
var arc := PackedVector2Array()  # feet positions of the jump in progress
var shown_arc := PackedVector2Array()
var arc_time := 99.0
var miss_from := Vector2.ZERO
var miss_to := Vector2.ZERO
var miss_time := 99.0
var misses := 0  # this run
var miss_word := ""  # why the lesson enemy got past, drawn at the enemy
var miss_arc := true  # false for a fireball miss: the word only, no arc
const MISS_WORD_TIME := 0.8  # the why shows briefly, then the lesson line returns
var target_jumped := false  # the hero jumped while the lesson enemy closed in
var tick_pos := Vector2.ZERO
var tick_time := 99.0
var tick_word := ""  # why the tick: what the player just did
var double_cue_time := 0.0  # how long the double-jump cue has been showing
var double_demoed := false  # the first cue only demonstrates; the next one asks
const DEMO_TIME := 0.35  # one flight of the ghost along the second arc
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
	fire_taught = Assets.get_pref(FLAGS[3]) > 0
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
	tapped = false
	high_powerup = false
	arc.clear()
	shown_arc.clear()
	arc_time = 99.0
	miss_time = 99.0
	misses = 0
	miss_word = ""
	miss_arc = true
	fire_spared = 0
	fire_passed = null
	target_jumped = false
	tick_time = 99.0
	tick_word = ""
	double_cue_time = 0.0
	double_demoed = false
	_summoner_was = false
	line = ""
	line_alpha = 0.0


func teaching() -> bool:
	return not jump_taught or not double_taught or not summoner_taught or not fire_taught


## Returns true on the frame a spared enemy walks past (main buzzes).
func update(delta: float, world: GameWorld) -> bool:
	t += delta
	arc_time += delta
	miss_time += delta
	tick_time += delta
	var h := world.hero
	if h.jumped:
		tapped = true
	if h.jumped:
		arc.append(h.position + Vector2(16, 21))
	elif not arc.is_empty():
		arc.clear()
	world.harmless = not jump_taught and spared < MAX_SPARED
	world.learning = not jump_taught
	world.fire_harmless = fire_wizard(world) != null
	_update_fire(world)
	if double_cue(world):
		double_cue_time += delta
	else:
		if double_cue_time > 0.15:
			double_demoed = true  # the ghost has flown it once: next jump asks
		double_cue_time = 0.0
	_update_line(delta, world)
	_update_high_powerup(world)

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
	if target and h.jumped:
		target_jumped = true
	var missed := false
	if target and target.position.x + target.width < h.position.x:
		spared += 1
		misses += 1
		missed = true
		miss_from = h.position + Vector2(16, 24)
		miss_to = target.circle_center - Vector2(0, target.circle_radius)
		miss_time = 0.0
		# The why, in the player's terms: no tap at all, or a jump that came down off it.
		miss_word = "Land on top" if target_jumped else "Tap sooner"
		miss_arc = true
		target = null
	if target == null:
		target_jumped = false
		var best := INF
		for e in s.enemies:
			if e.is_visible and e.alive and e.type in [Enemy.Type.KNIGHT, Enemy.Type.PIG] and e.position.x > h.position.x + h.width:
				if e.position.x < best:
					best = e.position.x
					target = e
	return missed


## The first wizard on screen after the jump is learned: its fireballs can't
## kill until one has been jumped (or two have gone through the hero).
func fire_wizard(world: GameWorld) -> Wizard:
	if not jump_taught or fire_taught or not world.hero.alive:
		return null
	for e in world.scroller.wizards:
		if e.is_visible and e.alive and e.position.x < Geo.view_width:
			return e as Wizard
	return null


## The lesson fireball nearest the hero that hasn't passed it yet.
func fire_ball(world: GameWorld) -> Projectile:
	var wz := fire_wizard(world)
	if wz == null:
		return null
	var hx := world.hero.position.x
	var best: Projectile = null
	for p in wz.projectiles:
		if p.is_visible and p.position.x + p.width >= hx and p != fire_passed:
			if best == null or p.position.x < best.position.x:
				best = p
	return best


func _update_fire(world: GameWorld) -> void:
	var wz := fire_wizard(world)
	if wz == null:
		world.wizard_spared = false
		return
	var h := world.hero
	for p in wz.projectiles:
		if p == fire_passed or not p.is_moving or p.position.x + p.width >= h.position.x:
			continue
		fire_passed = p
		if world.wizard_spared:
			# It went through the hero: say how to beat it, at the hero.
			world.wizard_spared = false
			fire_spared += 1
			misses += 1
			miss_to = h.position + Vector2(16, 0)
			miss_from = miss_to
			miss_time = 0.0
			miss_word = "Jump the fire"
			miss_arc = false
			if fire_spared >= 2:
				_learn(3)
		else:
			_learn(3)
			_tick(h.position + Vector2(16, -6), "Cleared it")
		return


func _ahead(world: GameWorld) -> float:
	return target.position.x - world.hero.position.x if target else INF


## The world runs at half speed while the lesson's enemy closes in.
func slow(world: GameWorld) -> bool:
	var a := _ahead(world)
	if waiting(world):
		return true  # a fast knight can reach its hold point before the slow window
	if not jump_taught and not world.hero.jumped and a > 0 and a < SLOW_WINDOW:
		return true
	var fb := fire_ball(world)
	if fb and fb.is_moving and not world.hero.jumped and fb.position.x - world.hero.position.x < SLOW_WINDOW:
		return true  # the first fireball closes in at half speed
	return double_cue(world)  # the double-jump demo plays in slow motion too


## While the double jump is being taught, a pickup floats up where one jump
## can't reach: the second tap has something to go for.
const HIGH_PICKUP_Y := 34.0
func _update_high_powerup(world: GameWorld) -> void:
	var p := world.scroller.powerup
	if not jump_taught or double_taught or world.state != GameWorld.State.RUNNING:
		high_powerup = false
		return
	if not p.is_visible:
		p.spawn(PowerUp.Kind.DOUBLE)
		p.position.y = HIGH_PICKUP_Y
		high_powerup = true


## The first cue of a run waits for the player: the world holds still with the
## knight just ahead until the first tap.
func waiting(world: GameWorld) -> bool:
	if jump_taught or tapped or target == null or world.hero.jumped or not world.hero.alive:
		return false
	var a := _ahead(world)
	return a > 0 and a <= HOLD_OFFSET - target.velocity.x * STOMP_LEAD


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
		_tick(enemy.circle_center + Vector2(0, -12), "Stopped it")
		return true
	if enemy.type == Enemy.Type.WIZARD and jump_taught and not fire_taught:
		_learn(3)  # stomping the wizard also ends its fire
	if jump_taught:
		return false
	_learn(0)
	shown_arc = arc.duplicate()
	arc_time = 0.0
	_tick(enemy.circle_center + Vector2(0, -12), "Landed on it")
	return true


func on_double(hero: Hero) -> bool:
	if not jump_taught or double_taught:
		return false
	_learn(1)
	_tick(hero.position + Vector2(16, -6), "Double jump")
	return true


func _wanted_line(world: GameWorld) -> String:
	var h := world.hero
	if not h.alive or world.state != GameWorld.State.RUNNING:
		return ""
	var s := world.scroller
	# "Tap to jump" stays up from the first step until the first stomp.
	if not jump_taught:
		if h.jumped and target != null:
			return "Land on it"
		return "Tap to jump on it" if target != null else "Tap to jump"
	if double_cue(world):
		return "Tap again to go higher" if double_demoed else "Watch: double jump"
	if fire_ball(world) != null:
		return "Jump the fire"
	if not summoner_taught and not double_cue(world) and s.summoner.is_visible and s.summoner.alive and s.summoner.position.x < Geo.view_width:
		return "Stop the ghost"
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
## One message at a time: the line steps aside while a miss or tick speaks.
func lesson_text() -> String:
	if reacting():
		return ""
	return String(TranslationServer.translate(line)) if line_alpha > 0.0 and line != "" else ""


func reacting() -> bool:
	return (miss_time < MISS_WORD_TIME and miss_word != "") or (tick_time < 0.8 and tick_word != "")


func _learn(i: int) -> void:
	match i:
		0: jump_taught = true
		1: double_taught = true
		2: summoner_taught = true
		3: fire_taught = true
	Assets.set_pref(FLAGS[i], 1)


func _tick(pos: Vector2, word: String) -> void:
	miss_time = 99.0  # a success clears the last miss's why, so the words never stack
	tick_pos = pos
	tick_time = 0.0
	tick_word = word


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
		# Ghost of the second arc, from where the hero is now, with a ghost
		# hero flying it (the demo) before the line asks for the tap.
		var f := h.position + Vector2(16, 21)
		var ghost_arc := _parabola(f, f + Vector2(46, 4), 26.0)
		_dotted(ci, ghost_arc, Color(1, 1, 1, 0.75))
		var k := fmod(double_cue_time, DEMO_TIME + 0.15) / DEMO_TIME
		if k <= 1.0:
			var gi := int(round(k * (ghost_arc.size() - 1)))
			var g := ghost_arc[gi] + Vector2(0, -9)
			ci.draw_circle(g, 7.5, Color(Ui.INK, 0.45 * (1.0 - 0.5 * k)))
			ci.draw_circle(g, 6.5, Color(1, 1, 1, 0.55 * (1.0 - 0.5 * k)))
	# One lesson at a time: while the jump is being taught, mark where to land.
	if not jump_taught and target and target.is_visible and target.alive and (jump_cue(world) or h.jumped):
		var top := target.circle_center - Vector2(0, target.circle_radius + 7 + 2 * _pulse(5))
		var chev := PackedVector2Array([top + Vector2(-5, -4), top, top + Vector2(5, -4)])
		ci.draw_polyline(chev, Ui.INK, 3.4)
		ci.draw_polyline(chev, Color.WHITE, 1.8)
	elif fire_ball(world) != null:
		var fb := fire_ball(world)
		_ring(ci, fb.circle_center, 8.0, Ui.RED)
		if not h.jumped:
			_tap_glyph(ci, h.position + Vector2(16, -9), 1)
	elif jump_taught and not summoner_taught and s.summoner.is_visible and s.summoner.alive and s.summoner.position.x < Geo.view_width:
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
		if miss_arc:
			_dotted(ci, _parabola(miss_from, miss_to, 30.0), Color(1, 1, 1, 0.85 * a))
			var x := miss_to + Vector2(0, -4)
			ci.draw_line(x + Vector2(-3.5, -3.5), x + Vector2(3.5, 3.5), Color(Ui.RED, a), 1.8)
			ci.draw_line(x + Vector2(-3.5, 3.5), x + Vector2(3.5, -3.5), Color(Ui.RED, a), 1.8)
		if miss_word != "" and miss_time < MISS_WORD_TIME:  # the why, at the enemy that got past
			a = 1.0 - maxf(0, miss_time - (MISS_WORD_TIME - 0.2)) / 0.2
			var mw := String(TranslationServer.translate(miss_word))
			var half := Ui.text_width(mw, 8) / 2 + 6
			var mp := miss_to + Vector2(0, -22 - 4 * minf(miss_time / 0.3, 1))
			mp.x = clampf(mp.x, half + 16, Geo.view_width - half - 16)  # clear of the notch side
			Ui.text(ci, mw, mp, 8, Color(Ui.RED.lightened(0.15), a), HORIZONTAL_ALIGNMENT_CENTER, Color(Ui.INK, a), 0.22)
	if tick_time < 0.9:
		var a := 1.0 - maxf(0, tick_time - 0.5) / 0.4
		var p := tick_pos + Vector2(0, -6 * minf(tick_time / 0.3, 1))
		ci.draw_circle(p, 5.5, Color(Ui.INK, a))
		ci.draw_circle(p, 4.7, Color(Ui.GOLD, a))
		ci.draw_polyline(PackedVector2Array([p + Vector2(-2.4, 0), p + Vector2(-0.6, 1.9), p + Vector2(2.6, -1.9)]), Color(Ui.INK, a), 1.2)
		if tick_word != "":
			var tw := String(TranslationServer.translate(tick_word))
			var th := Ui.text_width(tw, 8) / 2 + 6
			var tp := Vector2(clampf(p.x, th + 16, Geo.view_width - th - 16), p.y - 17)
			Ui.text(ci, tw, tp, 8, Color(Ui.GOLD.lightened(0.2), a), HORIZONTAL_ALIGNMENT_CENTER, Color(Ui.INK, a), 0.22)


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
