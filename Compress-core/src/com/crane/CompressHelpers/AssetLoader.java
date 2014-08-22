package com.crane.CompressHelpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {

	public static Texture texture, hero, enemy, boss;

	public static TextureRegion bg;

	// Hero
	public static Animation heroRunAnimation, heroStillAnimation;
	public static TextureRegion heroJump, heroFall, heroDash;

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

	// Boss
	public static TextureRegion bossOne;
	public static Animation bossAnimation;

	// Logo
	public static Texture logoTexture;
	public static TextureRegion logo;

	// Button
	public static Texture buttonTexture;
	public static TextureRegion buttonUp, buttonDown;

	// Font
	public static BitmapFont font;

	// Placeholder for Sounds
	// public static Sound example;

	// Storing Scores
	public static Preferences prefs;

	public AssetLoader() {
		// TODO Auto-generated constructor stub
	}

	public static void load() {

		loadBackground();

		loadHero();

		loadEnemy();

		loadBoss();

		loadEffect();

		loadUtilities();

		// Create (or retrieve existing) preferences file
		prefs = Gdx.app.getPreferences("Compress");

		if (!prefs.contains("highScore")) {
			prefs.putInteger("highScore", 0);
		}

		if (!prefs.contains("longestDistance")) {
			prefs.putInteger("longestDistance", 0);
		}
	}

	private static void loadBackground() {
		// Background Sprite
		texture = new Texture(Gdx.files.internal("Background.png"));
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		bg = new TextureRegion(texture, 0, 0, 816, 544);
		bg.flip(false, true);

	}

	// Load up Hero's Sprite
	private static void loadHero() {

		// Sprite File
		hero = new Texture(Gdx.files.internal("Hero Texture.png"));
		hero.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		// Each Frame for running animation
		TextureRegion runOne = new TextureRegion(hero, 0, 0, 100, 100);
		runOne.flip(false, true);

		TextureRegion runTwo = new TextureRegion(hero, 100, 0, 100, 100);
		runTwo.flip(false, true);

		TextureRegion runThree = new TextureRegion(hero, 200, 0, 100, 100);
		runThree.flip(false, true);

		TextureRegion runFour = new TextureRegion(hero, 300, 0, 100, 100);
		runFour.flip(false, true);

		// Hero's running animation
		TextureRegion[] runningHero = { runOne, runTwo, runThree, runFour };

		// Before : 0.1f
		heroRunAnimation = new Animation(0.1f, runningHero);
		heroRunAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

		TextureRegion stillOne = new TextureRegion(hero, 200, 100, 100, 100);
		stillOne.flip(false, true);

		TextureRegion stillTwo = new TextureRegion(hero, 300, 100, 100, 100);
		stillTwo.flip(false, true);

		TextureRegion[] stillHero = { stillOne, stillTwo };

		// Still Animation
		heroStillAnimation = new Animation(0.3f, stillHero);
		heroStillAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		// Hero's jump Sprite
		heroJump = new TextureRegion(hero, 0, 100, 100, 100);
		heroJump.flip(false, true);

		// Hero's fall Sprite
		heroFall = new TextureRegion(hero, 100, 100, 100, 100);
		heroFall.flip(false, true);

		// Hero's dash Sprite
		heroDash = new TextureRegion(hero, 0, 200, 100, 100);
		heroDash.flip(false, true);

	}

	private static void loadEnemy() {

		// Sprite File
		enemy = new Texture(Gdx.files.internal("Enemy Texture.png"));
		enemy.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		loadEnemyWizard();
		loadEnemyKnight();
		loadEnemySummoner();

	}

	private static void loadEnemyWizard() {

		// Run Animation
		enemyWizardAnimation = loadEnemyAnimation(0);

		// Flame
		TextureRegion flameOne = new TextureRegion(enemy, 300, 0, 50, 50);
		flameOne.flip(false, true);

		TextureRegion flameTwo = new TextureRegion(enemy, 350, 0, 50, 50);
		flameTwo.flip(false, true);

		TextureRegion flameThree = new TextureRegion(enemy, 300, 50, 50, 50);
		flameThree.flip(false, true);

		TextureRegion[] flame = { flameOne, flameTwo, flameThree };
		flameAnimation = new Animation(0.2f, flame);
		flameAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

		// Light
		TextureRegion lightOne = new TextureRegion(enemy, 400, 0, 50, 50);
		lightOne.flip(false, true);

		TextureRegion lightTwo = new TextureRegion(enemy, 450, 0, 50, 50);
		lightTwo.flip(false, true);

		TextureRegion lightThree = new TextureRegion(enemy, 400, 50, 50, 50);
		lightThree.flip(false, true);

		TextureRegion[] light = { lightOne, lightTwo, lightThree };
		lightAnimation = new Animation(0.2f, light);
		lightAnimation.setPlayMode(Animation.PlayMode.NORMAL);

	}

	private static void loadEnemyKnight() {
		// Run Animation
		enemyKnightAnimation = loadEnemyAnimation(100);

		// Attack
		TextureRegion enemyKnightSwingOne = new TextureRegion(enemy, 300, 100,
				100, 100);
		enemyKnightSwingOne.flip(false, true);

		enemyKnightSwingTwo = new TextureRegion(enemy, 400, 100, 100, 100);
		enemyKnightSwingTwo.flip(false, true);

		TextureRegion[] knightAttack = { enemyKnightSwingOne,
				enemyKnightSwingOne, enemyKnightSwingOne, enemyKnightSwingTwo };
		enemyKnightAttackAnimation = new Animation(0.2f, knightAttack);
		enemyKnightAttackAnimation.setPlayMode(Animation.PlayMode.LOOP);

	}

	private static void loadEnemySummoner() {
		// Run Animation
		enemySummonerAnimation = loadEnemyAnimation(200);
	}

	private static Animation loadEnemyAnimation(int y) {
		TextureRegion one = new TextureRegion(enemy, 0, y, 100, 100);
		one.flip(false, true);

		TextureRegion two = new TextureRegion(enemy, 100, y, 100, 100);
		two.flip(false, true);

		TextureRegion three = new TextureRegion(enemy, 200, y, 100, 100);
		three.flip(false, true);

		TextureRegion[] frames = { one, two, three };
		Animation animation = new Animation(0.2f, frames);
		animation.setPlayMode(Animation.PlayMode.LOOP);

		return animation;

	}

	public static void loadBoss() {

		boss = new Texture(Gdx.files.internal("Boss Texture.png"));
		boss.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		bossOne = new TextureRegion(boss, 0, 0, 314, 240);
		bossOne.flip(false, true);

		TextureRegion bossTwo = new TextureRegion(boss, 314, 0, 314, 240);
		bossTwo.flip(false, true);

		TextureRegion bossThree = new TextureRegion(boss, 0, 240, 314, 240);
		bossThree.flip(false, true);

		TextureRegion[] bossColl = { bossOne, bossTwo, bossThree };
		bossAnimation = new Animation(0.2f, bossColl);
		bossAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
	}
	
	private static void loadEffect() {
		// Effect
		TextureRegion soulOne = new TextureRegion(enemy, 0, 300, 100, 100);
		soulOne.flip(false, true);

		TextureRegion soulTwo = new TextureRegion(enemy, 100, 300, 100, 100);
		soulTwo.flip(false, true);

		TextureRegion soulThree = new TextureRegion(enemy, 200, 300, 100, 100);
		soulThree.flip(false, true);

		TextureRegion[] soul = { soulOne, soulTwo, soulThree };
		soulAnimation = new Animation(0.2f, soul);
		soulAnimation.setPlayMode(Animation.PlayMode.LOOP);

	}

	private static void loadUtilities() {
		// Font
		font = new BitmapFont(Gdx.files.internal("Trash3.fnt"));
		font.setScale(.15f, -.15f);

		logoTexture = new Texture(Gdx.files.internal("Logo ver One.png"));
		logoTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		logo = new TextureRegion(logoTexture, 0, 0, 204, 136);
		logo.flip(false, true);

		buttonTexture = new Texture(
				Gdx.files.internal("Button Sprite ver One.png"));
		buttonTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		buttonUp = new TextureRegion(buttonTexture, 0, 0, 100, 50);
		buttonUp.flip(false, true);

		buttonDown = new TextureRegion(buttonTexture, 0, 50, 100, 50);
		buttonDown.flip(false, true);

		// example =
		// Gdx.audio.newSound(Gdx.files.internal("path/to/sound/file");

	}

	

	public static void setHighScore(int val) {
		prefs.putInteger("highScore", val);
		prefs.flush();
	}

	public static int getHighScore() {
		return prefs.getInteger("highScore");
	}

	public static void setLongestDistance(int val) {
		prefs.putInteger("longestDistance", val);
		prefs.flush();
	}

	public static int getLongestDistance() {
		return prefs.getInteger("longestDistance");
	}

	public static void dispose() {
		// We must dispose of the texture when we are finished.
		texture.dispose();
		hero.dispose();
		enemy.dispose();
		boss.dispose();

		logoTexture.dispose();
		buttonTexture.dispose();
		
		font.dispose();

	}

}
