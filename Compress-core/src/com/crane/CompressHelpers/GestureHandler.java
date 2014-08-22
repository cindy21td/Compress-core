package com.crane.CompressHelpers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.crane.GameObjects.Hero;
import com.crane.GameWorld.GameWorld;
import com.crane.ui.SimpleButton;

public class GestureHandler implements GestureListener {

	private GameWorld myWorld;
	private Hero hero;
	
	private List<SimpleButton> menuButtons;

    private SimpleButton playButton;

    private float scaleFactorX;
    private float scaleFactorY;

	
	public GestureHandler(GameWorld world, float scaleFactorX, float scaleFactorY) {
		this.myWorld = world;
		this.hero = myWorld.getHero();
		
		int midPointY = myWorld.getMidPointY();

        this.scaleFactorX = scaleFactorX;
        this.scaleFactorY = scaleFactorY;

        menuButtons = new ArrayList<SimpleButton>();
        playButton = new SimpleButton(
                136 / 2 - (AssetLoader.buttonUp.getRegionWidth() / 2),
                midPointY + 50, 29, 16, AssetLoader.buttonUp,
                AssetLoader.buttonDown);
        menuButtons.add(playButton);
	}

	@Override
	public boolean tap(float screenX, float screenY, int count, int button) {
		screenX = scaleX(screenX);
        screenY = scaleY(screenY);
        System.out.println(screenX + " " + screenY);
        
        System.out.println("tapped");		
		
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
		if(velocityX > 0) {
			hero.onSwipe();
			return true;
		}
		return false;
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
	
	private int scaleX(float screenX) {
        return (int) (screenX / scaleFactorX);
    }

    private int scaleY(float screenY) {
        return (int) (screenY / scaleFactorY);
    }

    public List<SimpleButton> getMenuButtons() {
        return menuButtons;
    }

}
