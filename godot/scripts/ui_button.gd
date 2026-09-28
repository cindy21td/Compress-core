## A tappable button: a rounded card with an icon and/or label, or a sprite.
class_name UiButton
extends RefCounted

var rect: Rect2
var label: String
var icon: Ui.Icon
var fill: Color
var sprite := Rect2()  # region of Assets.texture; used instead of a card when set
var visible := true
var pressed := false


func _init(r: Rect2, text := "", icon_kind := Ui.Icon.NONE, color := Ui.PAPER) -> void:
	rect = r
	label = text
	icon = icon_kind
	fill = color


func hit(p: Vector2) -> bool:
	return visible and rect.grow(3).has_point(p)


func touch_down(p: Vector2) -> bool:
	pressed = hit(p)
	return pressed


## True if the press started and ended on this button.
func touch_up(p: Vector2) -> bool:
	var ok := pressed and hit(p)
	pressed = false
	return ok


func draw(ci: CanvasItem, scale := 1.0, alpha := 1.0) -> void:
	if not visible:
		return
	var r := rect
	if scale != 1.0:
		r = Rect2(r.get_center() - r.size * scale / 2, r.size * scale)
	if pressed:
		r.position.y += 1
	if sprite.has_area():
		ci.draw_texture_rect_region(Assets.texture, r, sprite, Color(1, 1, 1, alpha))
		return
	Ui.panel(ci, r, fill, 4.0, 1.0, 0.5 if pressed else 1.5)
	var h := r.size.y
	if label == "":
		Ui.icon(ci, icon, r.get_center(), h * 0.55)
	elif icon == Ui.Icon.NONE:
		Ui.text(ci, label, Vector2(r.get_center().x, r.get_center().y - h * 0.26), h * 0.42, Ui.INK, HORIZONTAL_ALIGNMENT_CENTER, Color(0, 0, 0, 0))
	else:
		var tw := Ui.text_width(label, h * 0.42)
		var x0 := r.get_center().x - (tw + h * 0.6) / 2
		Ui.icon(ci, icon, Vector2(x0 + h * 0.25, r.get_center().y), h * 0.5)
		Ui.text(ci, label, Vector2(x0 + h * 0.6, r.get_center().y - h * 0.26), h * 0.42, Ui.INK, HORIZONTAL_ALIGNMENT_LEFT, Color(0, 0, 0, 0))
