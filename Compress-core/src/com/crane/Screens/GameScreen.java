package com.crane.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.input.GestureDetector;
import com.crane.CompressHelpers.GestureHandler;
import com.crane.GameWorld.GameRenderer;
import com.crane.GameWorld.GameWorld;

public class GameScreen implements Screen {
	
	private GameWorld world;
	private GameRenderer renderer;
	
	private float runTime = 0;

	public GameScreen(GameWorld world) {
		
		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		float gameWidth = 204;
		float gameHeight = screenHeight / (screenWidth / gameWidth);
		
		int midPointY = (int) (gameHeight / 2);
		
		this.world = world;				// Initializes GameWorld
		renderer = new GameRenderer(world, (int) gameHeight, midPointY);			// Initializes GameRenderer
		
		GestureDetector gestureDetect = new GestureDetector(new GestureHandler(world, gameWidth, gameHeight));
		Gdx.input.setInputProcessor(gestureDetect);
	}

	@Override
	public void render(float delta) {
		runTime += delta;
		world.update(delta);					// GameWorld updates
		renderer.render(delta, runTime);						// GameRenderer renders
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("GameScreen - resizing");
	}

	@Override
	public void show() {
		System.out.println("GameScreen - show called");
	}

	@Override
	public void hide() {
		System.out.println("GameScreen - hide called");
	}

	@Override
	public void pause() {
		System.out.println("GameScreen - pause called");
	}

	@Override
	public void resume() {
		System.out.println("GameScreen - resume called");
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
