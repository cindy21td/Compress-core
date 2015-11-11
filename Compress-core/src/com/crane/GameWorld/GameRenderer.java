package com.crane.GameWorld;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.crane.CompressHelpers.AssetLoader;
import com.crane.CompressHelpers.InputHandler;
import com.crane.GameObjects.Background;
import com.crane.GameObjects.Enemy;
import com.crane.GameObjects.Hero;
import com.crane.GameObjects.Projectile;
import com.crane.GameObjects.ScrollHandler;
import com.crane.TweenAccessors.Value;
import com.crane.TweenAccessors.ValueAccessor;
import com.crane.ui.SimpleButton;

// TODO: Use midpointY.
public class GameRenderer {

	// Game's main object.
	private GameWorld myWorld;
	private OrthographicCamera cam;
	private ShapeRenderer shapeRenderer;

	// Bacther to draw sprite.
	private SpriteBatch batcher;

	private int midPointY;
	private int gameHeight;
	
	// Rate Prompt variable
	private boolean ratePrompt;

	// Game Objects
	private Hero hero;
	private ScrollHandler scroller;
	private Background bgFront, bgBack;
	private Enemy enemyOne, enemyTwo, enemyThree, enemyFour, enemyFive,
			enemySix, enemySeven;

	// Game Assets
	private TextureRegion bgFrontBody, bgBackBody;

	// Enemies
	private Animation enemyWizardAnimation, enemyKnightAnimation,
			enemySummonerAnimation;

	// Wizard Attack
	private Animation flameAnimation, lightAnimation;

	// Knight Attack
	private TextureRegion enemyKnightSwingTwo;
	private Animation enemyKnightAttackAnimation;

	// Soul
	private Animation soulAnimation;

	// Hero
	private Animation heroRunAnimation, heroStillAnimation;
	private TextureRegion heroJump, heroFall, heroDash;

	// Tween stuff
	private TweenManager manager;
	private Value alpha = new Value();

	// Icons
	private TextureRegion kill, distance;

	// Score board
	private TextureRegion scoreboard, highscoreMark;
	private Animation ratePromptAnimation;

	// Medals
	private TextureRegion bronzeMedal, silverMedal, goldMedal, emptyMedal;
	private Animation medalAnimation;

	// Title and main menu
	private TextureRegion title;
	private Animation stillAnimation, startInstructionAnimation,
			backgroundMenuAnimation;

	// InputHandler
	private InputHandler input;

	// Transition Color
	private Color transitionColor;

	/**
	 * Constructor.
	 * 
	 * @param world
	 * @param gameHeight
	 * @param midPointY
	 */
	public GameRenderer(GameWorld world, int gameHeight, int midPointY) {
		myWorld = world;

		this.midPointY = midPointY;
		this.gameHeight = gameHeight;

		// Camera.
		cam = new OrthographicCamera();
		cam.setToOrtho(true, 204, 136);

		batcher = new SpriteBatch();
		// Attach batcher to camera
		batcher.setProjectionMatrix(cam.combined);

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(cam.combined);
		
		// Rate prompt
		ratePrompt = false;

		initGameObjects();
		initAssets();

		transitionColor = new Color();
		prepareTransition(0, 0, 0, 1.5f);

	}

	/**
	 * Sets input handler.
	 * 
	 * @param input
	 */
	public void setInputDetect(InputHandler input) {
		this.input = input;
	}

	/**
	 * Initializes game's objects.
	 */
	private void initGameObjects() {
		hero = myWorld.getHero();
		scroller = myWorld.getScroller();

		bgFront = scroller.getBgFront();
		bgBack = scroller.getBgBack();

		// Enemies
		enemyOne = scroller.getEnemyOne();
		enemyTwo = scroller.getEnemyTwo();
		enemyThree = scroller.getEnemyThree();
		enemyFour = scroller.getEnemyFour();
		enemyFive = scroller.getEnemyFive();
		enemySix = scroller.getEnemySix();
		enemySeven = scroller.getEnemySeven();

	}

