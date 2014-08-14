package com.crane.GameObjects;

import com.badlogic.gdx.math.Vector2;

public class Scrollable {
	
	// Protected is similar to private, but allows inheritance by subclasses.
    protected Vector2 position;
    protected Vector2 velocity;
    protected int width;
    protected int height;
    protected boolean isScrolledLeft;
    protected boolean isVisible;


	public Scrollable(float x, float y, int width, int height, float scrollSpeed) {
		position = new Vector2(x, y);
        velocity = new Vector2(scrollSpeed, 0);
        this.width = width;
        this.height = height;
        isScrolledLeft = false;
	}
	
	public void update(float delta) {
        position.add(velocity.cpy().scl(delta));
        // If the Scrollable object is no longer visible:
        if (position.x + width < 0) {
            isScrolledLeft = true;
            isVisible = false;
        }
        
    }

    // Reset: Should Override in subclass for more specific behavior.
    public void reset(float newX) {
        position.x = newX;
        isScrolledLeft = false;
        isVisible = false;
    }
    
    public void stop() {
        velocity.x = 0;
    }

    // Getters for instance variables
    public boolean isScrolledLeft() {
        return isScrolledLeft;
    }
    
    public boolean isVisible() {
    	return isVisible;
    }

    public float getTailX() {
        return position.x + width;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public boolean getScrolledLeft() {
    	return isScrolledLeft;
    }

}
