package com.crane.GameObjects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.crane.CompressHelpers.AssetLoader;

public class Hero {

	private Vector2 position;
	private Vector2 velocity;
	private Vector2 acceleration;

	private int width;
	private int height;

	private float centerX;
	private float centerY;

	private boolean jumped;
	private boolean doubleJumped;
	private boolean falling;
	private boolean actionDisabled;

	private boolean alive;

	// Collision object
	private Circle boundingBody;
	private Rectangle boundingFeet;

	public Hero(float x, float y, int width, int height) {
		this.setWidth(width);
		this.setHeight(height);

		position = new Vector2(x, y);
		velocity = new Vector2(0, 0);
		acceleration = new Vector2(0, 0);

		setCenterX(x + width / 2);
		setCenterY(y + height / 2);

		jumped = false;
		doubleJumped = false;
		actionDisabled = true;
		falling = false;

		alive = true;

		// Collision object
		boundingBody = new Circle();
		boundingFeet = new Rectangle();
	}

	public void update(float delta) {
		if (velocity.y > 0) {
			falling = true;
		} else {
			falling = false;
		}

		if (jumped && position.y + height / 2 > centerY) {
			jumped = false;
			doubleJumped = false;
			acceleration.y = 0;
			acceleration.x = 0;
			velocity.y = 0;
			velocity.x = 0;
			position.y = 104;

		}

		velocity.add(acceleration.cpy().scl(delta));

		position.add(velocity.cpy().scl(delta));

		// Collision
		if (falling) {
			boundingBody.set(position.x + 16, position.y + 11, 7f);
			boundingFeet.set(position.x + 12, position.y + 19, 9f, 2f);

		} else if (jumped) {
			boundingBody.set(position.x + 15, position.y + 10, 7f);
			boundingFeet.set(position.x + 13, position.y + 19, 6f, 2f);

		} else {
			boundingBody.set(position.x + 15, position.y + 13, 7f);
		}

	}

	public void updateReady(float runTime) {
		// position.y = 2 * (float) Math.sin(7 * runTime) + originalY;
	}

	public void onRestart() {
		velocity.y = 0;
		velocity.x = 0;
		acceleration.y = 0;
		acceleration.x = 0;
		position.x = 30;
		position.y = 104;

		actionDisabled = true;
		falling = false;

		jumped = false;
		doubleJumped = false;

		alive = true;
	}

	public void onClick() {
		if (actionDisabled) {
			actionDisabled = false;
		} else if (alive && !actionDisabled && jumped && !doubleJumped) {
			doubleJumped = true;
			velocity.y = -200;
			
			AssetLoader.jumpSound.play(0.3f);
			
		} else if (alive && !actionDisabled && !jumped) {
			jumped = true;
			velocity.y = -200;
			acceleration.y = 460;
			
			AssetLoader.jumpSound.play(0.3f);
			
		}
	}


	public void hitEnemy() {
		if (alive) {
			velocity.y = -100;
			if (doubleJumped) {
				doubleJumped = false;
			}
			
			AssetLoader.hitSound.play(0.3f);
		}
	}

	public boolean isJumping() {
		return jumped;
	}

	public boolean isFalling() {
		return falling;
	}

	public boolean isAlive() {
		return alive;
	}

	public boolean actionIsDisabled() {
		return actionDisabled;
	}

	public void isDead(boolean isDead) {
		alive = !isDead;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
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

	public Circle getBoundingBody() {
		return boundingBody;
	}

	public Rectangle getBoundingFeet() {
		return boundingFeet;
	}
}