	/**
	 * Initializes assets.
	 */
	private void initAssets() {
		// Background
		bgFrontBody = AssetLoader.bgFront;
		bgBackBody = AssetLoader.bgBack;

		// Hero
		heroRunAnimation = AssetLoader.heroRunAnimation;
		heroStillAnimation = AssetLoader.heroStillAnimation;
		heroJump = AssetLoader.heroJump;
		heroFall = AssetLoader.heroFall;

		// Enemies.
		enemyWizardAnimation = AssetLoader.enemyWizardAnimation;
		enemyKnightAnimation = AssetLoader.enemyKnightAnimation;
		enemySummonerAnimation = AssetLoader.enemySummonerAnimation;

		// Wizard's effects
		flameAnimation = AssetLoader.flameAnimation;
		lightAnimation = AssetLoader.lightAnimation;

		// Knight's attack
		enemyKnightSwingTwo = AssetLoader.enemyKnightSwingTwo;
		enemyKnightAttackAnimation = AssetLoader.enemyKnightAttackAnimation;

		// Soul
		soulAnimation = AssetLoader.soulAnimation;

		// Score icons
		kill = AssetLoader.kill;
		distance = AssetLoader.distance;

		// Score board
		scoreboard = AssetLoader.scoreboard;
		highscoreMark = AssetLoader.highscoreMark;
		ratePromptAnimation = AssetLoader.ratePromptAnimation;

		// Medals
		bronzeMedal = AssetLoader.bronzeMedal;
		silverMedal = AssetLoader.silverMedal;
		goldMedal = AssetLoader.goldMedal;
		emptyMedal = AssetLoader.emptyMedal;
		medalAnimation = AssetLoader.medalAnimation;

		// Main menu.
		title = AssetLoader.title;
		backgroundMenuAnimation = AssetLoader.backgroundMenuAnimation;
		startInstructionAnimation = AssetLoader.startInstructionAnimation;
		stillAnimation = AssetLoader.stillAnimation;
	}

	/**
	 * Renders the main menu.
	 * 
	 * @param runTime
	 */
	public void renderMenu(float runTime) {

		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batcher.begin();

		batcher.draw(backgroundMenuAnimation.getKeyFrame(runTime), 0, 0, 204,
				136);
		batcher.draw(title, 250 / 3f, 0, 350 / 3f, 278 / 3f);

		batcher.draw(stillAnimation.getKeyFrame(runTime), -110 / 3f, -10 / 3f,
				280 / 1.8f, 300 / 1.8f);

		batcher.draw(startInstructionAnimation.getKeyFrame(runTime), 100 / 3f,
				100, (374 / 3f) / 2.0f, (60 / 3f) / 2.0f, 374 / 3f, 60 / 3f, 1,
				1, 0);

		// TODO: Handles more button. it's only replay presently.
		// drawMenuUI(input.getMenuButtons());

		batcher.end();
	}

	/**
	 * Draws buttons.
	 * 
	 * @param menuButtons
	 */
	private void drawMenuUI(List<SimpleButton> menuButtons) {
		for (SimpleButton button : menuButtons) {
			button.draw(batcher);
		}

	}

	/**
	 * Render the main game loop.
	 * 
	 * @param delta
	 * @param runTime
	 */
	// runTime is for animation (determining which frame to render);
	public void render(float delta, float runTime) {

		// Fill the entire screen with black, to prevent potential flickering.
		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Begin SpriteBatch
		batcher.begin();
		// Disable transparency
		// This is good for performance when drawing images that do not require
		// transparency.
		batcher.disableBlending();
		batcher.draw(bgFrontBody, bgFront.getX(), 0, 204, 136);
		batcher.draw(bgBackBody, bgBack.getX(), 0, 204, 136);

		batcher.enableBlending();

		drawHero(runTime);
		drawEnemy(runTime);
		drawScore(runTime);

		// End SpriteBatch
		batcher.end();

		// Transition
		drawTransition(delta);

		// TODO: (Only for debugging).
		// Check Collision
		// drawCollisionCheck();

	}

