package com.crane.GameObjects;

import java.util.ArrayList;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.crane.GameObjects.ScrollHandler.EnemyType;

public class Wizard extends Enemy {

	private float attackTimeStart;
	private float actionDuration;

	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

	public Wizard(int width, int height, float scrollSpeed) {
		super(width, height, scrollSpeed, EnemyType.WIZARD);

		position.x = getRanPosX();
		position.y = getRanPosY();

		attackTimeStart = 1;
		actionDuration = 1.5f;
	}

	@Override
	public void update(float delta) {
		position.add(velocity.cpy().scl(delta));
        // If the Scrollable object is no longer visible:
        if (position.x + width < 0) {
            isScrolledLeft = true;
            isVisible = false;
        }
		setBoundingCollision();

		if (alive && attackTimeStart > actionDuration) {
			shoot();
			attackTimeStart = 0;
		} else {
			attackTimeStart += delta;
		}

		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = (Projectile) projectiles.get(i);
			p.update(delta);

		}

	}

	@Override
	public void reset(float newVelX) {
		attackTimeStart = 1.5f;
		projectiles.clear();

		position.y = getRanPosY();
		velocity.x = -59 - newVelX;
		alive = true;
		eaten = false;
		
		position.x = getRanPosX();
		isScrolledLeft = false;
		isVisible = false;
	}

	@Override
	public boolean collides(Hero hero) {
		if (alive && isVisible) {
			boolean hitByProjectile = false;
			for (int i = 0; i < projectiles.size(); i++) {
				Projectile p = (Projectile) projectiles.get(i);
				if (p.collides(hero)) {
					hitByProjectile = true;
				}
			}

			boolean isHit = false;
			if (position.x < hero.getX() + hero.getWidth()) {
				isHit = Intersector.overlaps(hero.getBoundingBody(),
						boundingCollisionCirc);
			}
			return (hitByProjectile || isHit);
		} else {
			return false;
		}
	}

	public void setBoundingCollision() {
		boundingCollisionCirc.set(position.x + 10, position.y + 12f, 6f);
	}

	public void shoot() {
		Projectile p = new Projectile(position.x, position.y + 5, velocity.x);
		p.setIsVisible(true);
		projectiles.add(p);
	}

	@Override
	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}
	
	@Override
	public void setIsVisible(boolean check) {
		attackTimeStart = 1;
		isVisible = check;
	}
	
	@Override
	public float getRanPosX() {
		//return MathUtils.random(204, 235);
		return 204 + (MathUtils.random(0, 2) * 20f);
	}

	@Override
	public float getRanPosY() {
		return MathUtils.random(50, 81);
	}


}
