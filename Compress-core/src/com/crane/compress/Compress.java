package com.crane.compress;

import com.badlogic.gdx.Game;
import com.crane.CompressHelpers.AssetLoader;
import com.crane.CompressHelpers.IActivityRequestHandler;
import com.crane.Screens.SplashScreen;

public class Compress extends Game {
	
	private IActivityRequestHandler myRequestHandler;

    public Compress(IActivityRequestHandler handler) {
        myRequestHandler = handler;
    }
    
	@Override
	public void create() {
		System.out.println("Compress Game created");
		AssetLoader.load();
		setScreen(new SplashScreen(this));
	}
	
	@Override
	public void dispose() {
		super.dispose();
		AssetLoader.dispose();
	}

	
	public void showAd(boolean value) {
		myRequestHandler.showAds(value);
	}
	
}
