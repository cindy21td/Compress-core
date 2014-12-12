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

	private float runTime = 0;

	private Compress game;
	private InputHandler inputDetect;

	public MainMenuScreen(Compress game) {
		this.game = game;

		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		float gameWidth = 204;
		float gameHeight = screenHeight / (screenWidth / gameWidth);

		int midPointY = (int) (gameHeight / 2);

		world = new GameWorld(game, midPointY); // Initializes GameWorld
		renderer = new GameRenderer(world, (int) gameHeight, midPointY); // Initializes
																			// GameRenderer
		inputDetect = new InputHandler(game, world, renderer, screenWidth
				/ gameWidth, screenHeight / gameHeight);

		renderer.setInputDetect(inputDetect);

		Gdx.input.setInputProcessor(inputDetect);
	}

	@Override
	public void render(float delta) {
		runTime += delta;
		renderer.renderMenu(runTime);
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("MenuScreen - resizing");
	}

	@Override
	public void show() {
		System.out.println("MenuScreen - show called");
	}

	@Override
	public void hide() {
		System.out.println("MenuScreen - hide called");
	}

	@Override
	public void pause() {
		System.out.println("MenuScreen - pause called");
	}

	@Override
	public void resume() {
		System.out.println("MenuScreen - resume called");
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
