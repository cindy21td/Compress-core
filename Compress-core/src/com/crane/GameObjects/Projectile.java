package com.crane.GameObjects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public class Projectile extends Scrollable {

	private Circle boundingCollision;
	private boolean isMoving;
	
	private float delayTime;

	public Projectile(float x, float y, float scrollSpeed) {
		super(x, y + 5, 10, 10, scrollSpeed);
		boundingCollision = new Circle();
		isMoving = false;
		delayTime = 0;
	}

	public void update(float delta) {
		super.update(delta);

		// Collision
		boundingCollision.set(position.x + 3, position.y + 6, 3);
		
		if(delayTime > 0.6f) {
			move();
			delayTime = 0;
		}
		if(isVisible && !isMoving) {
			delayTime += delta;
		}
	}

	public void reset(float newX, float newY, float newVelX) {
		position.y = newY;
		velocity.x = newVelX;
		isMoving = false;
		delayTime = 0;

		super.reset(newX);
	}

	public boolean collides(Hero hero) {
		if (isVisible && position.x < hero.getX() + hero.getWidth()) {
			return (Intersector.overlaps(hero.getBoundingBody(),
					boundingCollision));
		}
		return false;
	}

	public void setIsVisible(boolean check) {
		isVisible = check;
	}

	public Circle getBoundingCollision() {
		return boundingCollision;
	}
	
	public void move() {
		velocity.x -= 10;
		isMoving = true;
	}
	
	public boolean isMoving() {
		return isMoving;
	}

}