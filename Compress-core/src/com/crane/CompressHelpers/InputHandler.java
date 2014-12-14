package com.crane.CompressHelpers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Circle;
import com.crane.GameWorld.GameRenderer;
import com.crane.GameWorld.GameWorld;
import com.crane.compress.Compress;
import com.crane.ui.SimpleButton;

public class InputHandler implements InputProcessor {

	private GameWorld myWorld;
	private GameRenderer renderer;

	private List<SimpleButton> menuButtons;

	private SimpleButton playButton, replayButton;
	//private SimpleButton rateButton, scoreButton, helpButton, replayButton;

	private Circle play;

	private float scaleFactorX;
	private float scaleFactorY;

	private Compress game;

	public InputHandler(Compress game, GameWorld myWorld,
			GameRenderer renderer, float scaleFactorX, float scaleFactorY) {
		this.game = game;
		this.myWorld = myWorld;
		this.renderer = renderer;

		int midPointY = myWorld.getMidPointY();

		this.scaleFactorX = scaleFactorX;
		this.scaleFactorY = scaleFactorY;

		menuButtons = new ArrayList<SimpleButton>();

		//rateButton = new SimpleButton(390 / 3f, 340 / 3f, 100 / 3f, 58 / 3,
		//		AssetLoader.rateButtonUp, AssetLoader.rateButtonDown);

		//scoreButton = new SimpleButton(460 / 3f, 265 / 3f, 100 / 3f, 58 / 3,
		//		AssetLoader.scoreButtonUp, AssetLoader.scoreButtonDown);

		//helpButton = new SimpleButton(320 / 3f, 265 / 3f, 100 / 3f, 58 / 3,
		//		AssetLoader.helpButtonUp, AssetLoader.helpButtonDown);

		replayButton = new SimpleButton(263 / 3f, 265 / 3f, 100 / 3f, 70 / 3f,
				AssetLoader.playButtonUp, AssetLoader.playButtonDown);

		//menuButtons.add(rateButton);
		//menuButtons.add(scoreButton);
		//menuButtons.add(helpButton);

		play = new Circle(50, 92, 50f);

		playButton = new SimpleButton(50, 92, 50f);

	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenX = scaleX(screenX);
		screenY = scaleY(screenY);
		System.out.println(screenX + " " + screenY);
		if (myWorld.isMenu()) {
			playButton.isTouchDown(screenX, screenY);

			//rateButton.isTouchDown(screenX, screenY);
			//scoreButton.isTouchDown(screenX, screenY);
			//helpButton.isTouchDown(screenX, screenY);

		} else if (myWorld.isGameOver() || myWorld.isHighScore()) {
			replayButton.isTouchDown(screenX, screenY);
			//rateButton.isTouchDown(screenX, screenY);
			//scoreButton.isTouchDown(screenX, screenY);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screenX = scaleX(screenX);
		screenY = scaleY(screenY);

		if (myWorld.isMenu()) {
			if (playButton.isTouchUp(screenX, screenY)) {
				myWorld.ready(renderer, this);

				//rateButton.changePosition(160 / 3f, 280 / 3f);
				//scoreButton.changePosition(360 / 3f, 280 / 3f);
				menuButtons.add(replayButton);
				//menuButtons.remove(helpButton);
				
				return true;
			}
			
			/*
			if (rateButton.isTouchUp(screenX, screenY)) {

				return true;
			}

			if (scoreButton.isTouchUp(screenX, screenY)) {

				return true;
			}

			if (helpButton.isTouchUp(screenX, screenY)) {

				return true;
			}
			*/

		} else if(myWorld.isGameOver() || myWorld.isHighScore()) {
			if (replayButton.isTouchUp(screenX, screenY)) {

				myWorld.restart();
				
				return true;
			}
			/*
			if (rateButton.isTouchUp(screenX, screenY)) {

				return true;
			}

			if (scoreButton.isTouchUp(screenX, screenY)) {

				return true;
			}
			*/
		}

		return false;
	}

	public Circle getPlay() {
		return play;
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
