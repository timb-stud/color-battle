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
 * WlanMenu erstellt die Oberfläche,
 * inklusive Buttons und Skalierung,
 * um ein WlanGame zu starten
 */
public class WlanMenu implements Screen {

	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	// Buttons
	private TouchSprite joinWlanGameSprite;
	private TouchSprite open2PlWlanGameSprite;
	private TouchSprite open3PlWlanGameSprite;
	private TouchSprite open4PlWlanGameSprite;
	private TouchSprite backSprite;
	private InputMultiplexer inputMulti;

	private Texture wallpaper;

	public WlanMenu(ColorBattleGame game) {
		this.gameRef = game;
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);
		ownBatch = new SpriteBatch();

		// Grafikelemente anlegen
		wallpaper = new Texture(Gdx.files.internal("menu/WLANMenuScreenWallpaper.png"));

		joinWlanGameSprite = new TouchSprite(
				Gdx.files.internal("menu/join.png"), ownCamera);
		joinWlanGameSprite.setPosition(10.0f, 320.0f);

		open2PlWlanGameSprite = new TouchSprite(
				Gdx.files.internal("menu/2P_WLAN.png"), ownCamera);
		open2PlWlanGameSprite.setPosition(
				440.0f, 320.0f);

		open3PlWlanGameSprite = new TouchSprite(
				Gdx.files.internal("menu/3P_WLAN.png"), ownCamera);
		open3PlWlanGameSprite.setPosition(
				440.0f, 175.0f);

		open4PlWlanGameSprite = new TouchSprite(
				Gdx.files.internal("menu/4P_WLAN.png"), ownCamera);
		open4PlWlanGameSprite.setPosition(
				440.0f, 30.0f);

		backSprite = new TouchSprite(Gdx.files.internal("menu/back.png"),
				ownCamera);
		backSprite.setPosition(10, 30.0f);

		// for Touch-Events
		inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(joinWlanGameSprite);
		inputMulti.addProcessor(open2PlWlanGameSprite);
		inputMulti.addProcessor(open3PlWlanGameSprite);
		inputMulti.addProcessor(open4PlWlanGameSprite);
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
		joinWlanGameSprite.draw(ownBatch);
		open2PlWlanGameSprite.draw(ownBatch);
		open3PlWlanGameSprite.draw(ownBatch);
		open4PlWlanGameSprite.draw(ownBatch);
		backSprite.draw(ownBatch);
		ownBatch.end();

		if (joinWlanGameSprite.isTouched()) {
			joinWlanGameSprite.resetIsTouched(); 
			gameRef.multiGame = new MultigameLogic(gameRef);
			gameRef.multiGame.joinGame();
			gameRef.setScreen(new JoiningScreen(gameRef));
			this.dispose();
			
		} else if (open2PlWlanGameSprite.isTouched()) {
			open2PlWlanGameSprite.resetIsTouched(); 
			this.startServer(2);
			this.dispose();

		} else if (open3PlWlanGameSprite.isTouched()) {
			open3PlWlanGameSprite.resetIsTouched(); 
			this.startServer(3);
			this.dispose();
			
		} else if (open4PlWlanGameSprite.isTouched()) {
			open4PlWlanGameSprite.resetIsTouched(); 
			this.startServer(4);
			this.dispose();
			
		} else if (backSprite.isTouched()) {
			backSprite.resetIsTouched(); 
			gameRef.setScreen(new MainMenu(gameRef));
			this.dispose();
		}

	}
	
	private void startServer(int players){
		gameRef.multiGame = new MultigameLogic(gameRef, players);
		gameRef.setScreen(new JoiningScreen(gameRef));
	}
	
	@Override
	public void dispose() {
		ownBatch.dispose();
		ownCamera = null;
		inputMulti.removeProcessor(joinWlanGameSprite);
		inputMulti.removeProcessor(open2PlWlanGameSprite);
		inputMulti.removeProcessor(open3PlWlanGameSprite);
		inputMulti.removeProcessor(open4PlWlanGameSprite);
		inputMulti.removeProcessor(backSprite);
		joinWlanGameSprite.disposeTouchSprite();
		open2PlWlanGameSprite.disposeTouchSprite();
		open3PlWlanGameSprite.disposeTouchSprite();
		open4PlWlanGameSprite.disposeTouchSprite();
		backSprite.disposeTouchSprite();
		joinWlanGameSprite = null;
		open2PlWlanGameSprite = null;
		open3PlWlanGameSprite = null;
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