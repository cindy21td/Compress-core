package com.crane.GameObjects;

import java.util.ArrayList;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.crane.GameObjects.ScrollHandler.EnemyType;

public class Enemy extends Scrollable {

	protected boolean eaten;
	protected boolean alive;

	protected Rectangle boundingCollisionRect;
	protected Circle boundingCollisionCirc;

	private EnemyType type;

	public Enemy(int width, int height, float scrollSpeed, EnemyType type) {
		super(0, 0, width, height, scrollSpeed);

		eaten = false;
		alive = true;

		this.type = type;

		boundingCollisionRect = new Rectangle();
		boundingCollisionCirc = new Circle();

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
	public void update(float delta) {
	}

	public void reset(float newX) {
	}

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

	public float getRanPosX() {
		return 0;
	}

	public float getRanPosY() {
		return 0;
	}
	// Overriden Methods

}
