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
	
	public static Texture enemyT;
	
	
	public static TextureRegion bg;
    
    public static Animation heroRunAnimation, heroStillAnimation;
    public static TextureRegion heroRunOne, heroRunTwo, heroRunThree, heroRunFour;
    public static TextureRegion heroRunInOne, heroRunInTwo, heroRunInThree, heroRunInFour;
    public static TextureRegion heroJump, heroFall;
    public static TextureRegion heroStillOne, heroStillTwo;

     
    public static TextureRegion enemyWizardOne, enemyWizardTwo, enemyWizardThree;
    public static Animation enemyWizardAnimation;
    
    public static TextureRegion enemyKnightOne, enemyKnightTwo, enemyKnightThree;
    public static Animation enemyKnightAnimation;
    
    public static TextureRegion enemyGoblinOne, enemyGoblinTwo, enemyGoblinThree;
    public static Animation enemyGoblinAnimation;
    
    public static TextureRegion scribble;
    
    public static TextureRegion bossHead, bossChomp;
    
    
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
        scribble = new TextureRegion(enemyT, 300, 0, 100, 100);
        scribble.flip(false, true);
        
                
        // Font
        font = new BitmapFont(Gdx.files.internal("text.fnt"));
        font.setScale(.1f, -.1f);
        
        
        logoTexture = new Texture(Gdx.files.internal("Logo ver One.png"));
        logoTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        logo = new TextureRegion(logoTexture, 0, 0, 204, 136);
        logo.flip(false, true);
        
        
        buttonTexture = new Texture(Gdx.files.internal("Button Sprite ver One.png"));
        buttonTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        buttonUp = new TextureRegion(logoTexture, 0, 0, 100, 50);
        buttonUp.flip(false, true);
        
        buttonDown = new TextureRegion(logoTexture, 0, 50, 100, 50);
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
        
        
        enemyT.dispose();
        
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
        
        
        heroRunInOne = new TextureRegion(hero, 0, 200, 100, 100);
        heroRunInOne.flip(false, true);
        
        heroRunInTwo = new TextureRegion(hero, 100, 200, 100, 100);
        heroRunInTwo.flip(false, true);
        
        heroRunInThree = new TextureRegion(hero, 200, 200, 100, 100);
        heroRunInThree.flip(false, true);
        
        heroRunInFour = new TextureRegion(hero, 300, 200, 100, 100);
        heroRunInFour.flip(false, true);

        
        
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
    	enemyT = new Texture(Gdx.files.internal("Enemy Sprite ver Two.png"));
        enemyT.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        
        enemy = new Texture(Gdx.files.internal("Enemy Texture.png"));
        enemy.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        // Enemy Whisp
        enemyWizardOne = new TextureRegion(enemy, 0, 0, 100, 100);
        enemyWizardOne.flip(false, true);
        
        enemyWizardTwo = new TextureRegion(enemy, 100, 0, 100, 100);
        enemyWizardTwo.flip(false, true);
        
        enemyWizardThree = new TextureRegion(enemy, 200, 0, 100, 100);
        enemyWizardThree.flip(false, true);
        
        // Enemy Whisp Animation
        TextureRegion[] wizard = {enemyWizardOne, enemyWizardTwo, enemyWizardThree};
        enemyWizardAnimation = new Animation(0.2f, wizard);
        enemyWizardAnimation.setPlayMode(Animation.PlayMode.LOOP);

        
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
        
        
        // Enemy Goblin
        enemyGoblinOne = new TextureRegion(enemyT, 0, 200, 100, 100);
        enemyGoblinOne.flip(false, true);
        
        enemyGoblinTwo = new TextureRegion(enemyT, 100, 200, 100, 100);
        enemyGoblinTwo.flip(false, true);
        
        enemyGoblinThree = new TextureRegion(enemyT, 200, 200, 100, 100);
        enemyGoblinThree.flip(false, true);
        
        // Enemy Goblin Animation
        TextureRegion[] goblin = {enemyGoblinOne, enemyGoblinTwo, enemyGoblinThree};
        enemyGoblinAnimation = new Animation(0.2f, goblin);
        enemyGoblinAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);


    }
    
    public static void loadBoss() {

        boss = new Texture(Gdx.files.internal("Boss Sprite ver One.png"));
        boss.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        bossChomp = new TextureRegion(boss, 0, 0, 300, 272);
        bossChomp.flip(false, true);
        
        bossHead = new TextureRegion(boss, 0, 272, 300, 272);
        bossHead.flip(false, true);
        
    }
	
}
