## The Devourer: the dragon boss cut from the 2015 build (restored from
## Boss.java in git history). A Summoner that escapes alive calls it in; it
## creeps in from the top-left, and souls of stomped enemies that fly into
## its jaw hurt it and knock it back. If it reaches mid-screen it drops onto
## the hero.
class_name Boss
extends Scrollable

enum Phase { INACTIVE, CHASE, DROP, LANDED, RETREAT }

const W := 157
const H := 120
const START := Vector2(-85, -50)  # head and jaw already on screen
const SPEED := 3.5
const KNOCKBACK := 16.0
const MAX_HEALTH := 6
const DROP_AT := 102.0  # right edge position that triggers the drop
const GRAVITY := 480.0
const GROUND := 135.0

var phase := Phase.INACTIVE
var health := MAX_HEALTH
var hurt_time := 0.0
var landed_time := 0.0
var defeated := false  # retreating because health ran out (vs. shield)


func _init() -> void:
	super(START.x, START.y, W, H, 0)


func active() -> bool:
	return phase != Phase.INACTIVE


func summon() -> void:
	phase = Phase.CHASE
	position = START
	velocity = Vector2(SPEED, 0)
	health = MAX_HEALTH
	defeated = false
	hurt_time = 0.0


func reset() -> void:
	phase = Phase.INACTIVE
	position = START
	velocity = Vector2.ZERO


## The jaw: souls that touch it are eaten.
func jaw() -> Rect2:
	return Rect2(position.x, position.y + height - 12, width - 10, 4)


## Where souls aim: the front of the jaw, which is on screen first.
func jaw_target() -> Vector2:
	var j := jaw()
	return Vector2(j.end.x - 12, j.get_center().y)


func update(delta: float) -> void:
	hurt_time = maxf(hurt_time - delta, 0.0)
	match phase:
		Phase.CHASE:
			position += velocity * delta
			if position.x + width >= DROP_AT:
				phase = Phase.DROP
				velocity = Vector2.ZERO
		Phase.DROP:
			velocity.y += GRAVITY * delta
			position += velocity * delta
			if position.y + height >= GROUND:
				position.y = GROUND - height
				phase = Phase.LANDED
				landed_time = 0.0
		Phase.LANDED:
			landed_time += delta
			if landed_time > 1.0:  # only reached if the hero survived (shield)
				retreat(false)
		Phase.RETREAT:
			if defeated:  # knocked out: hops, then tumbles off the bottom
				velocity.y += GRAVITY * 0.25 * delta
				position += Vector2(-12, velocity.y) * delta
			else:
				position += Vector2(-40, -50) * delta
			if position.y + height < -5 or position.x + width < -5 or position.y > GROUND:
				phase = Phase.INACTIVE


func retreat(beaten: bool) -> void:
	phase = Phase.RETREAT
	defeated = beaten
	velocity = Vector2(0, -60)


## Eats the soul if it reached the jaw. Returns true on a hit.
func try_eat(soul: Soul) -> bool:
	if phase != Phase.CHASE or not soul.is_visible:
		return false
	if not Geo.circle_rect_overlap(soul.circle_center, soul.circle_radius, jaw()):
		return false
	soul.is_visible = false
	position.x = maxf(position.x - KNOCKBACK, START.x)  # stays in view
	health -= 1
	hurt_time = 0.25
	if health <= 0:
		retreat(true)
	return true


func collides(hero: Hero) -> bool:
	if not hero.alive:
		return false
	match phase:
		Phase.DROP, Phase.LANDED:
			return Geo.circle_rect_overlap(hero.body_center, hero.body_radius,
				Rect2(position.x, position.y + height - 45, width - 10, 45))
	return false
