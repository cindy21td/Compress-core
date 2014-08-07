package com.crane.GameObjects;


public class Boss extends Scrollable {
	
	private ScrollHandler scroller;
	
	private boolean alive;
	private boolean win;
	private int health = 10;

	
	public Boss(float x, float y, int width, int height, float scrollSpeed, ScrollHandler scroller) {
		super(x, y, width, height, scrollSpeed);
		
		this.scroller = scroller;
		
		alive = false;
		win = false;
	}
	
	public void update(float delta) {
		super.update(delta);
	}
	
	public void onRestart() {
		position.x = -150;
		velocity.x = 15;
		health = 10;
		alive = true;
		win = false;
	}
	
	public boolean collides(Enemy enemy) {
		if(alive && !enemy.isEaten() && !enemy.isAlive() && enemy.getX() < 0) {
			position.x -= 10;
			enemy.setEaten(true);
			health--;
			if(health <= 0) {
				alive = false;
				win = false;
				scroller.setBossAlive(false);
				scroller.toogleBossFight(false);
				
				// NEED FIX
				position.x = -150;
				velocity.x = 0;
			}
			return true;
		}
		
		return false;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public void setAlive(boolean check) {
		alive = check;
	}
	
	public boolean wins() {
		if(position.x + width >= 102) {
			win = true;
			return true;
		}
		return false;
	}
	
	public boolean hasWon() {
		return win;
	}

}
