package com.crane.GameObjects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.crane.GameObjects.ScrollHandler.EnemyType;

public class Wizard extends Enemy {

	private float attackTimeStart;
	private float actionDuration;

	private List<Integer> posX;
	private int currPos;

	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

	public Wizard(int width, int height, float scrollSpeed, List<Integer> posX) {
		super(width, height, scrollSpeed, EnemyType.WIZARD);

		currPos = -1;
		this.posX = posX;

		position.x = getRanPosX();
		position.y = getRanPosY();

		attackTimeStart = 1;
		actionDuration = 1.5f;
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

	public void setBoundingCollision() {
		boundingCollisionCirc.set(position.x + 10, position.y + 12f, 6f);
	}

	@Override
	public void reset(float newVelX, boolean startOver) {
		attackTimeStart = 1.5f;
		projectiles.clear();

		position.y = getRanPosY();
		velocity.x = -59 - newVelX;
		alive = true;
		eaten = false;
		// soulVisible = false;

		bossFight = false;

		position.x = getRanPosX();
		isScrolledLeft = false;
		isVisible = false;

		if (startOver) {
			soul.reset();
		}

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
			if (!hero.isDashing() && position.x < hero.getX() + hero.getWidth()) {
				isHit = Intersector.overlaps(hero.getBoundingBody(),
						boundingCollisionCirc);
			}
			return (hitByProjectile || isHit);
		} else {
			return false;
		}
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
		if (bossFight && check) {
			position.y = getRanPosY();
		}
	}

	@Override
	public float getRanPosX() {
		int ranIndex = MathUtils.random(0, posX.size() - 1);
		int pos = posX.get(ranIndex);
		posX.remove(ranIndex);
		if (currPos != -1) {
			posX.add(currPos);
		}
		currPos = pos;
		return 204 + currPos * 5;
		// return MathUtils.random(204, 235);
	}

	@Override
	public float getRanPosY() {
		if (bossFight) {
			return MathUtils.random(90, 96);
		}
		return MathUtils.random(50, 81);
	}

}
