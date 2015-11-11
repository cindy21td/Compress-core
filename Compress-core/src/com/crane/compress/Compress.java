package com.crane.compress;

import com.badlogic.gdx.Game;
import com.crane.CompressHelpers.AssetLoader;
import com.crane.CompressHelpers.IActivityRequestHandler;
import com.crane.Screens.SplashScreen;

public class Compress extends Game {

	private IActivityRequestHandler myRequestHandler;

	private boolean disableAdsRate = false;

	public Compress() {
		disableAdsRate = true;
	}

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
		if (!disableAdsRate) {
			myRequestHandler.showAds(value);
		}
	}

	public void showRate() {
		if (!disableAdsRate) {
			myRequestHandler.showRate();
		}
	}

	public void setRateValue(int val) {
		AssetLoader.setRateValue(val);
	}
}
