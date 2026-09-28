## A drawing layer that delegates _draw to a callable.
extends Node2D

var painter: Callable


func _draw() -> void:
	painter.call(self)
