package com.crane.GameObjects;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.crane.GameObjects.ScrollHandler.EnemyType;

public class Summoner extends Enemy {

	public Summoner(int width, int height, float scrollSpeed) {
		super(width, height, scrollSpeed, EnemyType.SUMMONER);

		position.x = getRanPosX();
		position.y = getRanPosY();

	}

	@Override
	public void update(float delta) {
		if (soul.isVisible()) {
			soul.update(delta);
		}

		position.add(velocity.cpy().scl(delta));
		// If the Scrollable object is no longer visible:
		if (position.x + width < 0) {
			isScrolledLeft = true;
			isVisible = false;
		}

		setBoundingCollision();
	}

	public void setBoundingCollision() {
		boundingCollisionCirc.set(position.x + 9, position.y + 10, 5f);
	}

	@Override
	public void reset(float newVelX, boolean startOver) {
		position.y = getRanPosY();
		velocity.x = -59 - newVelX;
		alive = true;
		eaten = false;

		bossFight = false;

		position.x = getRanPosX();
		isScrolledLeft = false;
		isVisible = false;

		if (startOver) {
			soul.reset();
			;
		}

	}

	@Override
	public boolean collides(Hero hero) {
		if (alive && isVisible && position.x < hero.getX() + hero.getWidth()
				&& !hero.isDashing()) {
			return Intersector.overlaps(hero.getBoundingBody(),
					boundingCollisionCirc);

		} else {
			return false;
		}
	}

	@Override
	public void setIsVisible(boolean check) {
		isVisible = check;
	}

	@Override
	public float getRanPosX() {
		return MathUtils.random(204, 235);
	}

	@Override
	public float getRanPosY() {
		return MathUtils.random(50, 112);
	}

}
