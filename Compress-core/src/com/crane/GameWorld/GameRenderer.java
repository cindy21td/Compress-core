package com.crane.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.crane.CompressHelpers.AssetLoader;
import com.crane.GameObjects.Background;
import com.crane.GameObjects.Enemy;
import com.crane.GameObjects.Hero;
import com.crane.GameObjects.ScrollHandler;

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
	private Enemy enemyBlob;

	// Game Assets
	private TextureRegion bg;
	private TextureRegion heroBodySwipe;
	private TextureRegion bgFrontBody, bgBackBody;
	private TextureRegion enemyBlobBody;
	private Animation heroRunAnimation;
	
	//private TextureRegion one, two, three;

	public GameRenderer(GameWorld world, int gameHeight, int midPointY) {
		myWorld = world;
		
		this.midPointY = midPointY;
		this.gameHeight = gameHeight;
		
		cam = new OrthographicCamera();
		cam.setToOrtho(true, 204, 136);
		
		batcher = new SpriteBatch();
		// Attach batcher to camera
		batcher.setProjectionMatrix(cam.combined);
		
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(cam.combined);
		
		initGameObjects();
		initAssets();
	}
	
	public void initGameObjects() {
		hero = myWorld.getHero();
		scroller = myWorld.getScroller();
		bgFront = scroller.getBgFront();
		bgBack = scroller.getBgBack();
		enemyBlob = scroller.getEnemy();
	}
	
	public void initAssets() {
		bg = AssetLoader.bg;
		bgFrontBody = bg;
		bgBackBody = bg;
		heroBodySwipe = AssetLoader.heroSwipe;
		enemyBlobBody = AssetLoader.enemyBlob;
		
		heroRunAnimation = AssetLoader.heroRunAnimation;

	}
	
	// runTime is for animation (determining which frame to render);
	public void render(float runTime) {

        // Fill the entire screen with black, to prevent potential flickering.
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Begin ShapeRenderer
        shapeRenderer.begin(ShapeType.Filled);
        
        
        // Draw Ground
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(0, 120, 204, 16);
        
        // End ShapeRenderer
        shapeRenderer.end();
        

        // Begin SpriteBatch
        batcher.begin();
        // Disable transparency 
        // This is good for performance when drawing images that do not require
        // transparency.
        batcher.disableBlending();
        batcher.draw(bgFrontBody, bgFront.getX(), 0, 204, 120);
        batcher.draw(bgBackBody, bgBack.getX(), 0, 204, 120);
        
        batcher.enableBlending();
        
        /*
        TextureRegion drawHero;
        if(hero.isAttacking()) {
        	drawHero = heroBodySwipe;
        } else {
        	drawHero = heroBody;
        }
        
        batcher.draw(drawHero, hero.getX(), hero.getY(), 15, 23);
        */
        
        if (hero.isAttacking()) {
            batcher.draw(heroBodySwipe, hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, 0);
        } else {
            batcher.draw(heroRunAnimation.getKeyFrame(runTime), hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, 0);
        }
                
        // Draw enemy
        batcher.draw(enemyBlobBody, enemyBlob.getX(), enemyBlob.getY(), 15, 15);
        
        /* Draw bird at its coordinates. Retrieve the Animation object from AssetLoader
        // Pass in the runTime variable to get the current frame.
        batcher.draw(AssetLoader.birdAnimation.getKeyFrame(runTime),
                bird.getX(), bird.getY(), bird.getWidth(), bird.getHeight());
                
                if (bird.shouldntFlap()) {
            batcher.draw(birdMid, bird.getX(), bird.getY(),
                    bird.getWidth() / 2.0f, bird.getHeight() / 2.0f,
                    bird.getWidth(), bird.getHeight(), 1, 1, bird.getRotation());

        } else {
            batcher.draw(birdAnimation.getKeyFrame(runTime), bird.getX(),
                    bird.getY(), bird.getWidth() / 2.0f,
                    bird.getHeight() / 2.0f, bird.getWidth(), bird.getHeight(),
                    1, 1, bird.getRotation());
        }
        */
        
        
        // End SpriteBatch
        batcher.end();
        

		
	}

}
