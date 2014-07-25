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
    public static TextureRegion hero, heroRun;
    
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

        hero = new TextureRegion(texture, 0, 0, 15, 23);
        hero.flip(false, true);
        
        heroRun = new TextureRegion(texture, 70, 0, 15, 23);
        heroRun.flip(false, true);

        
        heroSwipe = new TextureRegion(texture, 40, 0 , 20, 20);
        heroSwipe.flip(false, true);

        enemyBlob = new TextureRegion(texture, 20, 0, 15, 15);
        enemyBlob.flip(false, true);

        
        //three = new TextureRegion(texture, 80, 72, 28, 36);
        //three.flip(false, true);

        // Texture
        TextureRegion[] runningHero = { hero, heroRun, hero };
        heroRunAnimation = new Animation(0.1f, runningHero);
        heroRunAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        
        
        //example = Gdx.audio.newSound(Gdx.files.internal("path/to/sound/file");
        

    }
    
    public static void dispose() {
        // We must dispose of the texture when we are finished.
        texture.dispose();
    }
	
}
