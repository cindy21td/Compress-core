package com.crane.CompressHelpers;

import com.badlogic.gdx.InputProcessor;
import com.crane.GameObjects.Hero;

public class InputHandler implements InputProcessor {
	
	private Hero hero;
	
	public InputHandler(Hero hero) {
		this.hero = hero;
	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		hero.onClick();
		return true;
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
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
