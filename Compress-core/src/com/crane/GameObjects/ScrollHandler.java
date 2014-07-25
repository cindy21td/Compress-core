package com.crane.GameObjects;

public class ScrollHandler {
	
	private Enemy enemyBlob;
	private Background bgFront, bgBack;
	public static final int SCROLL_SPEED = -59;
	
	public ScrollHandler(float yPos) {
		enemyBlob = new Enemy(150, 111, 15, 15, SCROLL_SPEED);
		bgFront = new Background(0, 0, 204, 126, SCROLL_SPEED);
		bgBack = new Background(204, 0, 204, 126, SCROLL_SPEED);
    }
    
    public void update(float delta) {
    	bgFront.update(delta);
    	bgBack.update(delta);
    	
    	if (bgFront.isScrolledLeft()) {
            bgFront.reset(bgBack.getTailX());
        } else if(bgBack.isScrolledLeft()) {
        	bgBack.reset(bgFront.getTailX());
        }

    	
        enemyBlob.update(delta);
        if (enemyBlob.isScrolledLeft()) {
            enemyBlob.reset(204);
        }
    }

    public Enemy getEnemy() {
        return enemyBlob;
    }
    
    public Background getBgFront() {
    	return bgFront;
    }
    
    public Background getBgBack() {
    	return bgBack;
    }


}
