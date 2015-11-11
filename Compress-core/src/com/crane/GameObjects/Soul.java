package com.crane.GameObjects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Soul extends Scrollable {

	private Circle boundingCollision;

	public Soul() {
		super(0, 0, 15, 15, -59);

		velocity = new Vector2(-59, -120);

		isVisible = false;

		boundingCollision = new Circle();
	}

	@Override
	public void update(float delta) {
		if (isVisible) {
			position.add(velocity.cpy().scl(delta));
		}
		// If the Scrollable object is no longer visible:
		if (position.x + width < 0) {
			isScrolledLeft = true;
		}

		if ((position.x + width < 0) || position.y + height < 0) {
			isVisible = false;
		}

		setBoundingCollision();
	}

	public void reset() {
		isVisible = false;
		isScrolledLeft = false;
	}

	public void setBoundingCollision() {
		boundingCollision.set(position.x + 7, position.y + 3, 4f);
	}

	public void setIsVisible(boolean check) {
		isVisible = check;
	}

	public Circle getBoundingCollision() {
		return boundingCollision;
	}
}