	/**
	 * Draws hero.
	 * 
	 * @param runTime
	 */
	private void drawHero(float runTime) {
		// Check case
		int val = 0;
		if (!hero.isAlive()) {
			val = 1;
		} else if (hero.actionIsDisabled()) {
			val = 2;
		} else if (hero.isFalling()) {
			val = 3;
		} else if (hero.isJumping()) {
			val = 4;
		}

		switch (val) {
		case 0:
			// Hero Running (alive)
			batcher.draw(heroRunAnimation.getKeyFrame(runTime), hero.getX(),
					hero.getY(), hero.getWidth() / 2.0f,
					hero.getHeight() / 2.0f, hero.getWidth(), hero.getHeight(),
					1, 1, 0);

			break;

		case 1:
			// Hero is dead
			batcher.draw(soulAnimation.getKeyFrame(runTime), hero.getX(),
					hero.getY(), hero.getWidth() / 2.0f,
					hero.getHeight() / 2.0f, hero.getWidth(), hero.getHeight(),
					1, 1, 0);

			break;

		case 2:
			// Hero's starting position
			batcher.draw(heroStillAnimation.getKeyFrame(runTime), hero.getX(),
					hero.getY(), hero.getWidth() / 2.0f,
					hero.getHeight() / 2.0f, hero.getWidth(), hero.getHeight(),
					1, 1, 0);

			break;

		case 3:
			// Hero Falling (alive)
			batcher.draw(heroFall, hero.getX(), hero.getY(),
					hero.getWidth() / 2.0f, hero.getHeight() / 2.0f,
					hero.getWidth(), hero.getHeight(), 1, 1, 0);

			break;

		case 4:
			// Hero Jumping (alive)
			batcher.draw(heroJump, hero.getX(), hero.getY(),
					hero.getWidth() / 2.0f, hero.getHeight() / 2.0f,
					hero.getWidth(), hero.getHeight(), 1, 1, 0);

			break;

		}
	}

	/**
	 * Draws enemies.
	 * 
	 * @param runTime
	 */
	private void drawEnemy(float runTime) {

		// Wizard
		drawEnemyWizard(runTime, enemyOne, enemyWizardAnimation);
		drawEnemyWizard(runTime, enemyTwo, enemyWizardAnimation);
		drawEnemyWizard(runTime, enemyThree, enemyWizardAnimation);

		// Knight
		drawEnemyKnight(runTime, enemyFour, enemyKnightAnimation);
		drawEnemyKnight(runTime, enemyFive, enemyKnightAnimation);
		drawEnemyKnight(runTime, enemySix, enemyKnightAnimation);

		// Summoner
		drawEnemySummoner(runTime, enemySeven, enemySummonerAnimation);

	}

