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
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.multiplayer.MultigameLogic;

/**
 * BluetoothMenu  erstellt die Oberfläche,
 * inklusive Buttons und Skalierung,
 * um ein BluetoothGame zu starten
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

		openBtGameSprite = new TouchSprite(
				Gdx.files.internal("menu/Open_BT_Button.png"), ownCamera);
		openBtGameSprite.setPosition(
				(width - openBtGameSprite.getWidth()) / 2.0f,
				(height - openBtGameSprite.getHeight()) / 2.0f);

		backSprite = new TouchSprite(Gdx.files.internal("menu/back.png"),
				ownCamera);
		backSprite.setPosition((width - backSprite.getWidth()) / 2.0f, +15.0f);

		// for Touch-Events
		inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(joinBtGameSprite);
		inputMulti.addProcessor(openBtGameSprite);
		inputMulti.addProcessor(backSprite);
		Gdx.input.setInputProcessor(inputMulti);
	}

	@Override
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
			gameRef.multiGame = new MultigameLogic(gameRef, false);
			gameRef.netSvc.connect();
			try {
				gameRef.setScreen(new JoiningScreen(gameRef));
			} catch (NetworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.dispose();

		} else if (openBtGameSprite.isTouched()) {
			 openBtGameSprite.resetIsTouched(); 
			 gameRef.netSvc.startServer();
			 startServer(2);
		} else if (backSprite.isTouched()) {
			gameRef.setScreen(new MainMenu(gameRef));
			this.dispose();
		}
	}
	
	private void startServer(int players){
		gameRef.bcConfig.multigamePlayerCount = players; // TODO gefällt mir gar nicht die config dafür zu nutzen .... ist ja keine laufzeit config
		//TODO das untendrunter kann man bestimmt auch schöner machen als über den gameref
		gameRef.multiGame = new MultigameLogic(gameRef, true);
		gameRef.multiGame.startServer();
		try {
			gameRef.setScreen(new JoiningScreen(gameRef));
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dispose();
	}

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
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}