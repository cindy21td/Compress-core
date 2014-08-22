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
import com.crane.GameObjects.Boss;
import com.crane.GameObjects.Enemy;
import com.crane.GameObjects.Hero;
import com.crane.GameObjects.Projectile;
import com.crane.GameObjects.ScrollHandler;
import com.crane.TweenAccessors.Value;
import com.crane.TweenAccessors.ValueAccessor;
import com.crane.ui.SimpleButton;

public class GameRenderer {

	private GameWorld myWorld;
	private OrthographicCamera cam;
	private ShapeRenderer shapeRenderer;

	private SpriteBatch batcher;

	private int midPointY;
	private int gameHeight;

	// Game Objects
	private Hero hero;
	private ScrollHandler scroller;
	private Background bgFront, bgBack;
	private Enemy enemyOne, enemyTwo, enemyThree, enemyFour, enemyFive,
			enemySix, enemySeven;
	private Boss boss;

	// Game Assets
	private TextureRegion bg;
	private TextureRegion bgFrontBody, bgBackBody;

	private Animation enemyWizardAnimation, enemyKnightAnimation,
			enemySummonerAnimation;
	private Animation flameAnimation;
	private Animation lightAnimation;

	private TextureRegion enemyKnightSwingTwo;
	private Animation enemyKnightAttackAnimation;

	private Animation soulAnimation;

	private Animation heroRunAnimation, heroStillAnimation;
	private TextureRegion heroJump, heroFall, heroDash;

	private TextureRegion bossOne;
	private Animation bossAnimation;

	// Tween stuff
	private TweenManager manager;
	private Value alpha = new Value();

	// Buttons
	private List<SimpleButton> menuButtons;

	public GameRenderer(GameWorld world, int gameHeight, int midPointY) {
		myWorld = world;

		this.midPointY = midPointY;
		this.gameHeight = gameHeight;

		//this.menuButtons = ((InputHandler) Gdx.input.getInputProcessor())
		//		.getMenuButtons();

		cam = new OrthographicCamera();
		cam.setToOrtho(true, 204, 136);

		batcher = new SpriteBatch();
		// Attach batcher to camera
		batcher.setProjectionMatrix(cam.combined);

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(cam.combined);

		initGameObjects();
		initAssets();
		setupTweens();
	}

	private void setupTweens() {
		Tween.registerAccessor(Value.class, new ValueAccessor());
		manager = new TweenManager();
		Tween.to(alpha, -1, .5f).target(0).ease(TweenEquations.easeOutQuad)
				.start(manager);
	}

	public void initGameObjects() {
		hero = myWorld.getHero();
		scroller = myWorld.getScroller();
		bgFront = scroller.getBgFront();
		bgBack = scroller.getBgBack();
		enemyOne = scroller.getEnemyOne();
		enemyTwo = scroller.getEnemyTwo();
		enemyThree = scroller.getEnemyThree();
		enemyFour = scroller.getEnemyFour();
		enemyFive = scroller.getEnemyFive();
		enemySix = scroller.getEnemySix();

		enemySeven = scroller.getEnemySeven();

		boss = scroller.getBoss();

	}

	public void initAssets() {
		bg = AssetLoader.bg;
		bgFrontBody = bg;
		bgBackBody = bg;

		heroRunAnimation = AssetLoader.heroRunAnimation;
		heroStillAnimation = AssetLoader.heroStillAnimation;
		heroJump = AssetLoader.heroJump;
		heroFall = AssetLoader.heroFall;
		heroDash = AssetLoader.heroDash;

		enemyWizardAnimation = AssetLoader.enemyWizardAnimation;
		enemyKnightAnimation = AssetLoader.enemyKnightAnimation;
		enemySummonerAnimation = AssetLoader.enemySummonerAnimation;

		bossOne = AssetLoader.bossOne;
		bossAnimation = AssetLoader.bossAnimation;

		flameAnimation = AssetLoader.flameAnimation;
		lightAnimation = AssetLoader.lightAnimation;

		enemyKnightSwingTwo = AssetLoader.enemyKnightSwingTwo;
		enemyKnightAttackAnimation = AssetLoader.enemyKnightAttackAnimation;

		soulAnimation = AssetLoader.soulAnimation;

	}

	public void renderMenu(float delta, InputHandler input) {
		batcher.begin();
		batcher.draw(bgFrontBody, 0, 0, 204, 136);
		

		drawMenuUI(input.getMenuButtons());
		batcher.end();
	}

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

