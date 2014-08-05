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
	
	public void onRestart() {
		position.x = -150;
		velocity.x = 5;
		alive = true;
	}
	
	public boolean collides(Enemy enemy) {
		if(!enemy.isEaten() && !enemy.isAlive() && enemy.getX() < 0) {
			position.x -= 5;
			enemy.setEaten(true);
			return true;
		}
		
		return false;
	}
	
	public boolean isAlive() {
		return alive;
	}

}
