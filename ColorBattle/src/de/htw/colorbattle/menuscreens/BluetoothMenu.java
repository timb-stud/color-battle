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
import de.htw.colorbattle.multiplayer.MultigameLogic;

/**
 * BluetoothMenu  creates GUI, with Buttons to start a BluetoothGame
 */
public class BluetoothMenu implements Screen {

	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	// Buttons
	private TouchSprite joinBtGameSprite;
	private TouchSprite openBtGameSprite;
	private TouchSprite backSprite;
	private InputMultiplexer inputMulti;

	private Texture wallpaper;

	/*
	 * Constructor
	 */
	public BluetoothMenu(ColorBattleGame game) {
		this.gameRef = game;
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);
		ownBatch = new SpriteBatch();

		float width = BattleColorConfig.WIDTH;
		float height = BattleColorConfig.HEIGHT;

		// Grafikelemente anlegen

		wallpaper = new Texture(Gdx.files.internal("menu/MenuScreenWallpaper.png"));

		joinBtGameSprite = new TouchSprite(
				Gdx.files.internal("menu/join.png"), ownCamera);
		joinBtGameSprite.setPosition(
				(width - joinBtGameSprite.getWidth()) / 2.0f, height
						- joinBtGameSprite.getHeight()-15.0f);
		joinBtGameSprite.highlightOnTouch = true;
		joinBtGameSprite.setTouchDownPicture(Gdx.files.internal("menu/join_hover.png"));

		openBtGameSprite = new TouchSprite(
				Gdx.files.internal("menu/Open_BT_Button.png"), ownCamera);
		openBtGameSprite.setPosition(
				(width - openBtGameSprite.getWidth()) / 2.0f,
				(height - openBtGameSprite.getHeight()) / 2.0f);
		openBtGameSprite.highlightOnTouch = true;
		openBtGameSprite.setTouchDownPicture(Gdx.files.internal("menu/Open_BT_Button_hover.png"));

		backSprite = new TouchSprite(Gdx.files.internal("menu/back.png"),
				ownCamera);
		backSprite.setPosition((width - backSprite.getWidth()) / 2.0f, +15.0f);
		backSprite.highlightOnTouch = true;
		backSprite.setTouchDownPicture(Gdx.files.internal("menu/back_hover.png"));

		// for Touch-Events
		inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(joinBtGameSprite);
		inputMulti.addProcessor(openBtGameSprite);
		inputMulti.addProcessor(backSprite);
		Gdx.input.setInputProcessor(inputMulti);
	}

	/*
	 * Main loop in this class
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// camera.update();
		ownBatch.setProjectionMatrix(ownCamera.combined);

		ownBatch.begin();
		ownBatch.draw(wallpaper, 0, 0);
		openBtGameSprite.draw(ownBatch);
		joinBtGameSprite.draw(ownBatch);
		backSprite.draw(ownBatch);
		ownBatch.end();

		if (joinBtGameSprite.isTouched()) {
			joinBtGameSprite.resetIsTouched(); 
			gameRef.multiGame = new MultigameLogic(gameRef);
			gameRef.netSvc.connect();
			gameRef.setScreen(new JoiningScreen(gameRef));
			this.dispose();

		} else if (openBtGameSprite.isTouched()) {
			 openBtGameSprite.resetIsTouched(); 
			 gameRef.netSvc.startServer();
			 startServer(2);
		} else if (backSprite.isTouched()) {
			backSprite.resetIsTouched(); 
			gameRef.setScreen(new MainMenu(gameRef));
			this.dispose();
		}
	}
	
	/*
	 * Creates the MultigameLogic and switch the current screen to the JoininScreen
	 */
	private void startServer(int players){
		gameRef.multiGame = new MultigameLogic(gameRef, players);
		gameRef.setScreen(new JoiningScreen(gameRef));
		this.dispose();
	}

	/*
	 * Dispose all objects
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
		ownBatch.dispose();
		ownCamera = null;
		inputMulti.removeProcessor(joinBtGameSprite);
		inputMulti.removeProcessor(openBtGameSprite);
		inputMulti.removeProcessor(backSprite);
		joinBtGameSprite.disposeTouchSprite();
		openBtGameSprite.disposeTouchSprite();
		backSprite.disposeTouchSprite();
		joinBtGameSprite = null;
		openBtGameSprite = null;
		backSprite = null;
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