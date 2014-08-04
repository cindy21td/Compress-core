package com.crane.GameObjects;


public class Boss extends Scrollable {
	
	private boolean alive;

	
	public Boss(float x, float y, int width, int height, float scrollSpeed) {
		super(x, y, width, height, scrollSpeed);
		
		alive = true;
	}
	
	public void update(float delta) {
		super.update(delta);
	}

}
