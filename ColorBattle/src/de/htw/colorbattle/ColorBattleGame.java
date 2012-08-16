package de.htw.colorbattle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;

import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.exception.NetworkException;

public class ColorBattleGame extends Game implements InputProcessor {
	public MainMenuScreen mainMenuScreen;
	public GameScreen gameScreen;
	BattleColorConfig bcConfig;
	public Music music;
	public InputMultiplexer inputMultiplexer;
	
	public ColorBattleGame(BattleColorConfig bcConfig){
		super();
		this.bcConfig = bcConfig;
	}
	
	@Override
	public void create() {
		try {
			inputMultiplexer = new InputMultiplexer(this);
			gameScreen = new GameScreen(this);
			mainMenuScreen = new MainMenuScreen(this);

			this.setScreen(mainMenuScreen);
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "ColorBattleGame: Can't create network connection.", e);
			e.printStackTrace();
			//TODO: handle exception
		}
	}
	
	public void playSound() {
		music = Gdx.audio.newMusic(Gdx.files.internal("background-music.mp3"));
		music.setLooping(true);
		music.setVolume(0.5f);
		music.play();
	}
	
	@Override
	public void dispose() {
		mainMenuScreen.dispose();
		gameScreen.dispose();
		super.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