	/**
	 * Draws wizard.
	 * 
	 * @param runTime
	 * @param enemy
	 * @param animation
	 */
	private void drawEnemyWizard(float runTime, Enemy enemy, Animation animation) {
		if (!enemy.isAlive()) {
			batcher.draw(soulAnimation.getKeyFrame(runTime), enemy.getSoul()
					.getX(), enemy.getSoul().getY(), 15 / 2.0f, 15 / 2.0f, 15,
					15, 1, 1, 0);
		} else if (enemy.isVisible()) {
			batcher.draw(animation.getKeyFrame(runTime), enemy.getX(),
					enemy.getY(), enemy.getWidth() / 2.0f,
					enemy.getHeight() / 2.0f, enemy.getWidth(),
					enemy.getHeight(), 1, 1, 0);
		}

		// Draws projectiles.
		ArrayList<Projectile> projectiles = enemy.getProjectiles();
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = projectiles.get(i);
			if (p.isVisible()) {
				if (p.isMoving()) {
					batcher.draw(flameAnimation.getKeyFrame(runTime), p.getX(),
							p.getY(), p.getWidth() / 2.0f,
							p.getHeight() / 2.0f, p.getWidth(), p.getHeight(),
							1, 1, 0);
				} else {
					batcher.draw(lightAnimation.getKeyFrame(runTime),
							p.getX() + 1, p.getY() + 4,
							(p.getWidth() - 5) / 2.0f,
							(p.getHeight() - 5) / 2.0f, p.getWidth() - 5,
							p.getHeight() - 5, 1, 1, 0);
				}

			}
		}

	}

	/**
	 * Draws knights.
	 * 
	 * @param runTime
	 * @param enemy
	 * @param animation
	 */
	private void drawEnemyKnight(float runTime, Enemy enemy, Animation animation) {
		if (!enemy.isAlive()) {
			batcher.draw(soulAnimation.getKeyFrame(runTime), enemy.getSoul()
					.getX(), enemy.getSoul().getY(), 15 / 2.0f, 15 / 2.0f, 15,
					15, 1, 1, 0);

		} else if (enemy.isVisible()) {
			// Handles attack animation.
			if (enemy.isAttacking()) {
				TextureRegion frame = enemyKnightAttackAnimation
						.getKeyFrame(runTime);
				if (frame.equals(enemyKnightSwingTwo)) {
					enemy.swordThrust(true);
				} else {
					enemy.swordThrust(false);
				}
				batcher.draw(frame, enemy.getX(), enemy.getY(),
						enemy.getWidth() / 2.0f, enemy.getHeight() / 2.0f,
						enemy.getWidth(), enemy.getHeight(), 1, 1, 0);

			} else {
				batcher.draw(animation.getKeyFrame(runTime), enemy.getX(),
						enemy.getY(), enemy.getWidth() / 2.0f,
						enemy.getHeight() / 2.0f, enemy.getWidth(),
						enemy.getHeight(), 1, 1, 0);
			}
		}

	}

	/**
	 * Draws summoner.
	 * 
	 * @param runTime
	 * @param enemy
	 * @param animation
	 */
	private void drawEnemySummoner(float runTime, Enemy enemy,
			Animation animation) {
		if (!enemy.isAlive()) {
			batcher.draw(soulAnimation.getKeyFrame(runTime), enemy.getSoul()
					.getX(), enemy.getSoul().getY(), 15 / 2.0f, 15 / 2.0f, 15,
					15, 1, 1, 0);

		} else if (enemy.isVisible()) {
			batcher.draw(animation.getKeyFrame(runTime), enemy.getX(),
					enemy.getY(), enemy.getWidth() / 2.0f,
					enemy.getHeight() / 2.0f, enemy.getWidth(),
					enemy.getHeight(), 1, 1, 0);
		}

	}

	/**
	 * Draws score board.
	 */
	private void drawScore(float runTime) {
		if (myWorld.isReady()) {
			// TODO: Clean and fix starting instruction.
			// Draw text
			AssetLoader.font.draw(batcher, "--Instructions--", 65, 25);
			AssetLoader.font.draw(batcher, "1. Tap Once to Jump", 70, 35);
			AssetLoader.font.draw(batcher, "2. Tap Twice to Double Jump", 70, 45);
			AssetLoader.font.draw(batcher, "3. Jump on Enemies for Points", 70, 55);
			AssetLoader.font.draw(batcher, "4. Dodge Enemies", 70, 65);

			AssetLoader.font.draw(batcher, "--> Tap To Begin <--", 60, 85);

		} else {
			// Handles game over.
			if (myWorld.isGameOver() || myWorld.isHighScore()) {

				batcher.draw(scoreboard, 33, 0, 426 / 3.0f, 200 / 3.0f);

				String highScore = AssetLoader.getHighScore() + "";
				int totalScore = myWorld.getTotalScore();

				// Draw Text
				AssetLoader.font.draw(batcher, totalScore + "", 75, 47);
				AssetLoader.font.draw(batcher, highScore, 125, 47);

				// Handles high score.
				if (myWorld.isHighScore()) {
					batcher.draw(highscoreMark, 80, 43, 183 / 4.0f, 173 / 4.0f);
				}

				// Draw Medal
				if (totalScore > 150) {
					batcher.draw(medalAnimation.getKeyFrame(runTime), 87, 73,
							100 / 3.0f, 100 / 3.0f);
					batcher.draw(goldMedal, 87, 75, 100 / 3.0f, 100 / 3.0f);
				} else if (totalScore > 75) {
					batcher.draw(medalAnimation.getKeyFrame(runTime), 87, 73,
							100 / 3.0f, 100 / 3.0f);
					batcher.draw(silverMedal, 87, 75, 100 / 3.0f, 100 / 3.0f);
				} else if (totalScore > 25) {
					batcher.draw(medalAnimation.getKeyFrame(runTime), 87, 73,
							100 / 3.0f, 100 / 3.0f);
					batcher.draw(bronzeMedal, 87, 75, 100 / 3.0f, 100 / 3.0f);
				} else {
					batcher.draw(emptyMedal, 87, 75, 100 / 3.0f, 100 / 3.0f);
				}


				// Draw rate button prompt
				if(ratePrompt) {
					batcher.draw(ratePromptAnimation.getKeyFrame(runTime), 144, 42,
							110 / 3.0f, 110 / 3.0f);
				}

				// TODO: handle additional buttons.
				drawMenuUI(input.getMenuButtons());

			}

			// Draw Icon
			batcher.draw(kill, 0, 2, 13, 13);
			batcher.draw(distance, -2, 13, 13, 13);

			// Draw score and distance
			AssetLoader.font.draw(batcher, "" + myWorld.getScore(), 15, 5);
			AssetLoader.font.draw(batcher, "" + myWorld.getDistance(), 15, 15);

		}
	}

	/**
	 * Draws transitions.
	 * 
	 * @param delta
	 */
	private void drawTransition(float delta) {
		if (alpha.getValue() > 0) {
			manager.update(delta);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(transitionColor.r, transitionColor.g,
					transitionColor.b, alpha.getValue());
			shapeRenderer.rect(0, 0, 204, 136);
			shapeRenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);

		}
	}

	/**
	 * Sets up tween.
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param duration
	 */
	public void prepareTransition(int r, int g, int b, float duration) {
		transitionColor.set(r / 255.0f, g / 255.0f, b / 255.0f, 1);
		alpha.setValue(1);
		Tween.registerAccessor(Value.class, new ValueAccessor());
		manager = new TweenManager();
		Tween.to(alpha, -1, duration).target(0)
				.ease(TweenEquations.easeOutQuad).start(manager);
	}
	
	/**
	 * Toogle ratePrompt to true.
	 */
	public void showRatePrompt(boolean val) {
		ratePrompt = val;
	}
	
	//====================================================
	// TODO:For Checking Collision
	//====================================================
	/**
	 * Draws collision objects.
	 */
	private void drawCollisionCheck() {
		// Draw bounding collision
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);

		// Hero
		shapeRenderer.circle(hero.getBoundingBody().x,
				hero.getBoundingBody().y, hero.getBoundingBody().radius);

		if (hero.isJumping()) {
			shapeRenderer.setColor(Color.BLUE);
			shapeRenderer.rect(hero.getBoundingFeet().x,
					hero.getBoundingFeet().y,
					hero.getBoundingFeet().getWidth(), hero.getBoundingFeet()
							.getHeight());
		}

		// Wizard
		drawWizardCollision(enemyOne);
		drawWizardCollision(enemyTwo);
		drawWizardCollision(enemyThree);

		// Knight
		drawKnightCollision(enemyFour);
		drawKnightCollision(enemyFive);
		drawKnightCollision(enemySix);

		// Summoner
		shapeRenderer.circle(enemySeven.getBoundingCollisionCircle().x,
				enemySeven.getBoundingCollisionCircle().y,
				enemySeven.getBoundingCollisionCircle().radius);

		shapeRenderer.circle(enemySeven.getSoul().getBoundingCollision().x,
				enemySeven.getSoul().getBoundingCollision().y, enemySeven
						.getSoul().getBoundingCollision().radius);

		// Bar
		shapeRenderer.rect(0, 88, 204, 2);
		shapeRenderer.end();
	}

	/**
	 * Knight's collision
	 * 
	 * @param enemy
	 */
	private void drawKnightCollision(Enemy enemy) {
		shapeRenderer.circle(enemy.getBoundingCollisionCircle().x,
				enemy.getBoundingCollisionCircle().y,
				enemy.getBoundingCollisionCircle().radius);

		shapeRenderer.rect(enemy.getBoundingCollisionRect().x, enemy
				.getBoundingCollisionRect().y, enemy.getBoundingCollisionRect()
				.getWidth(), enemy.getBoundingCollisionRect().getHeight());

	}

	/**
	 * Wizard's collision
	 * 
	 * @param enemy
	 */
	private void drawWizardCollision(Enemy enemy) {
		shapeRenderer.circle(enemy.getBoundingCollisionCircle().x,
				enemy.getBoundingCollisionCircle().y,
				enemy.getBoundingCollisionCircle().radius);

		ArrayList<Projectile> projectiles = enemy.getProjectiles();
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = projectiles.get(i);
			if (p.isVisible()) {
				shapeRenderer.circle(p.getBoundingCollision().x,
						p.getBoundingCollision().y,
						p.getBoundingCollision().radius);
			}
		}
	}

}
