package com.crane.GameObjects;

import com.badlogic.gdx.math.Vector2;

public class Boss extends Scrollable {

	private ScrollHandler scroller;

	private Vector2 acceleration;

	private boolean alive;
	private boolean win;
	private boolean dropped;
	
	private int health = 10;

	public Boss(float x, float y, int width, int height, float scrollSpeed,
			ScrollHandler scroller) {
		super(x, y, width, height, scrollSpeed);

		this.scroller = scroller;

		acceleration = new Vector2(0, 0);

		alive = false;
		win = false;
		dropped = false;
	}

	public void update(float delta) {
		if(win) {
			if(position.y + height >= 135) {
				acceleration.y = 0;
				velocity.y = 0;
				position.y = 15;
				dropped = true;
			}
	        velocity.add(acceleration.cpy().scl(delta));
		} else if (position.x + width >= 102) {
			win = true;
			acceleration.y = 480;
		}

		
		super.update(delta);
	}

	public void onRestart() {
		position.x = -157;
		position.y = -80;
		velocity.x = 10;
		health = 10;
		alive = true;
		win = false;
		dropped = false;
	}

	public boolean collides(Enemy enemy) {
		if (alive && !enemy.isEaten() && !enemy.isAlive() && enemy.isVisible()
				&& enemy.getX() < 0) {
			position.x -= 10;
			enemy.setEaten(true);
			health--;
			if (health <= 0) {
				alive = false;
				win = false;
				dropped = false;
				scroller.setBossAlive(false);
				scroller.toogleBossFight(false);

				// NEED FIX
				position.x = -157;
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

	public boolean hasWon() {
		return win;
	}

	public boolean dropped() {
		return dropped;
	}
}
