package de.htw.colorbattle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.exception.NetworkException;

public class ColorBattleGame extends Game {
	public MainMenuScreen mainMenuScreen;
	public GameScreen gameScreen;
	BattleColorConfig bcConfig;
	
	public ColorBattleGame(BattleColorConfig bcConfig){
		super();
		this.bcConfig = bcConfig;
	}
	
	@Override
	public void create() {
		try {
			gameScreen = new GameScreen(this);
			mainMenuScreen = new MainMenuScreen(this);
			this.setScreen(mainMenuScreen);
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "ColorBattleGame: Can't create network connection.", e);
			e.printStackTrace();
			//TODO: handle exception
		}
	}
	
	@Override
	public void dispose() {
		mainMenuScreen.dispose();
		gameScreen.dispose();
		super.dispose();
	}
}
