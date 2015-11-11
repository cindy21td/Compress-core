package com.crane.CompressHelpers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.InputProcessor;
import com.crane.GameObjects.Hero;
import com.crane.GameWorld.GameRenderer;
import com.crane.GameWorld.GameWorld;
import com.crane.compress.Compress;
import com.crane.ui.SimpleButton;

public class InputHandler implements InputProcessor {

	private GameWorld myWorld;
	private GameRenderer renderer;
	private Hero hero;

	private List<SimpleButton> menuButtons;

	private SimpleButton replayButton, rateButton;

	private float scaleFactorX;
	private float scaleFactorY;

	private Compress game;

	public InputHandler(Compress game, GameWorld myWorld,
			GameRenderer renderer, float scaleFactorX, float scaleFactorY) {
		this.game = game;
		this.myWorld = myWorld;
		this.renderer = renderer;
		this.hero = myWorld.getHero();

		int midPointY = myWorld.getMidPointY();

		this.scaleFactorX = scaleFactorX;
		this.scaleFactorY = scaleFactorY;

		menuButtons = new ArrayList<SimpleButton>();

		replayButton = new SimpleButton(140 / 3f, 220 / 3f, 100 / 3f, 70 / 3f,
				AssetLoader.playButtonUp, AssetLoader.playButtonDown);
		
		rateButton = new SimpleButton(380 / 3f, 217 / 3f, 100 / 3f, 70 / 3f,
				AssetLoader.rateButtonUp, AssetLoader.rateButtonDown);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenX = scaleX(screenX);
		screenY = scaleY(screenY);
		System.out.println(screenX + " " + screenY);
		if (myWorld.isGameOver() || myWorld.isHighScore()) {
			replayButton.isTouchDown(screenX, screenY);
			rateButton.isTouchDown(screenX, screenY);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screenX = scaleX(screenX);
		screenY = scaleY(screenY);

		if (myWorld.isMenu()) {

			myWorld.ready(renderer, this);

			// Add buttons
			menuButtons.add(replayButton);
			menuButtons.add(rateButton);

			return true;

		}

		if (myWorld.isReady()) {

			myWorld.start();

		}

		if (myWorld.isRunning()) {
			hero.onClick();
		}

		if (myWorld.isGameOver() || myWorld.isHighScore()) {
			if (replayButton.isTouchUp(screenX, screenY)) {
				myWorld.restart();
				return true;
			}
			
			// Rate
			if (rateButton.isTouchUp(screenX, screenY)) {
				game.showRate();
				return true;
			}
		}

		return false;
	}

	private int scaleX(int screenX) {
		return (int) (screenX / scaleFactorX);
	}

	private int scaleY(int screenY) {
		return (int) (screenY / scaleFactorY);
	}

	public List<SimpleButton> getMenuButtons() {
		return menuButtons;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
