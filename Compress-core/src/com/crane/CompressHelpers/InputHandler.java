package com.crane.CompressHelpers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.InputProcessor;
import com.crane.GameWorld.GameRenderer;
import com.crane.GameWorld.GameWorld;
import com.crane.compress.Compress;
import com.crane.ui.SimpleButton;

public class InputHandler implements InputProcessor {

	private GameWorld myWorld;
	private GameRenderer renderer;

	private List<SimpleButton> menuButtons;

	private SimpleButton playButton;

	private float scaleFactorX;
	private float scaleFactorY;

	private Compress game;

	public InputHandler(Compress game, GameWorld myWorld, GameRenderer renderer, float scaleFactorX,
			float scaleFactorY) {
		this.game = game;
		this.myWorld = myWorld;
		this.renderer = renderer;

		int midPointY = myWorld.getMidPointY();

		this.scaleFactorX = scaleFactorX;
		this.scaleFactorY = scaleFactorY;

		menuButtons = new ArrayList<SimpleButton>();
		playButton = new SimpleButton(
				102 - (AssetLoader.buttonUp.getRegionWidth() / 2), midPointY
						- (AssetLoader.buttonUp.getRegionHeight() / 2), 100,
				50, AssetLoader.buttonUp, AssetLoader.buttonDown);
		menuButtons.add(playButton);

	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenX = scaleX(screenX);
		screenY = scaleY(screenY);
		System.out.println(screenX + " " + screenY);
		if (myWorld.isMenu()) {
			playButton.isTouchDown(screenX, screenY);
		} else if (myWorld.isReady()) {
			myWorld.start();
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screenX = scaleX(screenX);
		screenY = scaleY(screenY);

		if (myWorld.isMenu()) {
			if (playButton.isTouchUp(screenX, screenY)) {
				myWorld.ready(game, renderer);

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
