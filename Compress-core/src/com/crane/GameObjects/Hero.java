package com.crane.GameObjects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
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
	
	private boolean alive;
	
	// Collision object
	private Circle boundingHead;
	private Rectangle boundingBody;
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
		
		alive = true;
		
		// Collision object
		boundingHead = new Circle();
		boundingBody = new Rectangle();
		boundingFeet = new Rectangle();
	}
	
	public void update(float delta) {
		if(jumped && position.y + height / 2 > centerY) {
			jumped = false;
			doubleJumped = false;
			acceleration.y = 0;
			acceleration.x = 0;
			velocity.y = 0;
			velocity.x = 0;
			position.y = 97;
			
		}
				
		velocity.add(acceleration.cpy().scl(delta));
		
		position.add(velocity.cpy().scl(delta));
		
		
		// Collision
		if(jumped) {
			boundingHead.set(position.x + 18, position.y + 12, 5f);
			boundingBody.set(position.x + 10, position.y + 16, 11f, 10f);
		} else {
			boundingHead.set(position.x + 24, position.y + 12, 5f);
			boundingBody.set(position.x + 10, position.y + 16, 13f, 10f);
		}
		
		
		boundingFeet.set(position.x + 10, position.y + 26, 10f, 4f);
	}
	
	public void onRestart() {
		position.x = 30;
		position.y = 97;
		
		
		jumped = false;
		doubleJumped = false;
		
		alive = true;
	}
	
	public void onClick() {
		if(alive && jumped && !doubleJumped) {
			doubleJumped = true;
			velocity.y = -200;
		} else if(alive && !jumped) {
			jumped = true;
			velocity.y = -200;
			acceleration.y = 460;
		}
	}
	
	public void hitEnemy() {
		if(alive) {
			velocity.y = -100;
			if(doubleJumped) {
				doubleJumped = false;
			}
		}
	}
	
	public void takeElement() {
		
	}
	
	public boolean isJumping() {
		return jumped;
	}
	
	public boolean isAlive() {
		return alive;
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
	
	public Circle getBoundingHead() {
		return boundingHead;
	}
	
	public Rectangle getBoundingBody() {
		return boundingBody;
	}
	
	public Rectangle getBoundingFeet() {
		return boundingFeet;
	}
}
