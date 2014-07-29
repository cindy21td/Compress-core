package com.crane.CompressHelpers;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.crane.GameObjects.Hero;
import com.crane.GameWorld.GameWorld;

public class GestureHandler implements GestureListener {

	private GameWorld myWorld;
	private Hero hero;
	
	public GestureHandler(GameWorld world) {
		this.myWorld = world;
		this.hero = myWorld.getHero();
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		if(myWorld.isReady()) {
			
			myWorld.start();
			
		} 
		
		if(myWorld.isRunning()) {
			hero.onClick();
		}
		
		if(myWorld.isGameOver() || myWorld.isHighScore()) {
			
			myWorld.restart();
			
		} 
		
		
		return true;
	}
	
	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		
		if(velocityX < -10) {
			hero.onSwipe(false);
		} else if(velocityX > 10){
			hero.onSwipe(true);
		}
		
		return true;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

}
