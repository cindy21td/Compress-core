package com.crane.GameWorld;

import com.badlogic.gdx.math.MathUtils;
import com.crane.CompressHelpers.AssetLoader;
import com.crane.GameObjects.Hero;
import com.crane.GameObjects.ScrollHandler;
import com.crane.GameObjects.ScrollHandler.RunningState;
import com.crane.Screens.GameScreen;
import com.crane.compress.Compress;

public class GameWorld {
	
	private Hero hero;
	private ScrollHandler scroller;
	
	private int midPointY;
	
    private float runTime = 0;

	private int score = 0;
	private int distance = 0;
	
	private int rushDistance = 0;
	private final static int RUSH_DURATION = 60;
	private int randRushNumber;
	
	private GameState currentState;
		
	public enum GameState {
		MENU, READY, RUNNING, GAMEOVER, HIGHSCORE;
	}
	
	public GameWorld(int midPointY) {
		currentState = GameState.MENU;
		randRushNumber = MathUtils.random(1, 10) * MathUtils.random(1, 10) * MathUtils.random(1, 10);
		hero = new Hero(30, 104, 25, 25);
		scroller = new ScrollHandler(this, midPointY);
		
		this.midPointY = midPointY;
	}
	
	public void update(float delta) {
		runTime += delta;

        switch (currentState) {
        case READY:
        case MENU:
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
		//hero.updateReady(runTime);
        //scroller.updateReady(delta);
	}
	
	public void updateRunning(float delta) {
		// Add a delta cap so that if our game takes too long
        // to update, we will not break our collision detection.

        if (delta > .15f) {
            delta = .15f;
        }

        checkState();
        
        hero.update(delta);
		scroller.update(delta);
		
		
		// Collision
		if (scroller.collides(hero) || scroller.bossWins() ) {
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
    
    public void ready(Compress game) {
        currentState = GameState.READY;
    	game.setScreen(new GameScreen(this));

    }


    public void restart() {
        currentState = GameState.READY;
        scroller.changeStage(RunningState.NORMAL);
        
		randRushNumber = MathUtils.random(1, 10) * MathUtils.random(1, 10) * MathUtils.random(1, 10);
        rushDistance = 0;
        
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
    
    public boolean isMenu() {
        return currentState == GameState.MENU;
    }

    
    private void checkState() {
    	
    	// Normal and Rush
        if((rushDistance != 0) && (distance / 8 - rushDistance > RUSH_DURATION)) {
        	scroller.changeStage(RunningState.NORMAL);
    		randRushNumber = MathUtils.random(1, 10) * MathUtils.random(1, 10) * MathUtils.random(1, 10);
        	rushDistance = 0;
        	
        } else if((rushDistance == 0) && (distance / 8 != 0) && (distance / 8 % randRushNumber == 0)) {
        	scroller.changeStage(RunningState.RUSH);
        	rushDistance = distance / 8;
        }
        		
    }

	public int getMidPointY() {
		return midPointY;
	}
    
}
