package com.crane.GameObjects;

public class Background extends Scrollable {

	private float scrollSpeed;

	public Background(float x, float y, int width, int height, float scrollSpeed) {
		super(x, y, width, height, scrollSpeed);
		this.scrollSpeed = scrollSpeed;
	}

	public void onRestart() {
		velocity.x = scrollSpeed;
	}

}
