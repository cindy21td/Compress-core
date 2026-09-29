## Game rules: state machine, scoring, rush phases and game over.
## On top of the original rules: stomp combos, power-ups, the Pig and the
## Devourer boss.
class_name GameWorld
extends RefCounted

enum State { MENU, READY, RUNNING, GAMEOVER, HIGHSCORE }

## For effects only; the rules don't depend on anyone listening.
signal stomped(enemy: Enemy, points: int, combo: int)
signal died
signal boss_summoned
signal boss_hit(pos: Vector2)
signal boss_defeated
signal boss_slammed
signal powerup_collected(kind: PowerUp.Kind)
signal shield_broken

const MAX_COMBO_MULTIPLIER := 4
const BOSS_HIT_POINTS := 2
const BOSS_DEFEAT_BONUS := 20
const PIG_UNLOCK_DISTANCE := 150
const SLOWMO_TIME := 4.5
const DOUBLE_TIME := 8.0
const MAGNET_TIME := 8.0
const SHIELD_GRACE := 1.2

var combo := 0  # stomps since last touching the ground
var best_combo := 0
var shield := false
var grace := 0.0  # brief invulnerability after the shield breaks
var slowmo_time := 0.0
var double_time := 0.0
var magnet_time := 0.0
var bosses_defeated := 0

var invincible := false  # demo/testing only: collisions don't end the run

const RUSH_DURATION := 60

var state := State.MENU
var hero := Hero.new(30, 104, 25, 25)
var scroller: ScrollHandler
var renderer  # main.gd: prepare_transition(), show_rate_prompt()

var score := 0
var distance := 0  # frames survived; shown as distance / 8
var total_score := 0

var rush_distance := 0
var rand_rush_number := 0


func _init(game_renderer) -> void:
	renderer = game_renderer
	rand_rush_number = _roll_rush()
	scroller = ScrollHandler.new()


static func _roll_rush() -> int:
	return randi_range(1, 10) * randi_range(1, 10) * randi_range(1, 10)


func update(delta: float) -> void:
	if state == State.RUNNING:
		_update_running(delta)


func _update_running(delta: float) -> void:
	delta = minf(delta, 0.15)
	_check_rush()
	_tick_powerups(delta)
	scroller.pig_unlocked = get_distance() >= PIG_UNLOCK_DISTANCE
	scroller.magnet = magnet_time > 0

	var was_jumping := hero.jumped
	hero.update(delta)
	if was_jumping and not hero.jumped:
		combo = 0  # touched the ground
	scroller.update(delta)
	_handle_boss_events()

	var stomp := scroller.stomped_enemy(hero)
	if stomp:
		combo += 1
		best_combo = maxi(best_combo, combo)
		var points := mini(combo, MAX_COMBO_MULTIPLIER)
		if double_time > 0:
			points *= 2
		if magnet_time > 0:
			points += 1
		add_score(points)
		stomped.emit(stomp, points, combo)

	if scroller.powerup.collides(hero):
		_collect(scroller.powerup.kind)
		scroller.powerup.is_visible = false

	var hit: Object = null if invincible or grace > 0 else scroller.colliding(hero)
	if hit and shield:
		_break_shield(hit)
		hit = null
	if hit:
		Assets.stop_theme()
		scroller.stop()
		hero.alive = false
		renderer.prepare_transition(Color.WHITE, 0.3)
		Assets.play_sound("death", 0.3)
		state = State.GAMEOVER
		died.emit()

		# The original showed a "rate me" bubble every 16 deaths.
		var rate_val := Assets.get_pref("rateValue")
		if rate_val > 15:
			renderer.show_rate_prompt(true)
			Assets.set_pref("rateValue", 0)
		else:
			Assets.set_pref("rateValue", rate_val + 1)

		total_score = floori(distance / 200.0) + score
		if total_score > Assets.get_pref("highScore"):
			Assets.set_pref("highScore", total_score)
			state = State.HIGHSCORE

	if hero.alive:
		distance += 1


func _tick_powerups(delta: float) -> void:
	grace = maxf(grace - delta, 0.0)
	slowmo_time = maxf(slowmo_time - delta, 0.0)
	double_time = maxf(double_time - delta, 0.0)
	magnet_time = maxf(magnet_time - delta, 0.0)


func _collect(kind: PowerUp.Kind) -> void:
	match kind:
		PowerUp.Kind.SHIELD:
			shield = true
		PowerUp.Kind.SLOWMO:
			slowmo_time = SLOWMO_TIME
		PowerUp.Kind.DOUBLE:
			double_time = DOUBLE_TIME
		PowerUp.Kind.MAGNET:
			magnet_time = MAGNET_TIME
	powerup_collected.emit(kind)


## The shield takes the hit and destroys whatever caused it.
func _break_shield(hit: Object) -> void:
	shield = false
	grace = SHIELD_GRACE
	if hit is Enemy:
		var e: Enemy = hit
		e.alive = false
		e.soul.position = e.position
		e.soul.velocity = Vector2(-59, -120)
		e.soul.is_visible = true
	elif hit is Boss:
		(hit as Boss).retreat(false)
	shield_broken.emit()


func _handle_boss_events() -> void:
	var s := scroller
	if s.boss_summoned:
		Assets.loop_music("boss", 0.35)
		boss_summoned.emit()
	for pos in s.boss_hits:
		add_score(BOSS_HIT_POINTS)
		boss_hit.emit(pos)
	if s.boss_landed:
		boss_slammed.emit()
	if s.boss.phase == Boss.Phase.RETREAT and s.boss.defeated and not _boss_reward_given:
		_boss_reward_given = true
		bosses_defeated += 1
		add_score(BOSS_DEFEAT_BONUS)
		boss_defeated.emit()
	if s.boss.phase == Boss.Phase.CHASE:
		_boss_reward_given = false
	if not s.boss.active() and Assets.current_music == "boss" and hero.alive:
		Assets.loop_music("theme", 0.3)


var _boss_reward_given := false


## Rush starts when distance hits a random multiple and lasts RUSH_DURATION.
func _check_rush() -> void:
	var d := get_distance()
	if rush_distance != 0 and d - rush_distance > RUSH_DURATION:
		scroller.change_stage(ScrollHandler.RunningState.NORMAL)
		rand_rush_number = _roll_rush()
		rush_distance = 0
	elif rush_distance == 0 and d != 0 and d % rand_rush_number == 0:
		scroller.change_stage(ScrollHandler.RunningState.RUSH)
		rush_distance = d


func start() -> void:
	state = State.RUNNING
	Assets.loop_music("theme", 0.3)


func enter_ready() -> void:
	state = State.READY


func restart() -> void:
	state = State.READY
	scroller.change_stage(ScrollHandler.RunningState.NORMAL)
	rand_rush_number = _roll_rush()
	rush_distance = 0
	score = 0
	distance = 0
	total_score = 0
	combo = 0
	best_combo = 0
	shield = false
	grace = 0.0
	slowmo_time = 0.0
	double_time = 0.0
	magnet_time = 0.0
	bosses_defeated = 0
	_boss_reward_given = false
	renderer.show_rate_prompt(false)
	hero.on_restart()
	scroller.on_restart()


## Back to the title screen with a fresh run prepared.
func to_menu() -> void:
	restart()
	state = State.MENU


func add_score(increment: int) -> void:
	score += increment


func get_distance() -> int:
	return floori(distance / 8.0)


func is_game_over() -> bool:
	return state == State.GAMEOVER or state == State.HIGHSCORE
