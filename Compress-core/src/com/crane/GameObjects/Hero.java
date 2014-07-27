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
	private boolean doubleJumped;
	private float dodgeTime;
	private boolean dodgeDisabled;
	
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
		dodgeDisabled = false;
	}
	
	public void update(float delta) {
		dodgeTime += delta;
		if(jumped && position.y + height / 2 > centerY) {
			jumped = false;
			doubleJumped = false;
			acceleration.y = 0;
			acceleration.x = 0;
			velocity.y = 0;
			velocity.x = 0;
			position.y = 97;
			
		}
		
		if(!isDodging() && dodgeDisabled) {
			if((isDodgingRight() && position.x + width / 2 <= centerX) || 
					(!isDodgingRight() && position.x + width / 2 >= centerX)) {
				acceleration.x = 0;
				velocity.x = 0;
				position.x = 30;
				dodgeDisabled = false;
			} 
		}
		
		velocity.add(acceleration.cpy().scl(delta));
		
		position.add(velocity.cpy().scl(delta));
	}
	
	public void onClick() {
		if(jumped && !doubleJumped) {
			doubleJumped = true;
			velocity.y = -200;
		} else if(!jumped) {
			jumped = true;
			velocity.y = -200;
			acceleration.y = 460;
		}
	}
	
	public void onSwipe(boolean right) {
		if(!jumped && !dodgeDisabled && !isDodging()) {
			dodgeTime = 0;
			if(right) {
				velocity.x = 140;
				acceleration.x = -460;
			} else {
				velocity.x = -140;
				acceleration.x = 460;
			}
			dodgeDisabled = true;
		}
	}
			
	public boolean isDodging() {
		return dodgeTime < 0.20f;
	}
	
	public boolean isDodgingRight() {
		return acceleration.x < 0;
	}
	
	public boolean isJumping() {
		return jumped;
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
