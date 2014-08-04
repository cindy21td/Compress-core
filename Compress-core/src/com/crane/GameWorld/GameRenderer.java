package com.crane.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.crane.CompressHelpers.AssetLoader;
import com.crane.GameObjects.Background;
import com.crane.GameObjects.Element;
import com.crane.GameObjects.Enemy;
import com.crane.GameObjects.Hero;
import com.crane.GameObjects.ScrollHandler;
import com.crane.GameWorld.GameWorld.RunningState;

public class GameRenderer {
	
	private GameWorld myWorld;
	private OrthographicCamera cam;
	private ShapeRenderer shapeRenderer;
	
	private SpriteBatch batcher;
	
	private int midPointY;
	private int gameHeight;
	
	private RunningState stage;
	
	// Game Objects
	private Hero hero;
	private ScrollHandler scroller;
	private Background bgFront, bgBack;
	private Enemy enemyOne, enemyTwo, enemyThree;
	private Element elementTest;

	// Game Assets
	private TextureRegion bg;
	private TextureRegion bgFrontBody, bgBackBody;
	
	private Animation enemyBlobAnimation, enemyBatAnimation, enemyGoblinAnimation;
	
	private Animation heroRunAnimation;
	
	private TextureRegion heroJump;
	
	private TextureRegion scribble;
	
	private TextureRegion elementTestBody;
	
	//private TextureRegion one, two, three;

	public GameRenderer(GameWorld world, int gameHeight, int midPointY) {
		myWorld = world;
		
		stage = world.getStage();
		
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
		enemyOne = scroller.getEnemyOne();
		enemyTwo = scroller.getEnemyTwo();
		enemyThree = scroller.getEnemyThree();
		
		elementTest = scroller.getElement();
	}
	
	public void initAssets() {
		bg = AssetLoader.bg;
		bgFrontBody = bg;
		bgBackBody = bg;
		
		heroRunAnimation = AssetLoader.heroRunAnimation;
		
		heroJump = AssetLoader.heroJump;
		
		
		enemyBlobAnimation = AssetLoader.enemyBlobAnimation;
		enemyBatAnimation = AssetLoader.enemyBatAnimation;
		enemyGoblinAnimation = AssetLoader.enemyGoblinAnimation;
		
		scribble = AssetLoader.scribble;
		
		elementTestBody = AssetLoader.elementTest;
	}
	
	// runTime is for animation (determining which frame to render);
	public void render(float runTime) {
		
		stage = myWorld.getStage();
		

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
        
        
        
        drawHero(runTime);
        
        drawEnemy(runTime);   
                
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
        
        
        drawScore();
        
        // Draw current RunningState
        switch(stage) {
        
        case NORMAL:
        	AssetLoader.font.draw(batcher, "NORMAL", 100, 5);
        	break;
        case RUSH:
        	AssetLoader.font.draw(batcher, "RUSH", 100, 5);
        	break;
        case BOSS:
        	break;
        }
        
        
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
	
	public void setStage(RunningState newStage) {
		stage = newStage;
	}
	
	private void drawHero(float runTime) {
		
		// Check case
		int val = 0;
        if(!hero.isAlive()) {
        	val = 2;
		} else if(hero.isJumping()) {
        	val = 1;
        } 
        
        switch(val) {
        case 0:
        	
        	// Hero Running (alive)
        	batcher.draw(heroRunAnimation.getKeyFrame(runTime), hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, 0);
            
            break;
            
        case 1:
        	
        	// Hero Jumping (alive)
        	batcher.draw(heroJump, hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, 0);
        	        	
        	break;
        	        	
        case 2:
        	
        	// Hero is dead
        	batcher.draw(scribble, hero.getX(), hero.getY(), 
            		hero.getWidth() / 2.0f, hero.getHeight() / 2.0f, 
            		hero.getWidth(), hero.getHeight(), 1, 1, 0);
        	
        	break;
        }
        
  	}
	
	private void drawEnemy(float runTime) {
		
		drawEnemy(runTime, enemyOne, enemyBlobAnimation);
		drawEnemy(runTime, enemyTwo, enemyBatAnimation);
		drawEnemy(runTime, enemyThree, enemyGoblinAnimation);
		
		/*
        if(!enemyOne.isAlive()) {
        	batcher.draw(scribble, enemyOne.getX(), enemyOne.getY(), 
            		enemyOne.getWidth() / 2.0f, enemyOne.getHeight() / 2.0f,
            		enemyOne.getWidth(), enemyOne.getHeight(), 1, 1, 0);

        } else {
        	batcher.draw(enemyBlobAnimation.getKeyFrame(runTime), enemyOne.getX(), enemyOne.getY(), 
        		enemyOne.getWidth() / 2.0f, enemyOne.getHeight() / 2.0f,
        		enemyOne.getWidth(), enemyOne.getHeight(), 1, 1, 0);
        }
        
        if(!enemyTwo.isAlive()) {
        	batcher.draw(scribble, enemyTwo.getX(), enemyTwo.getY(), 
            		enemyTwo.getWidth() / 2.0f, enemyTwo.getHeight() / 2.0f,
            		enemyTwo.getWidth(), enemyTwo.getHeight(), 1, 1, 0);

        } else {
        	batcher.draw(enemyBatAnimation.getKeyFrame(runTime), enemyTwo.getX(), enemyTwo.getY(), 
        		enemyTwo.getWidth() / 2.0f, enemyTwo.getHeight() / 2.0f,
        		enemyTwo.getWidth(), enemyTwo.getHeight(), 1, 1, 0);
        }
        
        if(!enemyThree.isAlive()) {
        	batcher.draw(scribble, enemyThree.getX(), enemyThree.getY(), 
            		enemyThree.getWidth() / 2.0f, enemyThree.getHeight() / 2.0f,
            		enemyThree.getWidth(), enemyThree.getHeight(), 1, 1, 0);

        } else {
        	batcher.draw(enemyGoblinAnimation.getKeyFrame(runTime), enemyThree.getX(), enemyThree.getY(), 
        		enemyThree.getWidth() / 2.0f, enemyThree.getHeight() / 2.0f,
        		enemyThree.getWidth(), enemyThree.getHeight(), 1, 1, 0);
        }
        */

	}
	
	private void drawEnemy(float runTime, Enemy enemy, Animation animation) {
		if(!enemy.isAlive()) {
        	batcher.draw(scribble, enemy.getX(), enemy.getY(), 
            		enemy.getWidth() / 2.0f, enemy.getHeight() / 2.0f,
            		enemy.getWidth(), enemy.getHeight(), 1, 1, 0);

        } else {
        	batcher.draw(animation.getKeyFrame(runTime), enemy.getX(), enemy.getY(), 
        		enemy.getWidth() / 2.0f, enemy.getHeight() / 2.0f,
        		enemy.getWidth(), enemy.getHeight(), 1, 1, 0);
        }

	}
	
	private void drawScore() {
		
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
	}

}
