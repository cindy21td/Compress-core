package com.crane.GameObjects;

public class ScrollHandler {
	
	private Enemy enemyBlob, enemyBat;
	private Background bgFront, bgBack;
	
	private Element elementTest;
	
	public static final int SCROLL_SPEED = -59;
	
	public ScrollHandler(float yPos) {
		enemyBlob = new Enemy(204, 111, 15, 15, SCROLL_SPEED - 30);
		enemyBat = new Enemy(204, 50, 15, 15, SCROLL_SPEED - 15);
		
		bgFront = new Background(0, 0, 204, 126, SCROLL_SPEED);
		bgBack = new Background(204, 0, 204, 126, SCROLL_SPEED);
		
		elementTest = new Element(204, 80, 15, 15, SCROLL_SPEED);
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
        
        enemyBat.update(delta);
        if (enemyBat.isScrolledLeft()) {
            enemyBat.reset(204);
        }
        
        elementTest.update(delta);;
        if(elementTest.isScrolledLeft()) {
        	elementTest.reset(204);
        }

    }
    
    public void stop() {
    	enemyBlob.stop();
    	enemyBat.stop();
    	bgFront.stop();
    	bgBack.stop();
    	
    	elementTest.stop();
    }
    
    public boolean collides(Hero hero) {
   		return (enemyBlob.collides(hero) || enemyBat.collides(hero));
	}
    
    public boolean enemyIsHit(Hero hero) {
    	return (enemyBlob.isHit(hero) || enemyBat.isHit(hero));
    }
    
    public boolean elementIsTaken(Hero hero) {
    	return elementTest.collides(hero);
    }
    
    public Enemy getEnemyBlob() {
        return enemyBlob;
    }
    
    public Enemy getEnemyBat() {
    	return enemyBat;
    }
    
    public Background getBgFront() {
    	return bgFront;
    }
    
    public Background getBgBack() {
    	return bgBack;
    }
    
    public Element getElement() {
    	return elementTest;
    }


}
