package de.htw.colorbattle;

import com.badlogic.gdx.Game;

public class ColorBattleGame extends Game {
	public MainMenuScreen mainMenuScreen;
	public GameScreen gameScreen;
	
	@Override
	public void create() {
		gameScreen = new GameScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		this.setScreen(mainMenuScreen);
	}
	
	@Override
	public void dispose() {
		mainMenuScreen.dispose();
		gameScreen.dispose();
		super.dispose();
	}
}
