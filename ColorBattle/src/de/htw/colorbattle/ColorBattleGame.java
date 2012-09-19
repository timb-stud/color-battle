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
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.menuscreens.JoiningScreen;
import de.htw.colorbattle.menuscreens.SplashMenu;
import de.htw.colorbattle.multiplayer.MultigameLogic;
import de.htw.colorbattle.network.MainActivityInterface;
import de.htw.colorbattle.network.NetworkActionResolver;

public class ColorBattleGame extends Game implements InputProcessor,
		ApplicationListener {

	public JoiningScreen joiningScreen;
	public GameScreen gameScreen;
	public MultigameLogic multiGame;
	public BattleColorConfig bcConfig;
	public Music music;
	public InputMultiplexer inputMultiplexer;
	public OrthographicCamera camera;
	public NetworkActionResolver netSvc;
	public NetworkActionResolver bluetoothActionResolver;
	public MainActivityInterface mainActivity;

	public ColorBattleGame(BattleColorConfig bcConfig,
			NetworkActionResolver bluetoothActionResolver, MainActivityInterface mainActivity) {
		super();
		this.bcConfig = bcConfig;
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, bcConfig.width, bcConfig.height);
		this.bluetoothActionResolver = bluetoothActionResolver;
		this.mainActivity = mainActivity;
	}

	@Override
	public void create() {
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);

		try {
			inputMultiplexer = new InputMultiplexer(this);
			gameScreen = new GameScreen(this);
			joiningScreen = new JoiningScreen(this);

			SplashMenu newmenu = new SplashMenu(this);
			this.setScreen(newmenu);

		} catch (NetworkException e) {
			Gdx.app.error("NetworkException",
					"ColorBattleGame: Can't create network connection.", e);
			e.printStackTrace();
			// TODO: handle exception
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
		super.dispose();
		gameScreen.dispose();
		joiningScreen.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK) {
			// this.setScreen(mainMenuScreen);
			Gdx.app.exit();
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
}
