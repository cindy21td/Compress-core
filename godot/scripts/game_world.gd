## Game rules: state machine, scoring, rush phases and game over.
class_name GameWorld
extends RefCounted

enum State { MENU, READY, RUNNING, GAMEOVER, HIGHSCORE }

## For effects only; the rules don't depend on anyone listening.
signal stomped(enemy: Enemy)
signal died

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
	hero.update(delta)
	scroller.update(delta)

	var stomp := scroller.stomped_enemy(hero)
	if stomp:
		add_score(1)
		stomped.emit(stomp)
	if not invincible and scroller.collides(hero):
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
	Assets.loop_theme(0.3)


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
