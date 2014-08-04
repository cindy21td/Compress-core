package com.crane.GameWorld;

import com.crane.CompressHelpers.AssetLoader;
import com.crane.GameObjects.Hero;
import com.crane.GameObjects.ScrollHandler;

public class GameWorld {
	
	private Hero hero;
	private ScrollHandler scroller;
	
	private int score = 0;
	private int distance = 0;
	
	private int rushDistance = 0;
	
	private GameState currentState;
	private RunningState stage;
	
	public enum GameState {
		READY, RUNNING, GAMEOVER, HIGHSCORE;
	}
	
	public enum RunningState {
		NORMAL, RUSH, BOSS;
	}
	

	public GameWorld(int midPointY) {
		currentState = GameState.READY;
		stage = RunningState.NORMAL;
		
		hero = new Hero(30, 97, 32, 32);
		scroller = new ScrollHandler(this, midPointY);
		
	}
	
	public void update(float delta) {
		
		switch(currentState) {
		case READY:
			updateReady(delta);
			break;
			
		case RUNNING:
			updateRunning(delta);
			break;
			
		default:
			break;
		}
	}
	
	public void updateReady(float delta) {
		
	}
	
	public void updateRunning(float delta) {
		// Add a delta cap so that if our game takes too long
        // to update, we will not break our collision detection.

        if (delta > .15f) {
            delta = .15f;
        }

        if((rushDistance != 0) && (distance / 8 - rushDistance > 30)) {
        	stage = RunningState.NORMAL;
        	scroller.changeStage(RunningState.NORMAL);
        	
        	rushDistance = 0;
        } else if((distance / 8 != 0) && (distance / 8 % 50 == 0)) {
        	stage = RunningState.RUSH;
        	scroller.changeStage(RunningState.RUSH);
        	
        	rushDistance = distance / 8;
        }
        		
        hero.update(delta);
		scroller.update(delta);
		
		// Element
		// scroller.elementIsTaken(hero);
		
		// Collision
		if (!scroller.enemyIsHit(hero) && scroller.collides(hero)) {
	        // Clean up on game over
	        scroller.stop();
	        hero.isDead(true);
	        
	        currentState = GameState.GAMEOVER;
	        
	        if (score > AssetLoader.getHighScore()) {
                AssetLoader.setHighScore(score);
                currentState = GameState.HIGHSCORE;
            }
	        
	        if (distance / 8 > AssetLoader.getLongestDistance()) {
                AssetLoader.setLongestDistance(distance / 8);
                currentState = GameState.HIGHSCORE;
            }


	    }

		// Distance
		if(hero.isAlive()) {
			distance++;
		}
		
		// Sound Play
		// AssetLoader.example.play();
	}
	
	public void addScore(int increment) {
		score += increment;
	}
	
	
	public int getScore() {
		return score;
	}
	
	public int getDistance() {
		return distance / 8;
	}
	
	public Hero getHero() {
		return hero;
	}
	
	public ScrollHandler getScroller() {
		return scroller;
	}
	
	public boolean isReady() {
        return currentState == GameState.READY;
    }

    public void start() {
        currentState = GameState.RUNNING;
    }

    public void restart() {
        currentState = GameState.READY;
        stage = RunningState.NORMAL;
        scroller.changeStage(RunningState.NORMAL);
        
        score = 0;
        distance = 0;
        hero.onRestart();
        scroller.onRestart();
    }
    
    public boolean isRunning() {
    	return currentState == GameState.RUNNING;
    }

    public boolean isGameOver() {
        return currentState == GameState.GAMEOVER;
    }

    public boolean isHighScore() {
        return currentState == GameState.HIGHSCORE;
    }
    
    public RunningState getStage() {
    	return stage;
    }


}
