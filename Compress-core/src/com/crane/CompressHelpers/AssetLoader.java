package com.crane.CompressHelpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {

	public static Texture texture;

	// Background
	public static TextureRegion bgFront, bgBack;

	// Main Menu
	public static TextureRegion title, stillOne, stillTwo;
	public static Animation stillAnimation, startInstructionAnimation, backgroundMenuAnimation;

	// Hero
	public static Animation heroRunAnimation, heroStillAnimation;
	public static TextureRegion heroJump, heroFall;

	// Wizard
	public static Animation enemyWizardAnimation, flameAnimation,
			lightAnimation;

	// Knight
	public static TextureRegion enemyKnightSwingTwo;
	public static Animation enemyKnightAnimation, enemyKnightAttackAnimation;

	// Summoner
	public static Animation enemySummonerAnimation;

	// Soul
	public static Animation soulAnimation;

	// Logo
	public static Texture logoTexture;
	public static TextureRegion logo;

	// Button
	public static TextureRegion playButtonUp, playButtonDown, rateButtonUp, rateButtonDown;
	public static Animation ratePromptAnimation;

	// Icon
	public static TextureRegion kill, distance;

	// Score Board
	public static TextureRegion scoreboard, highscoreMark;
	
	// Medals
	public static TextureRegion emptyMedal, bronzeMedal, silverMedal, goldMedal;
	public static Animation medalAnimation;

	// Font
	public static BitmapFont font;

	// Sounds
	public static Sound deathSound, hitSound, jumpSound, knightAttack, runTheme, wizardAttack;

	// Storing Scores
	public static Preferences prefs;

	/**
	 * Constructor.
	 */
	public AssetLoader() {
	}

	/**
	 * Loads assets.
	 */
	public static void load() {
		
		texture = new Texture(Gdx.files.internal("texture.png"));
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		// Load each texture region.
		loadMainMenu();
		loadBackground();
		loadHero();
		loadEnemy();
		loadEffect();
		loadUtilities();
		loadMedals();
		loadSounds();

		// Create (or retrieve existing) preferences file
		prefs = Gdx.app.getPreferences("Compress");

//		if (!prefs.contains("highScore")) {
//			prefs.putInteger("highScore", 0);
//			prefs.flush();
//		}

	}

	/**
	 * Loads game backgrounds' sprite.
	 */
	private static void loadBackground() {
		// Background Sprite
		bgFront = new TextureRegion(texture, 0, 0, 612, 408);
		bgFront.flip(false, true);
		
		bgBack = new TextureRegion(texture, 612, 0, 612, 408);
		bgBack.flip(false, true);
	}

	/**
	 * Loads Hero Sprite
	 */
	private static void loadHero() {
		//===============
		//	Run Sprite
		//===============
		TextureRegion runOne = new TextureRegion(texture, 918, 408, 75, 75);
		runOne.flip(false, true);

		TextureRegion runTwo = new TextureRegion(texture, 993, 408, 75, 75);
		runTwo.flip(false, true);

		TextureRegion runThree = new TextureRegion(texture, 1068, 408, 75, 75);
		runThree.flip(false, true);

		TextureRegion runFour = new TextureRegion(texture, 1143, 408, 75, 75);
		runFour.flip(false, true);

		// Hero's running animation
		TextureRegion[] runningHero = { runOne, runTwo, runThree, runFour };

		heroRunAnimation = new Animation(0.1f, runningHero);
		heroRunAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

		//===============
		//	Still Sprite
		//===============
		TextureRegion stillOne = new TextureRegion(texture, 1068, 483, 75, 75);
		stillOne.flip(false, true);

		TextureRegion stillTwo = new TextureRegion(texture, 1143, 483, 75, 75);
		stillTwo.flip(false, true);

		TextureRegion[] stillHero = { stillOne, stillTwo };

		// Still Animation
		heroStillAnimation = new Animation(0.3f, stillHero);
		heroStillAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		//====================
		//	Jump/Fall Sprite
		//====================
		heroJump = new TextureRegion(texture, 918, 483, 75, 75);
		heroJump.flip(false, true);

		// Hero's fall Sprite
		heroFall = new TextureRegion(texture, 993, 483, 75, 75);
		heroFall.flip(false, true);

	}

	/**
	 * Loads Enemy Sprite
	 */
	private static void loadEnemy() {
		loadEnemyWizard();
		loadEnemyKnight();
		loadEnemySummoner();
	}

	/**
	 * Loads Wizard Sprite
	 */
	private static void loadEnemyWizard() {

		// Run Animation
		enemyWizardAnimation = loadEnemyAnimation(0);

		//================
		//	Flame Sprite
		//================
		TextureRegion flameOne = new TextureRegion(texture, 1449, 0, 37, 37);
		flameOne.flip(false, true);

		TextureRegion flameTwo = new TextureRegion(texture, 1486, 0, 37, 37);
		flameTwo.flip(false, true);

		TextureRegion flameThree = new TextureRegion(texture, 1449, 37, 37, 37);
		flameThree.flip(false, true);

		TextureRegion[] flame = { flameOne, flameTwo, flameThree };
		flameAnimation = new Animation(0.2f, flame);
		flameAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

		//================
		//	Light Sprite
		//================
		TextureRegion lightOne = new TextureRegion(texture, 1524, 0, 37, 37);
		lightOne.flip(false, true);

		TextureRegion lightTwo = new TextureRegion(texture, 1561, 0, 37, 37);
		lightTwo.flip(false, true);

		TextureRegion lightThree = new TextureRegion(texture, 1524, 37, 37, 37);
		lightThree.flip(false, true);

		TextureRegion[] light = { lightOne, lightTwo, lightThree };
		lightAnimation = new Animation(0.2f, light);
		lightAnimation.setPlayMode(Animation.PlayMode.NORMAL);

	}

	/**
	 * Loads Knight Sprite.
	 */
	private static void loadEnemyKnight() {
		// Run Animation
		enemyKnightAnimation = loadEnemyAnimation(75);

		// Attack
		TextureRegion enemyKnightSwingOne = new TextureRegion(texture, 1449, 75,
				75, 75);
		enemyKnightSwingOne.flip(false, true);

		enemyKnightSwingTwo = new TextureRegion(texture, 1524, 75, 75, 75);
		enemyKnightSwingTwo.flip(false, true);

		TextureRegion[] knightAttack = { enemyKnightSwingOne,
				enemyKnightSwingOne, enemyKnightSwingOne, enemyKnightSwingTwo };
		enemyKnightAttackAnimation = new Animation(0.2f, knightAttack);
		enemyKnightAttackAnimation.setPlayMode(Animation.PlayMode.LOOP);

	}

	/**
	 * Loads Summoner Sprite.
	 */
	private static void loadEnemySummoner() {
		// Run Animation
		enemySummonerAnimation = loadEnemyAnimation(150);
	}

	/**
	 * Loads enemy animation from the given y offset.
	 * @param y Sprite y offset.
	 * @return enemy generated Animation.
	 */
	private static Animation loadEnemyAnimation(int y) {
		TextureRegion one = new TextureRegion(texture, 1224, y, 75, 75);
		one.flip(false, true);

		TextureRegion two = new TextureRegion(texture, 1299, y, 75, 75);
		two.flip(false, true);

		TextureRegion three = new TextureRegion(texture, 1374, y, 75, 75);
		three.flip(false, true);

		TextureRegion[] frames = { one, two, three };
		Animation animation = new Animation(0.2f, frames);
		animation.setPlayMode(Animation.PlayMode.LOOP);

		return animation;

	}

	/**
	 * Loads Effects for enemies.
	 */
	private static void loadEffect() {
		// Effect
		TextureRegion soulOne = new TextureRegion(texture, 1224, 225, 75, 75);
		soulOne.flip(false, true);

		TextureRegion soulTwo = new TextureRegion(texture, 1299, 225, 75, 75);
		soulTwo.flip(false, true);

		TextureRegion soulThree = new TextureRegion(texture, 1374, 225, 75, 75);
		soulThree.flip(false, true);

		TextureRegion[] soul = { soulOne, soulTwo, soulThree };
		soulAnimation = new Animation(0.2f, soul);
		soulAnimation.setPlayMode(Animation.PlayMode.LOOP);

		//================
		//	Icon Sprite
		//================
		kill = new TextureRegion(texture, 768, 714, 75, 75);
		kill.flip(false, true);
	
		distance = new TextureRegion(texture, 843, 714, 75, 75);
		distance.flip(false, true);

	}

	/**
	 * Loads game's utilities (fonts, score board, button).
	 */
	private static void loadUtilities() {
		//===========
		//	Font
		//===========
		font = new BitmapFont(Gdx.files.internal("Trash3.fnt"));
		font.setScale(.15f, -.15f);

		//===============
		// Logo Texture
		//===============
		logoTexture = new Texture(Gdx.files.internal("libGdx.png"));
		logoTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		logo = new TextureRegion(logoTexture, 0, 0, 512, 256);
		logo.flip(false, false);

		//====================
		//	HighScore Sprite
		//====================
		highscoreMark = new TextureRegion(texture, 735, 840, 183, 173);
		highscoreMark.flip(false, true);
		
		//=================
		//	Button Sprite
		//=================
		// Replay Button
		playButtonUp = new TextureRegion(texture, 580, 965, 75, 45);
		playButtonUp.flip(false, true);

		playButtonDown = new TextureRegion(texture, 655, 965, 75, 45);
		playButtonDown.flip(false, true);
		
		// Rate Button
		rateButtonUp = new TextureRegion(texture, 580, 920, 75, 45);
		rateButtonUp.flip(false, true);

		rateButtonDown = new TextureRegion(texture, 655, 920, 75, 45);
		rateButtonDown.flip(false, true);
		
		// Rate Prompt
		TextureRegion ratePrompt1 = new TextureRegion(texture, 1545, 790, 110, 110);
		ratePrompt1.flip(false, true);

		TextureRegion ratePrompt2 = new TextureRegion(texture, 1545, 900, 110, 110);
		ratePrompt2.flip(false, true);
		
		TextureRegion[] ratePrompt = { ratePrompt1, ratePrompt2 };
		ratePromptAnimation = new Animation(0.4f, ratePrompt);
		ratePromptAnimation.setPlayMode(Animation.PlayMode.LOOP);

		//======================
		//	Score Board Sprite
		//======================
		scoreboard = new TextureRegion(texture, 1224, 300, 426, 200);
		scoreboard.flip(false, true);
	}
	
	/**
	 * Loads Main Menu Sprite
	 */
	private static void loadMainMenu() {
		//================
		//	Title Sprite
		//================
		title = new TextureRegion(texture, 918, 633, 442, 380);
		title.flip(false, true);

		//================
		//	Menu Sprite
		//================
		TextureRegion backgroundMenu1 = new TextureRegion(texture, 0, 408, 459, 306);
		backgroundMenu1.flip(false, true);
		
		TextureRegion backgroundMenu2 = new TextureRegion(texture, 459, 408, 459, 306);
		backgroundMenu2.flip(false, true);
		
		TextureRegion[] bm = { backgroundMenu1, backgroundMenu2 };
		backgroundMenuAnimation = new Animation(0.4f, bm);
		backgroundMenuAnimation.setPlayMode(Animation.PlayMode.LOOP);

		//=====================
		//	Instruction Sprite
		//=====================
		TextureRegion startInstruction = new TextureRegion(texture, 640, 790,
				278, 40);
		startInstruction.flip(false, true);

		TextureRegion blank = new TextureRegion(texture, 459, 714, 278, 40);
		blank.flip(false, true);

		TextureRegion[] start = { startInstruction, blank };
		startInstructionAnimation = new Animation(0.4f, start);
		startInstructionAnimation.setPlayMode(Animation.PlayMode.LOOP);

		//=================
		//	Still Sprite
		//=================
		stillOne = new TextureRegion(texture, 0, 714, 280, 300);
		stillOne.flip(false, true);

		stillTwo = new TextureRegion(texture, 300, 714, 280, 300);
		stillTwo.flip(false, true);

		TextureRegion[] still = { stillOne, stillTwo };
		stillAnimation = new Animation(0.3f, still);
		stillAnimation.setPlayMode(Animation.PlayMode.LOOP);
	}
	
	/**
	 * Loads medal sprite.
	 */
	private static void loadMedals() {
		// Empty medal
		emptyMedal = new TextureRegion(texture, 1544, 690,
				100, 100);
		emptyMedal.flip(false, true);
		
		// Bronze medal
		bronzeMedal = new TextureRegion(texture, 1430, 690,
				100, 100);
		bronzeMedal.flip(false, true);

		// Silver medal
		silverMedal = new TextureRegion(texture, 1544, 570,
				100, 100);
		silverMedal.flip(false, true);
		
		// Gold medal
		goldMedal = new TextureRegion(texture, 1430, 570,
				100, 100);
		goldMedal.flip(false, true);
		
		// Light effects
		TextureRegion light1 = new TextureRegion(texture, 1430, 795,
				100, 100);
		light1.flip(false, true);
		
		TextureRegion light2 = new TextureRegion(texture, 1430, 906,
				100, 100);
		light2.flip(false, true);

		TextureRegion[] light = { light1, light2 };
		medalAnimation = new Animation(0.6f, light);
		medalAnimation.setPlayMode(Animation.PlayMode.LOOP);
	}
	
	/**
	 * Loads sounds
	 */
	private static void loadSounds() {
		// example :
		// Gdx.audio.newSound(Gdx.files.internal("path/to/sound/file");
		
		deathSound = Gdx.audio.newSound(Gdx.files.internal("sound/Death Sound.m4a"));
		hitSound = Gdx.audio.newSound(Gdx.files.internal("sound/Hit Sound.m4a"));
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("sound/Jump Sound.m4a"));
		knightAttack = Gdx.audio.newSound(Gdx.files.internal("sound/Knight Attack.m4a"));
		runTheme = Gdx.audio.newSound(Gdx.files.internal("sound/theme.m4a"));
		wizardAttack = Gdx.audio.newSound(Gdx.files.internal("sound/Wizard Attack.m4a"));

	}

	/**
	 * Stores high score.
	 * @param val high score value.
	 */
	public static void setHighScore(int val) {
		prefs.putInteger("highScore", val);
		prefs.flush();
	}

	/**
	 * Gets the high score.
	 * @return the high score.
	 */
	public static int getHighScore() {
		return prefs.getInteger("highScore");
	}
	
	//TODO: rating???
	public static void setRateValue(int val) {
		prefs.putInteger("rateValue", val);
		prefs.flush();
	}
	
	public static int getRateValue() {
		return prefs.getInteger("rateValue");
	}

	public static void dispose() {
		texture.dispose();
		logoTexture.dispose();

		font.dispose();
		
		// Dispose Sounds
		deathSound.dispose();
		hitSound.dispose();
		jumpSound.dispose();
		knightAttack.dispose();
		runTheme.dispose();
		wizardAttack.dispose();
	}

}
