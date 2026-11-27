## When to ask for an App Store rating (1.3). Pure logic, saved to its own
## file in user://, tested headless by tests/rating_test.tscn.
##
## - Ask at most once ever: `asked` is saved the moment the ask is made.
## - Only after a good finished run: a new best of at least MIN_BEST, or a run
##   that beat the Devourer.
## - Never on the score screen right after the death that ended that run: a
##   good run only makes the ask due, and main.gd makes it on the title screen
##   once the player taps Title from the score board.
## - Never in the first session, and never while the in-run lessons are still
##   teaching (Coach.teaching()).
## Apple then decides whether the prompt really shows (RatingAsk.request()).
class_name RatingPolicy
extends RefCounted

const PATH := "user://rating.cfg"
const MIN_BEST := 40  # points; a short careless run scores well under this
const MIN_SESSIONS := 2  # the first launch never asks

var path := PATH
var asked := false
var sessions := 0
var due := false  # a good run happened this session and the ask is not made yet


func _init(p_path: String = PATH) -> void:
	path = p_path
	load_state()


func load_state() -> void:
	var cfg := ConfigFile.new()
	if cfg.load(path) != OK:
		asked = false
		sessions = 0
		return
	asked = bool(cfg.get_value("rating", "asked", false))
	sessions = int(cfg.get_value("rating", "sessions", 0))


func save_state() -> void:
	var cfg := ConfigFile.new()
	cfg.set_value("rating", "asked", asked)
	cfg.set_value("rating", "sessions", sessions)
	cfg.save(path)


## Once per app launch.
func begin_session() -> void:
	sessions += 1
	due = false
	save_state()


## A run just ended. `previous_best` is the high score from before this run.
func note_run_end(score: int, previous_best: int, bosses_beaten: int, teaching: bool) -> void:
	if asked or teaching or sessions < MIN_SESSIONS:
		return
	if bosses_beaten > 0 or (score > previous_best and score >= MIN_BEST):
		due = true


## True when the title screen, reached from the score board, should ask now.
func should_ask() -> bool:
	return due and not asked


## Call right before RatingAsk.request(): never again, on any launch.
func mark_asked() -> void:
	asked = true
	due = false
	save_state()
