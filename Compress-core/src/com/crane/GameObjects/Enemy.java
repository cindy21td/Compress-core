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
	private boolean alive;
	private boolean attacking;
	private boolean projectileIsEnabled;

	private float deltaTimeStart;
	private float deltaTimeDuration;
	private float attackDuration;

	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

	private Rectangle boundingCollisionRect;
	private Circle boundingCollisionCirc;

	private EnemyType type;

	public Enemy(float x, float y, int width, int height, float scrollSpeed,
			EnemyType type) {
		super(x, y, width, height, scrollSpeed);

		this.acceleration = new Vector2(0, 0);
		this.centerX = x + width / 2;
		this.centerY = y + height / 2;

		eaten = false;
		alive = true;
		attacking = false;

		attackDuration = 0.8f;

		this.type = type;

		boundingCollisionRect = new Rectangle();
		boundingCollisionCirc = new Circle();
		deltaTimeDuration = 1.5f;

		if (type == EnemyType.WIZARD) {
			projectileIsEnabled = true;
			deltaTimeStart = 1;

		} else {
			projectileIsEnabled = false;
			deltaTimeStart = 2;

		}
	}

	public void update(float delta) {
		velocity.add(acceleration.cpy().scl(delta));
		super.update(delta);

		// Collision
		setBoundingCollision();

		if (alive && attacking) {
			if (deltaTimeStart - deltaTimeDuration > attackDuration) {
				attacking = false;
				deltaTimeStart = 0;
			} else {
				deltaTimeStart += delta;
			}
		} else if (alive && deltaTimeStart > deltaTimeDuration) {
			if (projectileIsEnabled) {
				shoot();
				deltaTimeStart = 0;
			} else {
				startAttacking();
			}
		} else {
			deltaTimeStart += delta;
		}

		if (projectileIsEnabled) {
			for (int i = 0; i < projectiles.size(); i++) {
				Projectile p = (Projectile) projectiles.get(i);
				p.update(delta);
			}

		}
	}

	public void reset(float newX, float newY, float newVelX) {
		position.y = newY;
		velocity.x = -59 - newVelX;
		alive = true;
		eaten = false;
		attacking = false;
		deltaTimeStart = 1.5f;
		projectiles.clear();
		super.reset(newX);
	}

	public void startAttacking() {
		attacking = true;
	}

	public boolean collides(Hero hero) {
		if (alive && isVisible) {
			boolean hitByProjectile = false;
			if (projectileIsEnabled) {
				for (int i = 0; i < projectiles.size(); i++) {
					Projectile p = (Projectile) projectiles.get(i);
					if (p.collides(hero)) {
						hitByProjectile = true;
					}
				}

			}
			boolean isHit = false;
			if (position.x < hero.getX() + hero.getWidth()) {
				isHit = (Intersector.overlaps(hero.getBoundingBody(),
						boundingCollisionRect) || Intersector.overlaps(
						hero.getBoundingBody(), boundingCollisionCirc));

			}
			return (hitByProjectile || isHit);

		}
		return false;
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

	public void setBoundingCollision() {
		switch (type) {

		case WIZARD:

			boundingCollisionCirc.set(position.x + 10, position.y + 12f, 6f);
			break;

		case KNIGHT:
			if (attacking) {
				if (deltaTimeStart - deltaTimeDuration > attackDuration - 0.4f) {
					boundingCollisionRect.set(position.x + 2, position.y + 17,
							7, 2);
				} else {
					boundingCollisionRect.set(position.x + 3, position.y + 17,
							7, 2);
				}
			}
			boundingCollisionCirc.set(position.x + 13, position.y + 15, 7f);
			break;

		}

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

	public boolean isAttacking() {
		return attacking;
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

	public void setIsVisible(boolean check) {
		deltaTimeStart = 0;
		isVisible = check;
	}

	public void shoot() {
		Projectile p = new Projectile(position.x, position.y + 5, velocity.x);
		p.setIsVisible(true);
		projectiles.add(p);
	}

	public ArrayList getProjectiles() {
		return projectiles;
	}

}
