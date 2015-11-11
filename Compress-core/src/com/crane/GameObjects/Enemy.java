package com.crane.GameObjects;

import java.util.ArrayList;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.crane.GameObjects.ScrollHandler.EnemyType;

public class Enemy extends Scrollable {

	protected boolean alive;

	protected Rectangle boundingCollisionRect;
	protected Circle boundingCollisionCirc;

	protected Soul soul;

	private EnemyType type;

	public Enemy(int width, int height, float scrollSpeed, EnemyType type) {
		super(0, 0, width, height, scrollSpeed);

		alive = true;

		this.type = type;

		boundingCollisionRect = new Rectangle();
		boundingCollisionCirc = new Circle();

		soul = new Soul();

	}

	public boolean isHit(Hero hero) {
		if (hero.isAlive() && alive && isVisible
				&& (position.x < hero.getX() + hero.getWidth())) {
			if ((hero.isJumping() && Intersector.overlaps(
					boundingCollisionCirc, hero.getBoundingFeet()))) {
				alive = false;
				hero.hitEnemy();
				soul.position.x = position.x;
				soul.position.y = position.y;
				soul.setIsVisible(true);
				return true;
			}
		}

		return false;
	}

	public boolean isAlive() {
		return alive;
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

	public boolean soulVisible() {
		return soul.isVisible;
	}

	public Soul getSoul() {
		return soul;
	}

	// Overriden Methods
	@Override
	public void update(float delta) {
	}

	public void reset(float newX, boolean startOver) {
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
