package com.crane.GameObjects;

import com.crane.CompressHelpers.Randomizer;

public class ScrollHandler {
	
	private Enemy enemyBlob, enemyBat, enemyGoblin;
	private Background bgFront, bgBack;
	
	private Element elementTest;
	
	private Randomizer r;
	
	public static final int SCROLL_SPEED = -59;
	
	public ScrollHandler(float yPos) {
		//r = new Randomizer();
		
		
		/*enemyBlob = new Enemy(r.getEnemyOnePosition().x, r.getEnemyOnePosition().y, 
				15, 15, SCROLL_SPEED - r.getEnemyOneVelocity().x);
		enemyBat = new Enemy(r.getEnemyTwoPosition().x, r.getEnemyTwoPosition().y,
				15, 15, SCROLL_SPEED - r.getEnemyTwoVelocity().x);
		enemyGoblin = new Enemy(r.getEnemyThreePosition().x, r.getEnemyThreePosition().y,
				15, 15, SCROLL_SPEED - r.getEnemyThreeVelocity().x);
		*/
		
		enemyBlob = new Enemy(204, 111, 15, 15, SCROLL_SPEED - 30);
		enemyBat = new Enemy(204, 50, 15, 15, SCROLL_SPEED - 15);
		enemyGoblin = new Enemy(204, 105, 15, 15, SCROLL_SPEED - 20);
		
		//enemyBlob = new Enemy(204, 111, 15, 15, SCROLL_SPEED - 30);
		//enemyBat = new Enemy(204, 50, 15, 15, SCROLL_SPEED - 15);
		//enemyGoblin = new Enemy(204, 105, 15, 15, SCROLL_SPEED - 20);
		
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
        	
        	r.resetEnemyOne(enemyBat.getPosition(), enemyGoblin.getPosition());
        	enemyBlob.reset(r.getEnemyOnePosition());
        	
            //enemyBlob.reset(204);
        }
        
        enemyBat.update(delta);
        if (enemyBat.isScrolledLeft()) {
            
        	r.resetEnemyTwo(enemyBlob.getPosition(), enemyGoblin.getPosition());
        	enemyBlob.reset(r.getEnemyTwoPosition());
        	
        	//enemyBat.reset(204);
        }
        
        enemyGoblin.update(delta);
        if(enemyGoblin.isScrolledLeft()) {
        	
        	r.resetEnemyThree(enemyBat.getPosition(), enemyBlob.getPosition());
        	enemyBlob.reset(r.getEnemyThreePosition());
        	
        	//enemyGoblin.reset(204);
        }
        
        elementTest.update(delta);;
        if(elementTest.isScrolledLeft()) {
        	elementTest.reset(204);
        }

    }
    
    public void stop() {
    	enemyBlob.stop();
    	enemyBat.stop();
    	enemyGoblin.stop();
    	
    	bgFront.stop();
    	bgBack.stop();
    	
    	elementTest.stop();
    }
    
    public boolean collides(Hero hero) {
   		return (enemyBlob.collides(hero) || enemyBat.collides(hero) || enemyGoblin.collides(hero));
	}
    
    public boolean enemyIsHit(Hero hero) {
    	return (enemyBlob.isHit(hero) || enemyBat.isHit(hero) || enemyGoblin.isHit(hero));
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
    
    public Enemy getEnemyGoblin() {
    	return enemyGoblin;
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
