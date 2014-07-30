package com.crane.GameWorld;

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
import com.crane.GameObjects.Background;
import com.crane.GameObjects.Element;
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
	private Enemy enemyBlob, enemyBat, enemyGoblin;
	private Element elementTest;

	// Game Assets
	private TextureRegion bg;
	private TextureRegion bgFrontBody, bgBackBody;
	
	private Animation enemyBlobAnimation, enemyBatAnimation, enemyGoblinAnimation;
	
	private Animation heroRunAnimation;
	
	private TextureRegion heroJump, heroDash;
	
	private TextureRegion smoke;
	
	private TextureRegion elementTestBody;
	
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
		enemyBlob = scroller.getEnemyBlob();
		enemyBat = scroller.getEnemyBat();
		enemyGoblin = scroller.getEnemyGoblin();
		
		elementTest = scroller.getElement();
	}
	
	public void initAssets() {
		bg = AssetLoader.bg;
		bgFrontBody = bg;
		bgBackBody = bg;
		
		heroRunAnimation = AssetLoader.heroRunAnimation;
		
		heroJump = AssetLoader.heroJump;
		
		heroDash = AssetLoader.heroDash;
		
		
		enemyBlobAnimation = AssetLoader.enemyBlobAnimation;
		enemyBatAnimation = AssetLoader.enemyBatAnimation;
		enemyGoblinAnimation = AssetLoader.enemyGoblinAnimation;
		
		smoke = AssetLoader.smoke;
		
		elementTestBody = AssetLoader.elementTest;
	}
	
	// runTime is for animation (determining which frame to render);
	public void render(float runTime) {

        // Fill the entire screen with black, to prevent potential flickering.
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        
        /*
        // Begin ShapeRenderer
        shapeRenderer.begin(ShapeType.Filled);
        
        
        // Draw Ground
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(0, 120, 204, 16);
        
        
        
        
        // End ShapeRenderer
        shapeRenderer.end();
        */
        

        // Begin SpriteBatch
        batcher.begin();
        // Disable transparency 
        // This is good for performance when drawing images that do not require
        // transparency.
        batcher.disableBlending();
        batcher.draw(bgFrontBody, bgFront.getX(), 0, 204, 136);
        batcher.draw(bgBackBody, bgBack.getX(), 0, 204, 136);
        
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
        
        int val = 0;
        if(!hero.isAlive()) {
        	val = 3;
		} else if(hero.isJumping()) {
        	val = 1;
        } else if(hero.isDodging()) {
        	val = 2;
        }
        
        switch(val) {
        case 0:
        	batcher.draw(heroRunAnimation.getKeyFrame(runTime), hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, 0);
            
            break;
            
        case 1:
        	batcher.draw(heroJump, hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, 0);
        	        	
        	break;
        	
        case 2:
        	float rotation = 0;
        	if(!hero.isDodgingRight()) {
        		rotation = -45;
        	}
        	
        	batcher.draw(heroDash, hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, rotation);

        	break;
        	
        case 3:
        	batcher.draw(smoke, hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, 0);
        	
        	break;
        }
        
        /*if (hero.isAttacking()) {
            batcher.draw(heroBodySwipe, hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, 0);
        } else {
            batcher.draw(heroRunAnimation.getKeyFrame(runTime), hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, 0);
            
            batcher.draw(handAnimation.getKeyFrame(runTime), hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, 0);
        }*/
        
                
        // Draw enemy
        if(!enemyBlob.isAlive()) {
        	batcher.draw(smoke, enemyBlob.getX(), enemyBlob.getY(), 
            		enemyBlob.getWidth() / 2.0f, enemyBlob.getHeight() / 2.0f,
            		enemyBlob.getWidth(), enemyBlob.getHeight(), 1, 1, 0);

        } else {
        	batcher.draw(enemyBlobAnimation.getKeyFrame(runTime), enemyBlob.getX(), enemyBlob.getY(), 
        		enemyBlob.getWidth() / 2.0f, enemyBlob.getHeight() / 2.0f,
        		enemyBlob.getWidth(), enemyBlob.getHeight(), 1, 1, 0);
        }
        
        if(!enemyBat.isAlive()) {
        	batcher.draw(smoke, enemyBat.getX(), enemyBat.getY(), 
            		enemyBat.getWidth() / 2.0f, enemyBat.getHeight() / 2.0f,
            		enemyBat.getWidth(), enemyBat.getHeight(), 1, 1, 0);

        } else {
        	batcher.draw(enemyBatAnimation.getKeyFrame(runTime), enemyBat.getX(), enemyBat.getY(), 
        		enemyBat.getWidth() / 2.0f, enemyBat.getHeight() / 2.0f,
        		enemyBat.getWidth(), enemyBat.getHeight(), 1, 1, 0);
        }
        
        if(!enemyGoblin.isAlive()) {
        	batcher.draw(smoke, enemyGoblin.getX(), enemyGoblin.getY(), 
            		enemyGoblin.getWidth() / 2.0f, enemyGoblin.getHeight() / 2.0f,
            		enemyGoblin.getWidth(), enemyGoblin.getHeight(), 1, 1, 0);

        } else {
        	batcher.draw(enemyGoblinAnimation.getKeyFrame(runTime), enemyGoblin.getX(), enemyGoblin.getY(), 
        		enemyGoblin.getWidth() / 2.0f, enemyGoblin.getHeight() / 2.0f,
        		enemyGoblin.getWidth(), enemyGoblin.getHeight(), 1, 1, 0);
        }


        /*
        // Element
        if(!elementTest.isTaken()) {
        	batcher.draw(elementTestBody, elementTest.getX(), elementTest.getY(), 
        		elementTest.getWidth() / 2.0f, elementTest.getHeight() / 2.0f,
        		elementTest.getWidth(), elementTest.getHeight(), 1, 1, 0);
        } else {
        	batcher.draw(smoke, elementTest.getX(), elementTest.getY(), 
            		elementTest.getWidth() / 2.0f, elementTest.getHeight() / 2.0f,
            		elementTest.getWidth(), elementTest.getHeight(), 1, 1, 0);

        }
		*/
        
        
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
        // Draw score and distance
        AssetLoader.font.draw(batcher, "" + myWorld.getScore(), 10, 5);
        AssetLoader.font.draw(batcher, "" + myWorld.getDistance(), 10, 15);
        */
        
        
        // End SpriteBatch
        batcher.end();
        

        /*
        // Draw bounding collision
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(hero.getBoundingHead().x, hero.getBoundingHead().y, hero.getBoundingHead().radius);
        shapeRenderer.rect(hero.getBoundingBody().x, hero.getBoundingBody().y, hero.getBoundingBody().getWidth(), hero.getBoundingBody().getHeight());
        
        if(hero.isJumping()) {
        	shapeRenderer.setColor(Color.BLUE);
        	shapeRenderer.rect(hero.getBoundingFeet().x, hero.getBoundingFeet().y, hero.getBoundingFeet().getWidth(), hero.getBoundingFeet().getHeight());
        }
        
        shapeRenderer.rect(enemyBlob.getBoundingCollision().x, enemyBlob.getBoundingCollision().y, 
        		enemyBlob.getBoundingCollision().getWidth(), enemyBlob.getBoundingCollision().getHeight());
        
        shapeRenderer.rect(enemyBat.getBoundingCollision().x, enemyBat.getBoundingCollision().y, 
        		enemyBat.getBoundingCollision().getWidth(), enemyBat.getBoundingCollision().getHeight());

        shapeRenderer.rect(enemyGoblin.getBoundingCollision().x, enemyGoblin.getBoundingCollision().y, 
        		enemyGoblin.getBoundingCollision().getWidth(), enemyGoblin.getBoundingCollision().getHeight());
        
        shapeRenderer.circle(elementTest.getBoundingCollision().x, elementTest.getBoundingCollision().y, elementTest.getBoundingCollision().radius);

        
        shapeRenderer.end();
        */
	}

}
