package com.crane.GameObjects;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Scrollable {
	
	private Vector2 acceleration;
		
	private float centerX;
	private float centerY;
	
	private boolean jumped;
	
	private Rectangle boundingCollision;
	
	public Enemy(float x, float y, int width, int height, float scrollSpeed) {
		super(x, y, width, height, scrollSpeed);
		
		this.acceleration = new Vector2(0,0);
		this.centerX = x + width / 2;
		this.centerY = y + height / 2;
		
		jumped = false;
		
		boundingCollision = new Rectangle();
		
	}
	
	public void update(float delta) {
		velocity.add(acceleration.cpy().scl(delta));
		super.update(delta);
		
		// Collision
		boundingCollision.set(position.x + 2, position.y + 3, 10f, 10f);
	}
	
	@Override
    public void reset(float newX) {
        // Call the reset method in the superclass (Scrollable)
        super.reset(newX);

    }
	
	public boolean collides(Hero hero) {
        if (position.x < hero.getX() + hero.getWidth()) {
            return (Intersector.overlaps(hero.getBoundingHead(), boundingCollision)
                    || Intersector.overlaps(hero.getBoundingBody(), boundingCollision));
        }
        return false;
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

	public boolean isJumped() {
		return jumped;
	}

	public void setJumped(boolean jumped) {
		this.jumped = jumped;
	}
	
	public Rectangle getBoundingCollision() {
		return boundingCollision;
	}

}
