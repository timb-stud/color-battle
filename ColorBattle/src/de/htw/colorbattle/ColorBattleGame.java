package de.htw.colorbattle;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;

import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.config.RuntimeConfig;
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.menuscreens.MainMenu;
import de.htw.colorbattle.menuscreens.SplashMenu;
import de.htw.colorbattle.multiplayer.MultigameLogic;
import de.htw.colorbattle.network.MainActivityInterface;
import de.htw.colorbattle.network.NetworkActionResolver;
import de.htw.colorbattle.toast.Toast;

public class ColorBattleGame extends Game implements InputProcessor,
		ApplicationListener {

	public GameScreen gameScreen;
	public InputMultiplexer inputMultiplexer;
	public OrthographicCamera camera;

	public Toast toast;

	public RuntimeConfig bcConfig;
	public Music music;

	// Networks
	public MultigameLogic multiGame;
	public NetworkActionResolver netSvc;
	public NetworkActionResolver bluetoothActionResolver;
	public MainActivityInterface mainActivity;

	private boolean showSplashScreen = true;

	public ColorBattleGame(RuntimeConfig bcConfig,
			NetworkActionResolver bluetoothActionResolver,
			MainActivityInterface mainActivity) {
		super();
		this.bcConfig = bcConfig;
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);
		this.bluetoothActionResolver = bluetoothActionResolver;
		this.mainActivity = mainActivity;
		this.toast = new Toast(7, 20);
	}

	@Override
	public void create() {
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		
		if (bcConfig.playSound) {
			playSound();
		}	

		try {
			inputMultiplexer = new InputMultiplexer(this);
			gameScreen = new GameScreen(this);
			if (showSplashScreen) {
				SplashMenu splashMenu = new SplashMenu(this);
				this.setScreen(splashMenu);
			} else {
				MainMenu mainMenu = new MainMenu(this);
				this.setScreen(mainMenu);
			}

		} catch (NetworkException e) {
			Gdx.app.error("NetworkException",
					"ColorBattleGame: Can't create network connection.", e);
			e.printStackTrace();
		}
	}

	public static String getDeviceId() {
		return BattleColorConfig.DEVICE_ID;
	}

	public void playSound() {
		music = Gdx.audio.newMusic(Gdx.files.internal("sound/background.mp3"));
		music.setLooping(true);
		music.setVolume(0.5f);
		music.stop();
		music.play();
	}

	@Override
	public void dispose() {
		super.dispose();
		gameScreen.disposeFromGameScreen();
		if (music != null) {
			music.dispose();
			music = null;
		}
		inputMultiplexer = null;
		camera = null;
		bcConfig = null;	
		multiGame = null;
		netSvc = null;
		bluetoothActionResolver = null;
		mainActivity = null;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK) {
			// this.setScreen(mainMenuScreen);
			// Gdx.app.exit();
		}
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

	public void setShowSplashScreen(boolean showSplashScreen) {
		this.showSplashScreen = showSplashScreen;
	}
}