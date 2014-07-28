package com.crane.CompressHelpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {
	
	public static Texture texture;
	public static TextureRegion bg;
    

    public static Animation heroRunAnimation;
    public static TextureRegion heroRunOne, heroRunTwo, heroRunThree, heroRunFour;
    
    public static TextureRegion heroJump;
    
    public static TextureRegion heroDash;
    
    
    public static TextureRegion enemyBlobOne, enemyBlobTwo, enemyBlobThree;
    public static Animation enemyBlobAnimation;
    
    public static TextureRegion enemyBatOne, enemyBatTwo, enemyBatThree;
    public static Animation enemyBatAnimation;
    
    public static TextureRegion smoke;
    
    public static TextureRegion elementTest;
    
    
    // Placeholder for Sounds
    // public static Sound example;

    public AssetLoader() {
		// TODO Auto-generated constructor stub
    }
  
    public static void load() {

        texture = new Texture(Gdx.files.internal("SpriteTexture.png"));
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        bg = new TextureRegion(texture, 0, 40, 204, 136);
        bg.flip(false, true);
        
        
        Texture hero = new Texture(Gdx.files.internal("Hero Sprite.png"));
        hero.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        heroRunOne = new TextureRegion(hero, 0, 0, 100, 100);
        heroRunOne.flip(false, true);
        
        heroRunTwo = new TextureRegion(hero, 100, 0, 100, 100);
        heroRunTwo.flip(false, true);
        
        heroRunThree = new TextureRegion(hero, 200, 0, 100, 100);
        heroRunThree.flip(false, true);
        
        heroRunFour = new TextureRegion(hero, 300, 0, 100, 100);
        heroRunFour.flip(false, true);
        
        TextureRegion[] runningHero = {heroRunOne, heroRunTwo, heroRunThree, heroRunFour};
        heroRunAnimation = new Animation(0.1f, runningHero);
        heroRunAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        
        heroJump = new TextureRegion(hero, 0, 100, 100, 100);
        heroJump.flip(false, true);        
        
        heroDash = new TextureRegion(hero, 100, 100, 100, 100);
        heroDash.flip(false, true);
        
        
        Texture enemy = new Texture(Gdx.files.internal("Enemy Sprite.png"));
        enemy.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        enemyBlobOne = new TextureRegion(enemy, 0, 0, 100, 100);
        enemyBlobOne.flip(false, true);
        
        enemyBlobTwo = new TextureRegion(enemy, 100, 0, 100, 100);
        enemyBlobTwo.flip(false, true);
        
        enemyBlobThree = new TextureRegion(enemy, 200, 0, 100, 100);
        enemyBlobThree.flip(false, true);
        
        TextureRegion[] blob = {enemyBlobOne, enemyBlobTwo, enemyBlobThree};
        enemyBlobAnimation = new Animation(0.2f, blob);
        enemyBlobAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        
        enemyBatOne = new TextureRegion(enemy, 0, 100, 100, 100);
        enemyBatOne.flip(false, true);
        
        enemyBatTwo = new TextureRegion(enemy, 100, 100, 100, 100);
        enemyBatTwo.flip(false, true);
        
        enemyBatThree = new TextureRegion(enemy, 200, 100, 100, 100);
        enemyBatThree.flip(false, true);
        
        TextureRegion[] bat = {enemyBatOne, enemyBatTwo, enemyBatThree};
        enemyBatAnimation = new Animation(0.2f, bat);
        enemyBatAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        

        
        // Effect
        smoke = new TextureRegion(enemy, 300, 0, 100, 100);
        smoke.flip(false, true);
        
        Texture element = new Texture(Gdx.files.internal("Element Sprite.png"));
        element.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        elementTest = new TextureRegion(element, 0, 0, 100, 100);
        elementTest.flip(false, true);
        
        
        //example = Gdx.audio.newSound(Gdx.files.internal("path/to/sound/file");
        

    }
    
    public static void dispose() {
        // We must dispose of the texture when we are finished.
        texture.dispose();
    }
	
}
