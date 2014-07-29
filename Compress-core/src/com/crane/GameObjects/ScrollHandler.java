package com.crane.GameObjects;

import com.badlogic.gdx.math.MathUtils;
import com.crane.GameWorld.GameWorld;


public class ScrollHandler {
	
	private GameWorld gameWorld;
	
	private Enemy enemyBlob, enemyBat, enemyGoblin;
	private Background bgFront, bgBack;
	
	private Element elementTest;
	
	public static final int SCROLL_SPEED = -59;
	
	public ScrollHandler(GameWorld world, float yPos) {
		
		this.gameWorld = world;
		
		enemyBlob = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX());
		enemyBat = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX());
		enemyGoblin = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX());
				
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
        	
        	enemyBlob.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
        	
        }
        
        enemyBat.update(delta);
        if (enemyBat.isScrolledLeft()) {
            
        	enemyBat.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
        	
        }
        
        enemyGoblin.update(delta);
        if(enemyGoblin.isScrolledLeft()) {
        	
        	enemyGoblin.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
        	
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
    
    public void onRestart() {
    	enemyBlob.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
    	enemyBat.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
    	enemyGoblin.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
    	
    	bgFront.onRestart();
    	bgBack.onRestart();
    }
    
    public boolean collides(Hero hero) {
   		return (enemyBlob.collides(hero) || enemyBat.collides(hero) || enemyGoblin.collides(hero));
	}
    
    public boolean enemyIsHit(Hero hero) {
    	
    	if (enemyBlob.isHit(hero) || enemyBat.isHit(hero) || enemyGoblin.isHit(hero)) {
    		// Add Score
    		gameWorld.addScore(1);
    		
    		return true;
    	}
    	
    	return false;
    }
    
    public boolean elementIsTaken(Hero hero) {
    	return elementTest.collides(hero);
    }
    
    public float getEnemyRanPosX() {
		return MathUtils.random(204, 235);
	}
	
	public float getEnemyRanPosY() {
		return MathUtils.random(50, 112);
	}
	
	public float getEnemyRanVelX() {
		return MathUtils.random(0, 41);
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
