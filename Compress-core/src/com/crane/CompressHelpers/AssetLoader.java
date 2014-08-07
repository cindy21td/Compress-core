package com.crane.CompressHelpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {
	
	public static Texture texture, hero, enemy, element, boss;
	public static TextureRegion bg;
    

    public static Animation heroRunAnimation;
    public static TextureRegion heroRunOne, heroRunTwo, heroRunThree, heroRunFour;
    
    public static TextureRegion heroJump;
    
    
    public static TextureRegion enemyBlobOne, enemyBlobTwo, enemyBlobThree;
    public static Animation enemyBlobAnimation;
    
    public static TextureRegion enemyBatOne, enemyBatTwo, enemyBatThree;
    public static Animation enemyBatAnimation;
    
    public static TextureRegion enemyGoblinOne, enemyGoblinTwo, enemyGoblinThree;
    public static Animation enemyGoblinAnimation;
    
    public static TextureRegion scribble;
    
    public static TextureRegion elementTest;
    
    public static TextureRegion bossHead, bossChomp;
    
    
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
        texture = new Texture(Gdx.files.internal("Background Sprite ver three.png"));
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        bg = new TextureRegion(texture, 0, 0, 816, 544);
        bg.flip(false, true);
        
        
        loadHero();
        
        loadEnemy();     
               
        // Effect
        scribble = new TextureRegion(enemy, 300, 0, 100, 100);
        scribble.flip(false, true);
        
        element = new Texture(Gdx.files.internal("Element Sprite.png"));
        element.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        elementTest = new TextureRegion(element, 0, 0, 100, 100);
        elementTest.flip(false, true);
        
        
        boss = new Texture(Gdx.files.internal("Boss Sprite ver One.png"));
        boss.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        bossChomp = new TextureRegion(boss, 0, 0, 300, 272);
        bossChomp.flip(false, true);
        
        bossHead = new TextureRegion(boss, 0, 272, 300, 272);
        bossHead.flip(false, true);
        
        
        
        // Font
        font = new BitmapFont(Gdx.files.internal("text.fnt"));
        font.setScale(.1f, -.1f);
        
        
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
        element.dispose();
        
        boss.dispose();
        
        font.dispose();
        
    }
    
    // Load up Hero's Sprite
    private static void loadHero() {
    	
    	// Sprite File
    	hero = new Texture(Gdx.files.internal("Hero Sprite ver Three.png"));
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
        
        // Hero's running animation
        TextureRegion[] runningHero = {heroRunOne, heroRunTwo, heroRunThree, heroRunFour};
        heroRunAnimation = new Animation(0.1f, runningHero);
        heroRunAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        // Hero's jump Sprite
        heroJump = new TextureRegion(hero, 0, 100, 100, 100);
        heroJump.flip(false, true);        
        

    }
    
    private static void loadEnemy() {
    	
    	// Sprite File
    	enemy = new Texture(Gdx.files.internal("Enemy Sprite ver Two.png"));
        enemy.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        // Enemy Blob
        enemyBlobOne = new TextureRegion(enemy, 0, 0, 100, 100);
        enemyBlobOne.flip(false, true);
        
        enemyBlobTwo = new TextureRegion(enemy, 100, 0, 100, 100);
        enemyBlobTwo.flip(false, true);
        
        enemyBlobThree = new TextureRegion(enemy, 200, 0, 100, 100);
        enemyBlobThree.flip(false, true);
        
        // Enemy Blob Animation
        TextureRegion[] blob = {enemyBlobOne, enemyBlobTwo, enemyBlobThree};
        enemyBlobAnimation = new Animation(0.2f, blob);
        enemyBlobAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        
        // Enemy Bat
        enemyBatOne = new TextureRegion(enemy, 0, 100, 100, 100);
        enemyBatOne.flip(false, true);
        
        enemyBatTwo = new TextureRegion(enemy, 100, 100, 100, 100);
        enemyBatTwo.flip(false, true);
        
        enemyBatThree = new TextureRegion(enemy, 200, 100, 100, 100);
        enemyBatThree.flip(false, true);
        
        // Enemy Bat Animation
        TextureRegion[] bat = {enemyBatOne, enemyBatTwo, enemyBatThree};
        enemyBatAnimation = new Animation(0.2f, bat);
        enemyBatAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        
        
        // Enemy Goblin
        enemyGoblinOne = new TextureRegion(enemy, 0, 200, 100, 100);
        enemyGoblinOne.flip(false, true);
        
        enemyGoblinTwo = new TextureRegion(enemy, 100, 200, 100, 100);
        enemyGoblinTwo.flip(false, true);
        
        enemyGoblinThree = new TextureRegion(enemy, 200, 200, 100, 100);
        enemyGoblinThree.flip(false, true);
        
        // Enemy Goblin Animation
        TextureRegion[] goblin = {enemyGoblinOne, enemyGoblinTwo, enemyGoblinThree};
        enemyGoblinAnimation = new Animation(0.2f, goblin);
        enemyGoblinAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);


    }
	
}
