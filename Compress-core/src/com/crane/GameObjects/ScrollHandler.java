package com.crane.GameObjects;

import com.badlogic.gdx.math.MathUtils;
import com.crane.GameWorld.GameWorld;


public class ScrollHandler {
	
	private GameWorld gameWorld;
	
	private Enemy enemyOne, enemyTwo, enemyThree;
	
	private Background bgFront, bgBack;
	
	private Element elementTest;
	
	private Boss boss;
	
	private RunningState state;
	
	public enum RunningState {
		NORMAL, RUSH;
	}
	
	private boolean bossFight;
	
	
	public static final int SCROLL_SPEED = -59;
	
	public enum EnemyType {
		BLOB, BAT, GOBLIN;
	}

	
	public ScrollHandler(GameWorld world, float yPos) {
		
		this.gameWorld = world;
		this.state = RunningState.NORMAL;
		this.bossFight = false;
		
		
		enemyOne = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.BLOB);
		enemyTwo = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.BAT);
		enemyThree = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.GOBLIN);
		
				
		bgFront = new Background(0, 0, 204, 136, SCROLL_SPEED);
		bgBack = new Background(204, 0, 204, 136, SCROLL_SPEED);
		
		elementTest = new Element(204, 80, 15, 15, SCROLL_SPEED);
		
		boss = new Boss(-150, 0, 150, 136, 15, this);
    }
    
    public void update(float delta) {
    	bgFront.update(delta);
    	bgBack.update(delta);
    	
    	if (bgFront.isScrolledLeft()) {
            bgFront.reset(bgBack.getTailX());
        } else if(bgBack.isScrolledLeft()) {
        	bgBack.reset(bgFront.getTailX());
        }

    	
    	// Update Enemy
    	enemyUpdate(delta, enemyOne);
    	enemyUpdate(delta, enemyTwo);
    	enemyUpdate(delta, enemyThree);
    	
    	        
        elementTest.update(delta);
        if(elementTest.isScrolledLeft()) {
        	elementTest.reset(204);
        }
        
        if(bossFight) {
        	boss.update(delta);
        }

    }
    
    public void stop() {
    	enemyOne.stop();
    	enemyTwo.stop();
    	enemyThree.stop();
    	
    	bgFront.stop();
    	bgBack.stop();
    	
    	elementTest.stop();
    	
    	boss.stop();
    }
    
    public void onRestart() {
    	enemyOne.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
    	enemyTwo.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
    	enemyThree.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());

    	bgFront.onRestart();
    	bgBack.onRestart();
    	
    	bossFight = false;
    	boss.onRestart();
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
    
    public boolean bossIsHit() {
    	return (boss.collides(enemyOne) || boss.collides(enemyTwo) || boss.collides(enemyThree));
    }
    
    public boolean elementIsTaken(Hero hero) {
    	return elementTest.collides(hero);
    }
    
    public float getEnemyRanPosX() {
		return MathUtils.random(204, 235);
	}
	
	public float getEnemyRanPosY() {
		if(bossFight) {
			return MathUtils.random(80, 112);
		}
		
		return MathUtils.random(50, 112);
	}
	
	public float getEnemyRanVelX() {
		switch(state) {
		
		case NORMAL:
			return MathUtils.random(0, 41);
			
			
		case RUSH:
			return MathUtils.random(50, 71);
			
			
		default:
			return MathUtils.random(0, 41);
			
		}
		
	}
	
	private void enemyUpdate(float delta, Enemy enemy) {
		enemy.update(delta);
        if (enemy.isScrolledLeft()) {
        	
        	enemy.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
        	
        }

	}

	public void changeStage(RunningState newStage) {
		state = newStage;
	}
	
	public void setBossAlive(boolean check) {
		boss.setAlive(check);
	}
	
	public boolean bossWins() {
		return boss.wins();
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
    
    public Boss getBoss() {
    	return boss;
    }

    public boolean getBossFight() {
    	return bossFight;
    }
    
    public void toogleBossFight(boolean check) {
    	if(check) {
    		boss.onRestart();
    	}
    	bossFight = check;
    }

}
