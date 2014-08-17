package com.crane.GameObjects;

import java.util.ArrayList;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.crane.GameObjects.ScrollHandler.EnemyType;

public class Enemy extends Scrollable {

	private Vector2 acceleration;

	private float centerX;
	private float centerY;

	private boolean eaten;
	protected boolean alive;

	protected Rectangle boundingCollisionRect;
	protected Circle boundingCollisionCirc;

	private EnemyType type;

	public Enemy(float x, float y, int width, int height, float scrollSpeed,
			EnemyType type) {
		super(x, y, width, height, scrollSpeed);

		this.acceleration = new Vector2(0, 0);
		this.centerX = x + width / 2;
		this.centerY = y + height / 2;

		eaten = false;
		alive = true;

		this.type = type;

		boundingCollisionRect = new Rectangle();
		boundingCollisionCirc = new Circle();

	}

	public void update(float delta) {
		velocity.add(acceleration.cpy().scl(delta));
		super.update(delta);
	}

	public void reset(float newX, float newY, float newVelX) {
		position.y = newY;
		velocity.x = -59 - newVelX;
		alive = true;
		eaten = false;
		super.reset(newX);
	}

	public boolean isHit(Hero hero) {
		if (hero.isJumping() && hero.isAlive() && alive && isVisible
				&& (position.x < hero.getX() + hero.getWidth())) {
			if (Intersector.overlaps(boundingCollisionCirc,
					hero.getBoundingFeet())) {
				alive = false;
				hero.hitEnemy();
				return true;
			}
		}

		return false;
	}

	public boolean isAlive() {
		return alive;
	}

	public boolean isEaten() {
		return eaten;
	}

	public void setEaten(boolean check) {
		eaten = check;
	}

	public float getCenterX() {
		return centerX;
	}

	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}

	public float getVelX() {
		return velocity.x;
	}

	public Rectangle getBoundingCollisionRect() {
		return boundingCollisionRect;
	}

	public Circle getBoundingCollisionCircle() {
		return boundingCollisionCirc;
	}

	public EnemyType getType() {
		return type;
	}
	
	// Overriden Methods
	public boolean collides(Hero hero) {
		return false;
	}

	public void setIsVisible(boolean check) {		
	}

	public ArrayList<Projectile> getProjectiles() {
		return null;
	}

	public boolean isAttacking() {
		return false;
	}

	public void swordThrust(boolean b) {		
	}
	// Overriden Methods

}
