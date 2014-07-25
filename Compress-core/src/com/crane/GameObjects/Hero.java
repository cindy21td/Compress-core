package com.crane.GameObjects;

import com.badlogic.gdx.math.Vector2;

public class Hero {

	private Vector2 position;
	private Vector2 velocity;
	private Vector2 acceleration;
	
	private int width;
	private int height;
	
	private float centerX;
	private float centerY;
	
	private boolean jumped;
	private float attackTime;
	
	public Hero(float x, float y, int width, int height) {
		this.setWidth(width);
		this.setHeight(height);
		
		position = new Vector2(x, y);
		velocity = new Vector2(0, 0);
		acceleration = new Vector2(0, 0);
		
		setCenterX(x + width / 2);
		setCenterY(y + height / 2);
		
		jumped = false;
	}
	
	public void update(float delta) {
		attackTime += delta;
		if(jumped && position.y + height / 2 > centerY) {
			jumped = false;
			acceleration.y = 0;
			velocity.y = 0;
			position.y = 106;
			
		}
		
		velocity.add(acceleration.cpy().scl(delta));
		
		position.add(velocity.cpy().scl(delta));
	}
	
	public void onClick() {
		if(!jumped) {
			jumped = true;
			velocity.y = -140;
			acceleration.y = 460;
		}
	}
	
	public void onSwipe() {
		attackTime = 0;
	}
	
	public boolean isAttacking() {
		return attackTime < 0.1;
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
}
