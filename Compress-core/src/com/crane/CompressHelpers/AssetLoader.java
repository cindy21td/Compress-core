package com.crane.CompressHelpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {
	
	public static Texture texture;
	public static TextureRegion bg;
    public static TextureRegion heroSwipe;
    
    public static TextureRegion enemyBlob;

    public static Animation heroRunAnimation;
    public static TextureRegion heroRunOne, heroRunTwo, heroRunThree, heroRunFour;
    
    public static Animation handAnimation;
    public static TextureRegion handOne, handTwo, handThree, handFour;
    
    public static TextureRegion testOne, testTwo, testThree, testFour;
    public static Animation testRun;
    
    public static TextureRegion heroJump, armJump;
    
    public static TextureRegion armAttackOne, armAttackTwo, armAttackThree, armAttackFour;
    public static Animation armAttack;
    
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
        
        heroSwipe = new TextureRegion(texture, 40, 0 , 20, 20);
        heroSwipe.flip(false, true);
        
        
        Texture hero = new Texture(Gdx.files.internal("Hero Sprite.png"));
        hero.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        heroRunOne = new TextureRegion(hero, 300, 0, 100, 100);
        heroRunOne.flip(false, true);
        
        heroRunTwo = new TextureRegion(hero, 200, 0, 100, 100);
        heroRunTwo.flip(false, true);
        
        heroRunThree = new TextureRegion(hero, 100, 0, 100, 100);
        heroRunThree.flip(false, true);
        
        heroRunFour = new TextureRegion(hero, 0, 0, 100, 100);
        heroRunFour.flip(false, true);
        
        TextureRegion[] runningHero = {heroRunOne, heroRunTwo, heroRunThree, heroRunFour};
        heroRunAnimation = new Animation(0.1f, runningHero);
        heroRunAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        
        
        handOne = new TextureRegion(hero, 300, 100, 100, 100);
        handOne.flip(false, true);
        
        handTwo = new TextureRegion(hero, 200, 100, 100, 100);
        handTwo.flip(false, true);
        
        handThree = new TextureRegion(hero, 100, 100, 100, 100);
        handThree.flip(false, true);
        
        handFour = new TextureRegion(hero, 0, 100, 100, 100);
        handFour.flip(false, true);

        TextureRegion[] handHero = {handOne, handTwo, handThree, handFour};
        handAnimation = new Animation(0.1f, handHero);
        handAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        
        heroJump = new TextureRegion(hero, 0, 200, 100, 100);
        heroJump.flip(false, true);
        
        armJump = new TextureRegion(hero, 100, 200, 100, 100);
        armJump.flip(false, true);
        
        
        armAttackOne = new TextureRegion(hero, 0, 300, 100, 100);
        armAttackOne.flip(false, true);
        
        armAttackTwo = new TextureRegion(hero, 100, 300, 100, 100);
        armAttackTwo.flip(false, true);
        
        armAttackThree = new TextureRegion(hero, 200, 300, 100, 100);
        armAttackThree.flip(false, true);
        

        TextureRegion[] attackHero = {armAttackOne, armAttackTwo, armAttackThree, armAttackTwo};
        armAttack = new Animation(0.05f, attackHero);
        armAttack.setPlayMode(Animation.PlayMode.LOOP);

        
        enemyBlob = new TextureRegion(texture, 20, 0, 15, 15);
        enemyBlob.flip(false, true);

        
        
        //example = Gdx.audio.newSound(Gdx.files.internal("path/to/sound/file");
        

    }
    
    public static void dispose() {
        // We must dispose of the texture when we are finished.
        texture.dispose();
    }
	
}
