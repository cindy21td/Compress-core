package com.crane.GameObjects;

import com.badlogic.gdx.math.MathUtils;
import com.crane.GameWorld.GameWorld;
import com.crane.GameWorld.GameWorld.RunningState;


public class ScrollHandler {
	
	private GameWorld gameWorld;
	
	private Enemy enemyOne, enemyTwo, enemyThree, enemyFour, enemyFive, enemySix;
	private Background bgFront, bgBack;
	
	private Element elementTest;
	
	private RunningState stage;
	
	public static final int SCROLL_SPEED = -59;
	
	public enum EnemyType {
		BLOB, BAT, GOBLIN;
	}

	
	public ScrollHandler(GameWorld world, float yPos) {
		
		this.gameWorld = world;
		this.stage = RunningState.NORMAL;
		
		
		enemyOne = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.BLOB);
		enemyTwo = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.BAT);
		enemyThree = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.GOBLIN);
		
		
		enemyFour = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.BLOB);
		enemyFive = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.BAT);
		enemySix = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.GOBLIN);
		
				
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

    	
        enemyOne.update(delta);
        if (enemyOne.isScrolledLeft()) {
        	
        	enemyOne.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
        	
        }
        
        enemyTwo.update(delta);
        if (enemyTwo.isScrolledLeft()) {
            
        	enemyTwo.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
        	
        }
        
        enemyThree.update(delta);
        if(enemyThree.isScrolledLeft()) {
        	
        	enemyThree.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
        	
        }
        
        
        elementTest.update(delta);;
        if(elementTest.isScrolledLeft()) {
        	elementTest.reset(204);
        }
        

    }
    
    public void stop() {
    	enemyOne.stop();
    	enemyTwo.stop();
    	enemyThree.stop();
    	
    	bgFront.stop();
    	bgBack.stop();
    	
    	elementTest.stop();
    }
    
    public void onRestart() {
    	enemyOne.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
    	enemyTwo.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
    	enemyThree.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
    	
    	bgFront.onRestart();
    	bgBack.onRestart();
    }
    
    public boolean collides(Hero hero) {
   		return (enemyOne.collides(hero) || enemyTwo.collides(hero) || enemyThree.collides(hero));
	}
    
    public boolean enemyIsHit(Hero hero) {
    	
    	if (enemyOne.isHit(hero) || enemyTwo.isHit(hero) || enemyThree.isHit(hero)) {
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
		switch(stage) {
		
		case NORMAL:
			return MathUtils.random(0, 41);
			
			
		case RUSH:
			return MathUtils.random(40, 81);
			
			
		case BOSS:
			return MathUtils.random(0, 21);
			
		default:
			return MathUtils.random(0, 41);
			
		}
		
	}

	public void changeStage(RunningState newStage) {
		stage = newStage;
	}
    
    public Enemy getEnemyOne() {
        return enemyOne;
    }
    
    public Enemy getEnemyTwo() {
    	return enemyTwo;
    }
    
    public Enemy getEnemyThree() {
    	return enemyThree;
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
