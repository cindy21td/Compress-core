package com.crane.GameWorld;

import com.crane.GameObjects.Hero;
import com.crane.GameObjects.ScrollHandler;

public class GameWorld {
	
	private Hero hero;
	private ScrollHandler scroller;

	public GameWorld(int midPointY) {
		hero = new Hero(30, 106, 15, 23);
		scroller = new ScrollHandler(midPointY);
	}
	
	public void update(float delta) {
		hero.update(delta);
		scroller.update(delta);
		// Sound Play
		// AssetLoader.example.play();
	}
	
	public Hero getHero() {
		return hero;
	}
	
	public ScrollHandler getScroller() {
		return scroller;
	}

}
