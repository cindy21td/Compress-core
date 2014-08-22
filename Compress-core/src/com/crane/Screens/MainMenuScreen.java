package com.crane.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.crane.CompressHelpers.InputHandler;
import com.crane.GameWorld.GameRenderer;
import com.crane.GameWorld.GameWorld;
import com.crane.compress.Compress;

public class MainMenuScreen implements Screen {

	private GameWorld world;
	private GameRenderer renderer;
	
	private Compress game;

	public MainMenuScreen(Compress game) {
		this.game = game;
		
		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		float gameWidth = 204;
		float gameHeight = screenHeight / (screenWidth / gameWidth);
		
		int midPointY = (int) (gameHeight / 2);
		
		world = new GameWorld(midPointY);				// Initializes GameWorld
		renderer = new GameRenderer(world, (int) gameHeight, midPointY);			// Initializes GameRenderer
		
        Gdx.input.setInputProcessor(new InputHandler(game, world, screenWidth / gameWidth, screenHeight / gameHeight));
	}

	@Override
	public void render(float delta) {
		renderer.renderMenu(delta, (InputHandler) Gdx.input.getInputProcessor());
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
