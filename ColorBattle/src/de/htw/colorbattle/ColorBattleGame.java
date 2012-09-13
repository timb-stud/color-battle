package de.htw.colorbattle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;

import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.network.BluetoothActionResolver;

public class ColorBattleGame extends Game implements InputProcessor {
	public MainMenuScreen mainMenuScreen;
	public JoiningScreen joiningScreen;
	public GameScreen gameScreen;
	BattleColorConfig bcConfig;
	public Music music;
	public InputMultiplexer inputMultiplexer;
	public OrthographicCamera camera;
	public GameEndScreen gameEndScreen;
	public BluetoothActionResolver bluetoothActionResolver;
	
	public ColorBattleGame(BattleColorConfig bcConfig, BluetoothActionResolver bluetoothActionResolver){
		super();
		this.bcConfig = bcConfig;
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, bcConfig.width, bcConfig.height);
		this.bluetoothActionResolver = bluetoothActionResolver;
	}
	
	@Override
	public void create() {
		inputMultiplexer = new InputMultiplexer(this);
		mainMenuScreen = new MainMenuScreen(this);
		gameScreen = new GameScreen(this);
		joiningScreen = new JoiningScreen(this);
		gameEndScreen = new GameEndScreen(this);
		
		if(bcConfig.isSinglePlayer){
			this.setScreen(gameScreen);
		} else {
			this.setScreen(mainMenuScreen);
		}
	}
	
	public void playSound() {
		music = Gdx.audio.newMusic(Gdx.files.internal("background-music.mp3"));
		music.setLooping(true);
		music.setVolume(0.5f);
		music.play();
	}
	
	public void showGameScreen(){
		setScreen(gameScreen);
	}
	
	public void showJoiningScreen(){
		setScreen(joiningScreen);
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
