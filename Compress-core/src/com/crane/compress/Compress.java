package com.crane.compress;

import com.badlogic.gdx.Game;
import com.crane.CompressHelpers.AssetLoader;
import com.crane.Screens.SplashScreen;

public class Compress extends Game {
		
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
	
}
