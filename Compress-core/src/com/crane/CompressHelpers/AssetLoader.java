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
    
    public static Animation heroRunAnimation, heroStillAnimation;
    public static TextureRegion heroRunOne, heroRunTwo, heroRunThree, heroRunFour;
    public static TextureRegion heroJump, heroFall;
    public static TextureRegion heroStillOne, heroStillTwo;
    public static TextureRegion heroDash;

     
    public static TextureRegion enemyWizardOne, enemyWizardTwo, enemyWizardThree;
    public static TextureRegion flameOne, flameTwo, flameThree;
    public static TextureRegion lightOne, lightTwo, lightThree;
    public static Animation enemyWizardAnimation;
    public static Animation flameAnimation;
    public static Animation lightAnimation;
    
    public static TextureRegion enemyKnightOne, enemyKnightTwo, enemyKnightThree;
    public static TextureRegion enemyKnightSwingOne, enemyKnightSwingTwo;
    public static Animation enemyKnightAnimation;
    public static Animation enemyKnightAttackAnimation;
    
    public static TextureRegion enemySummonerOne, enemySummonerTwo, enemySummonerThree;
    public static Animation enemySummonerAnimation;
    
    
    public static TextureRegion soulOne, soulTwo, soulThree;
    public static Animation soulAnimation;
    
    
    public static TextureRegion bossOne, bossTwo, bossThree;
    public static Animation bossAnimation;
    
    
    public static Texture logoTexture;
    public static TextureRegion logo;
    
    public static Texture buttonTexture;
    public static TextureRegion buttonUp, buttonDown;
    
    public static Texture pig;
    public static TextureRegion pigOne, pigTwo;
    public static Animation pigAni;
    
    
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
    	
    	// Background Sprite
        texture = new Texture(Gdx.files.internal("Background.png"));
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        bg = new TextureRegion(texture, 0, 0, 816, 544);
        bg.flip(false, true);
        
        
        loadHero();
        
        loadEnemy();
        
        loadBoss();
               
        // Effect
        soulOne = new TextureRegion(enemy, 0, 300, 100, 100);
        soulOne.flip(false, true);
        
        soulTwo = new TextureRegion(enemy, 100, 300, 100, 100);
        soulTwo.flip(false, true);
        
        soulThree = new TextureRegion(enemy, 200, 300, 100, 100);
        soulThree.flip(false, true);
        
        // Enemy Wizard Animation
        TextureRegion[] soul = {soulOne, soulTwo, soulThree};
        soulAnimation = new Animation(0.2f, soul);
        soulAnimation.setPlayMode(Animation.PlayMode.LOOP);

                
        // Font
        font = new BitmapFont(Gdx.files.internal("text.fnt"));
        font.setScale(.1f, -.1f);
        
        
        logoTexture = new Texture(Gdx.files.internal("Logo ver One.png"));
        logoTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        logo = new TextureRegion(logoTexture, 0, 0, 204, 136);
        logo.flip(false, true);
        
        
        buttonTexture = new Texture(Gdx.files.internal("Button Sprite ver One.png"));
        buttonTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        buttonUp = new TextureRegion(buttonTexture, 0, 0, 100, 50);
        buttonUp.flip(false, true);
        
        buttonDown = new TextureRegion(buttonTexture, 0, 50, 100, 50);
        buttonDown.flip(false, true);


        
        //example = Gdx.audio.newSound(Gdx.files.internal("path/to/sound/file");
        
        
        
        // Create (or retrieve existing) preferences file
        prefs = Gdx.app.getPreferences("Compress");

        if (!prefs.contains("highScore")) {
            prefs.putInteger("highScore", 0);
        }
        
        if (!prefs.contains("longestDistance")) {
            prefs.putInteger("longestDistance", 0);
        }
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
        
        font.dispose();
        
        
        
    }
    
    // Load up Hero's Sprite
    private static void loadHero() {
    	
    	// Sprite File
    	hero = new Texture(Gdx.files.internal("Hero Texture.png"));
        hero.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        // Each Frame for running animation
        heroRunOne = new TextureRegion(hero, 0, 0, 100, 100);
        heroRunOne.flip(false, true);
        
        heroRunTwo = new TextureRegion(hero, 100, 0, 100, 100);
        heroRunTwo.flip(false, true);
        
        heroRunThree = new TextureRegion(hero, 200, 0, 100, 100);
        heroRunThree.flip(false, true);
        
        heroRunFour = new TextureRegion(hero, 300, 0, 100, 100);
        heroRunFour.flip(false, true);
        
        
        heroDash = new TextureRegion(hero, 0, 200, 100, 100);
        heroDash.flip(false, true);
        
        
        
        // Hero's running animation
        TextureRegion[] runningHero = {heroRunOne, heroRunTwo,  
        		heroRunThree,  heroRunFour};
        
        // Before : 0.1f
        heroRunAnimation = new Animation(0.1f, runningHero);
        heroRunAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        // Hero's jump Sprite
        heroJump = new TextureRegion(hero, 0, 100, 100, 100);
        heroJump.flip(false, true);   
        
        heroFall = new TextureRegion(hero, 100, 100, 100, 100);
        heroFall.flip(false, true);
        
        
        heroStillOne = new TextureRegion(hero, 200, 100, 100, 100);
        heroStillOne.flip(false, true);   
        
        heroStillTwo = new TextureRegion(hero, 300, 100, 100, 100);
        heroStillTwo.flip(false, true); 
        
        TextureRegion[] stillHero = {heroStillOne, heroStillTwo};
        
        heroStillAnimation = new Animation(0.3f, stillHero);
        heroStillAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }
    
    private static void loadEnemy() {
    	
    	// Sprite File
        enemy = new Texture(Gdx.files.internal("Enemy Texture.png"));
        enemy.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        // Enemy Wizard
        enemyWizardOne = new TextureRegion(enemy, 0, 0, 100, 100);
        enemyWizardOne.flip(false, true);
        
        enemyWizardTwo = new TextureRegion(enemy, 100, 0, 100, 100);
        enemyWizardTwo.flip(false, true);
        
        enemyWizardThree = new TextureRegion(enemy, 200, 0, 100, 100);
        enemyWizardThree.flip(false, true);
        
        // Enemy Wizard Animation
        TextureRegion[] wizard = {enemyWizardOne, enemyWizardTwo, enemyWizardThree};
        enemyWizardAnimation = new Animation(0.2f, wizard);
        enemyWizardAnimation.setPlayMode(Animation.PlayMode.LOOP);
        
        
        flameOne = new TextureRegion(enemy, 300, 0, 50, 50);
        flameOne.flip(false, true);
        
        flameTwo = new TextureRegion(enemy, 350, 0, 50, 50);
        flameTwo.flip(false, true);
        
        flameThree = new TextureRegion(enemy, 300, 50, 50, 50);
        flameThree.flip(false, true);
        
        TextureRegion[] flame = {flameOne, flameTwo, flameThree};
        flameAnimation = new Animation(0.2f, flame);
        flameAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        
        
        lightOne = new TextureRegion(enemy, 400, 0, 50, 50);
        lightOne.flip(false, true);
        
        lightTwo = new TextureRegion(enemy, 450, 0, 50, 50);
        lightTwo.flip(false, true);
        
        lightThree = new TextureRegion(enemy, 400, 50, 50, 50);
        lightThree.flip(false, true);
        
        TextureRegion[] light = {lightOne, lightTwo, lightThree};
        lightAnimation = new Animation(0.2f, light);
        lightAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        
        // Enemy Knight
        enemyKnightOne = new TextureRegion(enemy, 0, 100, 100, 100);
        enemyKnightOne.flip(false, true);
        
        enemyKnightTwo = new TextureRegion(enemy, 100, 100, 100, 100);
        enemyKnightTwo.flip(false, true);
        
        enemyKnightThree = new TextureRegion(enemy, 200, 100, 100, 100);
        enemyKnightThree.flip(false, true);
        
        // Enemy Bat Animation
        TextureRegion[] knight = {enemyKnightOne, enemyKnightTwo, enemyKnightThree};
        enemyKnightAnimation = new Animation(0.2f, knight);
        enemyKnightAnimation.setPlayMode(Animation.PlayMode.LOOP);
        
        
        enemyKnightSwingOne = new TextureRegion(enemy, 300, 100, 100, 100);
        enemyKnightSwingOne.flip(false, true);
        
        enemyKnightSwingTwo = new TextureRegion(enemy, 400, 100, 100, 100);
        enemyKnightSwingTwo.flip(false, true);
        
        TextureRegion[] knightAttack = {enemyKnightSwingOne, enemyKnightSwingOne, enemyKnightSwingOne, enemyKnightSwingTwo};
        enemyKnightAttackAnimation = new Animation(0.2f, knightAttack);
        enemyKnightAttackAnimation.setPlayMode(Animation.PlayMode.LOOP);

        
        
        // Enemy Summoner
        enemySummonerOne = new TextureRegion(enemy, 0, 200, 100, 100);
        enemySummonerOne.flip(false, true);
        
        enemySummonerTwo = new TextureRegion(enemy, 100, 200, 100, 100);
        enemySummonerTwo.flip(false, true);
        
        enemySummonerThree = new TextureRegion(enemy, 200, 200, 100, 100);
        enemySummonerThree.flip(false, true);
        
        // Enemy Summoner Animation
        TextureRegion[] summoner = {enemySummonerOne, enemySummonerTwo, enemySummonerThree};
        enemySummonerAnimation = new Animation(0.2f, summoner);
        enemySummonerAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);


    }
    
    public static void loadBoss() {

        boss = new Texture(Gdx.files.internal("Boss Texture.png"));
        boss.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        bossOne = new TextureRegion(boss, 0, 0, 314, 240);
        bossOne.flip(false, true);
        
        bossTwo = new TextureRegion(boss, 314, 0, 314, 240);
        bossTwo.flip(false, true);
        
        bossThree = new TextureRegion(boss, 0, 240, 314, 240);
        bossThree.flip(false, true);

        TextureRegion[] bossColl = {bossOne, bossTwo, bossThree};
        bossAnimation = new Animation(0.2f, bossColl);
        bossAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }
	
}
