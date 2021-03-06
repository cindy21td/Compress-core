package com.crane.GameObjects;

import java.util.List;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.crane.GameObjects.ScrollHandler.EnemyType;

public class Knight extends Enemy {

	private boolean swordThrust;
	private boolean attacking;

	private float attackTimeStart;
	private float actionDuration;
	private float attackDuration;

	private List<Integer> posX;
	private int currPos;

	public Knight(int width, int height, float scrollSpeed, List<Integer> posX) {
		super(width, height, scrollSpeed, EnemyType.KNIGHT);

		currPos = -1;
		this.posX = posX;

		position.x = getRanPosX();
		position.y = getRanPosY();

		attacking = false;
		swordThrust = false;

		attackDuration = 0.8f;
		attackTimeStart = 2;
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

		if (alive && attacking) {
			if (attackTimeStart - actionDuration > attackDuration) {
				attacking = false;
				attackTimeStart = 0;
			} else {
				attackTimeStart += delta;
			}
		} else if (alive && attackTimeStart > actionDuration) {
			attack();
		} else {
			attackTimeStart += delta;
		}
	}

	public void setBoundingCollision() {
		if (swordThrust) {
			boundingCollisionRect.set(position.x + 1, position.y + 17, 7, 2);
		} else {
			boundingCollisionRect.set(position.x + 3, position.y + 17, 7, 2);
		}
		boundingCollisionCirc.set(position.x + 13, position.y + 15, 7f);
	}

	@Override
	public void reset(float newVelX, boolean startOver) {
		attacking = false;
		swordThrust = false;
		attackTimeStart = 1.5f;

		position.y = getRanPosY();
		velocity.x = -59 - newVelX;
		alive = true;

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
			boolean hitBySword = false;
			if (attacking) {
				hitBySword = Intersector.overlaps(hero.getBoundingBody(),
						boundingCollisionRect);
			}

			boolean isHit = false;
			if (position.x < hero.getX() + hero.getWidth()) {
				isHit = Intersector.overlaps(hero.getBoundingBody(),
						boundingCollisionCirc);
			}
			return (hitBySword || isHit);
		} else {
			return false;
		}
	}

	public void attack() {
		attacking = true;
	}

	@Override
	public boolean isAttacking() {
		return attacking;
	}

	@Override
	public void swordThrust(boolean check) {
		swordThrust = check;
	}

	@Override
	public void setIsVisible(boolean check) {
		attackTimeStart = 2;
		isVisible = check;
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
		return MathUtils.random(95, 105);
	}

}
