package com.crane.GameObjects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

public class Element extends Scrollable {

	private float centerX;
	private float centerY;
	
	private Circle boundingCollision;
	
	private boolean taken;
	
	public Element(float x, float y, int width, int height, float scrollSpeed) {
		super(x, y, width, height, scrollSpeed);
		
		this.centerX = x + width / 2;
		this.centerY = y + height / 2;
		
		taken = false;
		
		boundingCollision = new Circle();
	}
	
	public void update(float delta) {
		super.update(delta);
		
		// Collision
		boundingCollision.set(position.x + 8, position.y + 8, 5f);
	}
	
	@Override
    public void reset(float newX) {
        // Call the reset method in the superclass (Scrollable)
		taken = false;
        super.reset(newX);

    }
	
	public boolean collides(Hero hero) {
        if (position.x < hero.getX() + hero.getWidth()) {
            if (Intersector.overlaps(hero.getBoundingHead(), boundingCollision)
                    || Intersector.overlaps(boundingCollision, hero.getBoundingBody())){
            	taken = true;
            	hero.takeElement();
            	return true;
            }
        }
        return false;
    }
	
	public boolean isTaken() {
		return taken;
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
	
	public Circle getBoundingCollision() {
		return boundingCollision;
	}


}
