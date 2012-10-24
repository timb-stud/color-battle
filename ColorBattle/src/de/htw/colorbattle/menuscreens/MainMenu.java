package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.config.GameMode;
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.network.TCPService;
import de.htw.colorbattle.network.UDPService;

/**
 * Creates the MainMenu GUI with all Buttons
 */
public class MainMenu implements Screen {

	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	// Buttons
	private TouchSprite wlanGameSprite;
	private TouchSprite btGameSprite;
	private TouchSprite exitGameSprite;
	private InputMultiplexer inputMulti;

	private Texture wallpaper;

	/**
	 * wird benötigt wegen einem asynchronem Thread von Gdx,
	 * und verhindert einen gelegentlichen Nullpointer
	 */
	private boolean endButtonPushed = false;

	/*
	 * Constructor
	 */
	public MainMenu(ColorBattleGame game) {
		this.gameRef = game;
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);
		ownBatch = new SpriteBatch();

		float width = BattleColorConfig.WIDTH;
		float height = BattleColorConfig.HEIGHT;

		// Grafikelemente anlegen

		wallpaper = new Texture(
				Gdx.files.internal("menu/MenuScreenWallpaper.png"));

		wlanGameSprite = new TouchSprite(
				Gdx.files.internal("menu/Button_WLAN.png"), ownCamera);
		wlanGameSprite.setPosition((width - wlanGameSprite.getWidth()) / 2.0f,
				height - wlanGameSprite.getHeight() - 15.0f);
		wlanGameSprite.highlightOnTouch = true;
		wlanGameSprite.setTouchDownPicture(Gdx.files
				.internal("menu/Button_WLAN_hover.png"));

		btGameSprite = new TouchSprite(
				Gdx.files.internal("menu/Button_BT.png"), ownCamera);
		btGameSprite.setPosition((width - btGameSprite.getWidth()) / 2.0f,
				(height - btGameSprite.getHeight()) / 2.0f);
		btGameSprite.highlightOnTouch = true;
		btGameSprite.setTouchDownPicture(Gdx.files
				.internal("menu/Button_BT_hover.png"));

		exitGameSprite = new TouchSprite(
				Gdx.files.internal("menu/ExitGame.png"), ownCamera);
		exitGameSprite.setPosition((width - exitGameSprite.getWidth()) / 2.0f,
				+15.0f);
		exitGameSprite.highlightOnTouch = true;
		exitGameSprite.setTouchDownPicture(Gdx.files
				.internal("menu/ExitGame_hover.png"));

		// for Touch-Events
		inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(wlanGameSprite);
		inputMulti.addProcessor(btGameSprite);
		inputMulti.addProcessor(exitGameSprite);
		Gdx.input.setInputProcessor(inputMulti);
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		if (!endButtonPushed) {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			ownBatch.setProjectionMatrix(ownCamera.combined);

			ownBatch.begin();
			ownBatch.draw(wallpaper, 0, 0);
			btGameSprite.draw(ownBatch);
			wlanGameSprite.draw(ownBatch);
			exitGameSprite.draw(ownBatch);
			ownBatch.end();

			if (wlanGameSprite.isTouched()) {
				wlanGameSprite.resetIsTouched();
				gameRef.setScreen(new WlanMenu(gameRef));
				try {
					if(gameRef.bcConfig.gameMode == GameMode.TCP){
						gameRef.netSvc = new TCPService(gameRef.bcConfig.multicastAddress, 
														gameRef.bcConfig.multicastPort);
					} else { 
						gameRef.netSvc = UDPService.getInstance(
							gameRef.bcConfig.multicastAddress,
							gameRef.bcConfig.multicastPort);
					}
				} catch (NetworkException e) {
					Gdx.app.error("Network",
							"Can't create Wifi network service");
				}
				this.dispose();
			} else if (btGameSprite.isTouched()) {
				btGameSprite.resetIsTouched();
				if (gameRef.mainActivity.enableBluetoothQuestion()){
					gameRef.netSvc = gameRef.bluetoothActionResolver;
					gameRef.setScreen(new BluetoothMenu(gameRef));
					this.dispose();
				}
			} else if (exitGameSprite.isTouched()) {
				endButtonPushed = true;
				gameRef.dispose();
				this.dispose();
				Gdx.app.exit();
			}
		}
	}

	/*
	 * Dispose all objects used in this class
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
		ownBatch.dispose();
		ownCamera = null;
		inputMulti.removeProcessor(wlanGameSprite);
		inputMulti.removeProcessor(btGameSprite);
		inputMulti.removeProcessor(exitGameSprite);
		wlanGameSprite.disposeTouchSprite();
		btGameSprite.disposeTouchSprite();
		exitGameSprite.disposeTouchSprite();
		wlanGameSprite = null;
		btGameSprite = null;
		exitGameSprite = null;
		inputMulti = null;
		wallpaper.dispose();
	}

	// other methods not need here
	
	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {

	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	@Override
	public void resume() {
	}
}