		// Draw Boss
		if (boss.hasWon()) {
			batcher.draw(bossOne, boss.getX(), boss.getY(),
					boss.getWidth() / 2.0f, boss.getHeight() / 2.0f,
					boss.getWidth(), boss.getHeight(), 1, 1, 0);

		} else {
			batcher.draw(bossAnimation.getKeyFrame(runTime), boss.getX(),
					boss.getY(), boss.getWidth() / 2.0f,
					boss.getHeight() / 2.0f, boss.getWidth(), boss.getHeight(),
					1, 1, 0);
		}

		drawScore();
		
		// End SpriteBatch
		batcher.end();

		// Draw DashGauge (NEED FIX)
		int barHeight = hero.getDashGauge() * 10;

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(195, 10 + 50 - barHeight, 5, barHeight);
		shapeRenderer.end();

		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(195, 10, 5, 50);
		shapeRenderer.end();

		// Check Collision
		//drawCollisionCheck();

		drawTransition(delta);

	}

	private void drawMenuUI(List<SimpleButton> menuButtons) {
		// batcher.draw(AssetLoader.zbLogo, 136 / 2 - 56, midPointY - 50,
		// AssetLoader.zbLogo.getRegionWidth() / 1.2f,
		// AssetLoader.zbLogo.getRegionHeight() / 1.2f);

		for (SimpleButton button : menuButtons) {
			button.draw(batcher);
		}

	}

	private void drawTransition(float delta) {
		if (alpha.getValue() > 0) {
			manager.update(delta);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(1, 1, 1, alpha.getValue());
			shapeRenderer.rect(0, 0, 136, 300);
			shapeRenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);

		}
	}

	private void drawHero(float runTime) {

		// Check case
		int val = 0;
		if (!hero.isAlive()) {
			val = 2;
		} else if (hero.actionIsDisabled()) {
			val = 4;
		} else if (hero.isFalling()) {
			val = 3;
		} else if (hero.isJumping()) {
			val = 1;
		} else if (hero.isDashing()) {
			val = 5;
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

			// Hero Jumping (alive)
			batcher.draw(heroJump, hero.getX(), hero.getY(),
					hero.getWidth() / 2.0f, hero.getHeight() / 2.0f,
					hero.getWidth(), hero.getHeight(), 1, 1, 0);

			break;

		case 2:

			// Hero is dead
			batcher.draw(soulAnimation.getKeyFrame(runTime), hero.getX(),
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

			batcher.draw(heroStillAnimation.getKeyFrame(runTime), hero.getX(),
					hero.getY(), hero.getWidth() / 2.0f,
					hero.getHeight() / 2.0f, hero.getWidth(), hero.getHeight(),
					1, 1, 0);

			break;

		case 5:

			batcher.draw(heroDash, hero.getX(), hero.getY(),
					hero.getWidth() / 2.0f, hero.getHeight() / 2.0f,
					hero.getWidth(), hero.getHeight(), 1, 1, 0);

			break;

		}

	}

	private void drawEnemy(float runTime) {

		drawEnemyWizard(runTime, enemyOne, enemyWizardAnimation);
		drawEnemyWizard(runTime, enemyTwo, enemyWizardAnimation);
		drawEnemyWizard(runTime, enemyThree, enemyWizardAnimation);
		drawEnemyKnight(runTime, enemyFour, enemyKnightAnimation);
		drawEnemyKnight(runTime, enemyFive, enemyKnightAnimation);
		drawEnemyKnight(runTime, enemySix, enemyKnightAnimation);

		drawEnemySummoner(runTime, enemySeven, enemySummonerAnimation);

	}

	private void drawEnemyKnight(float runTime, Enemy enemy, Animation animation) {
		if (!enemy.isAlive()) {
			batcher.draw(soulAnimation.getKeyFrame(runTime), enemy.getSoul()
					.getX(), enemy.getSoul().getY(), 15 / 2.0f, 15 / 2.0f, 15,
					15, 1, 1, 0);

		} else if (enemy.isVisible()) {
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

		ArrayList<Projectile> projectiles = enemy.getProjectiles();
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = (Projectile) projectiles.get(i);
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

	private void drawScore() {

		// TEMPORARY CODE! We will fix this section later:
		if (myWorld.isReady()) {
			// Draw text
			AssetLoader.font.draw(batcher, "Touch me", 100, 75);
			
		} else {

			if (myWorld.isGameOver() || myWorld.isHighScore()) {

				if (myWorld.isGameOver()) {
					AssetLoader.font.draw(batcher, "Game Over", 100, 55);

					AssetLoader.font.draw(batcher, "High Score:", 100, 75);

					String highScore = AssetLoader.getHighScore() + "";

					// Draw text
					AssetLoader.font.draw(batcher, highScore, 150, 75);
				} else {
					AssetLoader.font.draw(batcher, "High Score!", 100, 55);
				}

				AssetLoader.font.draw(batcher, "Try again?", 100, 95);

				// Convert integer into String
				String score = myWorld.getScore() + "";

				// Draw text
				AssetLoader.font.draw(batcher, score, 100, 115);
			}

			// Draw score and distance
			AssetLoader.font.draw(batcher, "" + myWorld.getScore(), 10, 5);
			AssetLoader.font.draw(batcher, "" + myWorld.getDistance(), 10, 15);

		}

		/*
		 * // Draw score and distance AssetLoader.font.draw(batcher, "" +
		 * myWorld.getScore(), 10, 5); AssetLoader.font.draw(batcher, "" +
		 * myWorld.getDistance(), 10, 15);
		 */
	}

	private void drawCollisionCheck() {
		// Draw bounding collision
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);

		/*
		 * shapeRenderer.circle(hero.getBoundingBody().x,
		 * hero.getBoundingBody().y, hero.getBoundingBody().radius);
		 * 
		 * if (hero.isJumping()) { shapeRenderer.setColor(Color.BLUE);
		 * shapeRenderer.rect(hero.getBoundingFeet().x,
		 * hero.getBoundingFeet().y, hero.getBoundingFeet().getWidth(),
		 * hero.getBoundingFeet() .getHeight()); }
		 * 
		 * drawWizardCollision(enemyOne); drawWizardCollision(enemyTwo);
		 * drawWizardCollision(enemyThree);
		 * 
		 * drawKnightCollision(enemyFour); drawKnightCollision(enemyFive);
		 * drawKnightCollision(enemySix);
		 * 
		 * // Summoner
		 * shapeRenderer.circle(enemySeven.getBoundingCollisionCircle().x,
		 * enemySeven.getBoundingCollisionCircle().y,
		 * enemySeven.getBoundingCollisionCircle().radius);
		 * 
		 * shapeRenderer.circle(enemySeven.getSoul().getBoundingCollision().x,
		 * enemySeven.getSoul().getBoundingCollision().y,
		 * enemySeven.getSoul().getBoundingCollision().radius);
		 * 
		 * 
		 * // Boss shapeRenderer.rect(boss.getBoundingCollision().x, boss
		 * .getBoundingCollision().y, boss.getBoundingCollision() .getWidth(),
		 * boss.getBoundingCollision().getHeight());
		 */
		// Bar
		shapeRenderer.rect(0, 88, 204, 2);

		shapeRenderer.end();

	}

	private void drawKnightCollision(Enemy enemy) {
		shapeRenderer.circle(enemy.getSoul().getBoundingCollision().x, enemy
				.getSoul().getBoundingCollision().y, enemy.getSoul()
				.getBoundingCollision().radius);

		shapeRenderer.circle(enemy.getBoundingCollisionCircle().x,
				enemy.getBoundingCollisionCircle().y,
				enemy.getBoundingCollisionCircle().radius);

		shapeRenderer.rect(enemy.getBoundingCollisionRect().x, enemy
				.getBoundingCollisionRect().y, enemy.getBoundingCollisionRect()
				.getWidth(), enemy.getBoundingCollisionRect().getHeight());

	}

	private void drawWizardCollision(Enemy enemy) {
		shapeRenderer.circle(enemy.getSoul().getBoundingCollision().x, enemy
				.getSoul().getBoundingCollision().y, enemy.getSoul()
				.getBoundingCollision().radius);

		shapeRenderer.circle(enemy.getBoundingCollisionCircle().x,
				enemy.getBoundingCollisionCircle().y,
				enemy.getBoundingCollisionCircle().radius);

		ArrayList<Projectile> projectiles = enemy.getProjectiles();
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = (Projectile) projectiles.get(i);
			if (p.isVisible()) {
				shapeRenderer.circle(p.getBoundingCollision().x,
						p.getBoundingCollision().y,
						p.getBoundingCollision().radius);
			}
		}

	}

}
