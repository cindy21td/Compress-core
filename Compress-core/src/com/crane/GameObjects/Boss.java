package com.crane.GameObjects;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Boss extends Scrollable {

	private ScrollHandler scroller;

	private Vector2 acceleration;

	private boolean alive;
	private boolean win;
	private boolean dropped;

	private int health = 10;

	private Rectangle boundingCollision;

	public Boss(float x, float y, int width, int height, float scrollSpeed,
			ScrollHandler scroller) {
		super(x, y, width, height, scrollSpeed);

		this.scroller = scroller;

		acceleration = new Vector2(0, 0);

		alive = false;
		win = false;
		dropped = false;

		boundingCollision = new Rectangle(0, 0, 0, 0);
	}

	public void update(float delta) {
		if (win) {
			if (position.y + height >= 135) {
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

		if (health <= 0) {
			scroller.setBossAlive(false);
			scroller.toogleBossFight(false);

			onRestart();
		}

		super.update(delta);

		setBoundingCollision();
	}

	public void onRestart() {
		position.x = -157;
		position.y = -80;
		velocity.x = 10;
		health = 10;
		alive = true;
		win = false;
		dropped = false;
		setBoundingCollision();
	}

	public boolean isHit(Enemy enemy) {
		if (alive && enemy.soulVisible()
				&& enemy.getSoul().getX() < position.x + width) {
			if (Intersector.overlaps(enemy.getSoul().getBoundingCollision(),
					boundingCollision)) {
				position.x -= 10;
				enemy.setEaten(true);
				health--;
				return true;
			}
		}

		return false;
	}

	public boolean collides(Hero hero) {
		if (alive && hero.isAlive()) {
			return Intersector.overlaps(hero.getBoundingBody(),
					boundingCollision);
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

	public void setBoundingCollision() {
		boundingCollision.set(position.x, position.y + height - 12, width - 10,
				4);
	}

	public Rectangle getBoundingCollision() {
		return boundingCollision;
	}
}
