package com.crane.GameObjects;

import com.badlogic.gdx.math.MathUtils;
import com.crane.GameWorld.GameWorld;


public class ScrollHandler {
	
	private GameWorld gameWorld;
	
	private Enemy enemyOne, enemyTwo, enemyThree;
	
	private Background bgFront, bgBack;
	
	private Boss boss;
	private boolean bossFight;

	
	private RunningState state;
	
	public enum RunningState {
		NORMAL, RUSH;
	}	
	
	public static final int SCROLL_SPEED = -59;
	
	public enum EnemyType {
		WHISP, BAT, GOBLIN;
	}

	
	public ScrollHandler(GameWorld world, float yPos) {
		
		this.gameWorld = world;
		this.state = RunningState.NORMAL;
		this.bossFight = false;
		
		enemyOne = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				20, 20, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.WHISP);
		enemyTwo = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.BAT);
		enemyThree = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 
				15, 15, SCROLL_SPEED - getEnemyRanVelX(), EnemyType.GOBLIN);
		
				
		bgFront = new Background(0, 0, 204, 136, SCROLL_SPEED);
		bgBack = new Background(204, 0, 204, 136, SCROLL_SPEED);
		
		boss = new Boss(-150, 0, 150, 136, 13, this);
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
    	if(enemyIsHit(hero)) {
    		gameWorld.addScore(1);
    	}
    	
    	if(bossFight && bossIsHit()) {
    		gameWorld.addScore(2);
    	}
    	
    	
   		return (enemyOne.collides(hero) || enemyTwo.collides(hero) || enemyThree.collides(hero));
	}
    
    public boolean enemyIsHit(Hero hero) {    	
    	return (enemyOne.isHit(hero) || enemyTwo.isHit(hero) || enemyThree.isHit(hero));
    }
    
    public boolean bossIsHit() {
    	return (boss.collides(enemyOne) || boss.collides(enemyTwo) || boss.collides(enemyThree));
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
