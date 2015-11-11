package com.crane.GameWorld;

import com.badlogic.gdx.math.MathUtils;
import com.crane.CompressHelpers.AssetLoader;
import com.crane.CompressHelpers.InputHandler;
import com.crane.GameObjects.Hero;
import com.crane.GameObjects.ScrollHandler;
import com.crane.GameObjects.ScrollHandler.RunningState;
import com.crane.Screens.GameScreen;
import com.crane.compress.Compress;

public class GameWorld {

	// Game main objects
	private Compress game;
	private GameScreen screen;
	private GameRenderer renderer;

	// Game objects
	private Hero hero;
	private ScrollHandler scroller;

	private int midPointY;

	// Scores variable
	private int score = 0;
	private int distance = 0;
	private int totalScore = 0;

	// Rush variable
	private int rushDistance = 0;
	private final static int RUSH_DURATION = 60;
	private int randRushNumber;

	// Game state.
	private GameState currentState;

	public enum GameState {
		MENU, READY, RUNNING, GAMEOVER, HIGHSCORE;
	}

	/**
	 * Initializes World.
	 * 
	 * @param game
	 *            Game object.
	 * @param midPointY
	 *            Midpoint Screen.
	 */
	public GameWorld(Compress game, int midPointY) {
		this.game = game;

		currentState = GameState.MENU;
		randRushNumber = MathUtils.random(1, 10) * MathUtils.random(1, 10)
				* MathUtils.random(1, 10);

		hero = new Hero(30, 104, 25, 25);
		scroller = new ScrollHandler(this, midPointY);

		this.midPointY = midPointY;
	}

	/**
	 * Updates game mechanic.
	 * 
	 * @param delta
	 */
	public void update(float delta) {

		switch (currentState) {

		case READY:
			break;

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

	/**
	 * TODO: Clean this method. Updates when READY
	 * 
	 * @param delta
	 */
	public void updateReady(float delta) {
		// hero.updateReady(runTime);
		// scroller.updateReady(delta);

		// TODO: Ads
		// Handle Ad
		game.showAd(false);
	}

	/**
	 * Updates when RUNNING.
	 * 
	 * @param delta
	 */
	public void updateRunning(float delta) {
		// Add a delta cap so that if our game takes too long
		// to update, we will not break our collision detection.

		if (delta > .15f) {
			delta = .15f;
		}

		// Checks game state.
		checkState();

		hero.update(delta);
		scroller.update(delta);

		// Handles Collision
		if (scroller.collides(hero)) {
			// Clean up on game over
			AssetLoader.runTheme.stop();

			scroller.stop();
			hero.isDead(true);
			renderer.prepareTransition(255, 255, 255, .3f);

			AssetLoader.deathSound.play(0.3f);

			currentState = GameState.GAMEOVER;

			// TODO: Ads
			// Handle Ad
			game.showAd(true);

			// Rating
			int rateVal = AssetLoader.getRateValue();
			if (rateVal > 15) {
				renderer.showRatePrompt(true);
				AssetLoader.setRateValue(0);
			} else {
				AssetLoader.setRateValue(rateVal + 1);
			}
			
			// Calculate score.
			totalScore = distance / 200 + score;
			if (totalScore > AssetLoader.getHighScore()) {
				AssetLoader.setHighScore(totalScore);
				currentState = GameState.HIGHSCORE;
			}

		}

		// Updates Distance
		if (hero.isAlive()) {
			distance++;
		}

	}

	/**
	 * Checks game state.
	 */
	private void checkState() {
		// Normal and Rush
		if ((rushDistance != 0)
				&& (distance / 8 - rushDistance > RUSH_DURATION)) {
			scroller.changeStage(RunningState.NORMAL);
			randRushNumber = MathUtils.random(1, 10) * MathUtils.random(1, 10)
					* MathUtils.random(1, 10);
			rushDistance = 0;

		} else if ((rushDistance == 0) && (distance / 8 != 0)
				&& (distance / 8 % randRushNumber == 0)) {
			scroller.changeStage(RunningState.RUSH);
			rushDistance = distance / 8;
		}

	}

	/**
	 * Starts game.
	 */
	public void start() {
		currentState = GameState.RUNNING;

		AssetLoader.runTheme.loop(0.3f);

	}

	/**
	 * Game on READY.
	 * 
	 * @param renderer
	 * @param input
	 */
	public void ready(GameRenderer renderer, InputHandler input) {
		currentState = GameState.READY;

		screen = new GameScreen(this, renderer, input);

		game.setScreen(screen);

		// TODO: Ads.
		// Handle Ads
		game.showAd(false);
	}

	/**
	 * Restart game.
	 */
	public void restart() {
		currentState = GameState.READY;
		// TODO: Ads.
		// Handle Ads.
		game.showAd(false);

		scroller.changeStage(RunningState.NORMAL);

		randRushNumber = MathUtils.random(1, 10) * MathUtils.random(1, 10)
				* MathUtils.random(1, 10);
		rushDistance = 0;

		score = 0;
		distance = 0;
		totalScore = 0;
		
		// Rating prompt
		renderer.showRatePrompt(false);

		hero.onRestart();
		scroller.onRestart();
	}

	public boolean isReady() {
		return currentState == GameState.READY;
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

	public Hero getHero() {
		return hero;
	}

	public ScrollHandler getScroller() {
		return scroller;
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

	public int getTotalScore() {
		return totalScore;
	}

	public int getMidPointY() {
		return midPointY;
	}

	// Sets renderer object in World.
	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}

}
