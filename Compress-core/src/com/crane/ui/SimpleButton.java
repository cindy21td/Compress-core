package com.crane.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class SimpleButton {

	private float x, y, width, height;

	private TextureRegion buttonUp;
	private TextureRegion buttonDown;

	private Rectangle boundsRect;

	private boolean isPressed = false;

	public SimpleButton(float x, float y, float width, float height,
			TextureRegion buttonUp, TextureRegion buttonDown) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.buttonUp = buttonUp;
		this.buttonDown = buttonDown;

		boundsRect = new Rectangle(x - 10, y - 10, width + 20, height + 20);

	}

	public void draw(SpriteBatch batcher) {
		if (isPressed) {
			batcher.draw(buttonDown, x, y, width, height);
		} else {
			batcher.draw(buttonUp, x, y, width, height);
		}
	}

	public void changePosition(float newX, float newY) {
		this.x = newX;
		this.y = newY;
	}

	public boolean isTouchDown(int screenX, int screenY) {

		if (boundsRect.contains(screenX, screenY)) {
			isPressed = true;
			return true;
		}

		return false;
	}

	public boolean isTouchUp(int screenX, int screenY) {

		// It only counts as a touchUp if the button is in a pressed state.
		if (boundsRect.contains(screenX, screenY) && isPressed) {
			isPressed = false;
			return true;
		}

		// Whenever a finger is released, we will cancel any presses.
		isPressed = false;
		return false;
	}

}
