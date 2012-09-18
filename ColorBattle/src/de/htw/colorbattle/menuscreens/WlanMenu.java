package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.TouchSprite;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.multiplayer.MultigameLogic;

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

		float width = BattleColorConfig.WIDTH;
		float height = BattleColorConfig.HEIGHT;

		// Grafikelemente anlegen

		wallpaper = new Texture(Gdx.files.internal("GameScreenWallpaper.png"));

		joinWlanGameSprite = new TouchSprite(
				Gdx.files.internal("menu/join_WLAN.png"), ownCamera);
		joinWlanGameSprite.setPosition(
				(width - joinWlanGameSprite.getWidth()) / 2.0f, 400.0f);

		open2PlWlanGameSprite = new TouchSprite(
				Gdx.files.internal("menu/2P_WLAN.png"), ownCamera);
		open2PlWlanGameSprite.setPosition(
				(width - open2PlWlanGameSprite.getWidth()) / 2.0f, 300.0f);

		open3PlWlanGameSprite = new TouchSprite(
				Gdx.files.internal("menu/3P_WLAN.png"), ownCamera);
		open3PlWlanGameSprite.setPosition(
				(width - open3PlWlanGameSprite.getWidth()) / 2.0f, 200.0f);

		open4PlWlanGameSprite = new TouchSprite(
				Gdx.files.internal("menu/4P_WLAN.png"), ownCamera);
		open4PlWlanGameSprite.setPosition(
				(width - open4PlWlanGameSprite.getWidth()) / 2.0f, 100.0f);

		backSprite = new TouchSprite(Gdx.files.internal("menu/back_WLAN.png"),
				ownCamera);
		backSprite.setPosition((width - backSprite.getWidth()) / 2.0f, 0);

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

			try {
				gameRef.multiGame = new MultigameLogic(gameRef, false);
				gameRef.multiGame.joinGame();
				gameRef.setScreen(gameRef.joiningScreen); //TODO bessere Lösung
				this.dispose();
			} catch (NetworkException e) {
				Gdx.app.error("Network Service", "WLANmenu sending problem on joining game");
			}
			

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
		gameRef.bcConfig.multigamePlayerCount = players; // TODO gefällt mir gar nicht die config dafür zu nutzen .... ist ja keine laufzeit config
		//TODO das untendrunter kann man bestimmt auch schöner machen als über den gameref
		try {
			gameRef.multiGame = new MultigameLogic(gameRef, true);
			gameRef.multiGame.startServer();
			gameRef.setScreen(gameRef.joiningScreen);
		} catch (NetworkException e) {
			Gdx.app.error("Network Service", "Mainmenu sending problem");
		}
		
	}
	

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

	@Override
	public void dispose() {
		ownBatch.dispose();
		ownCamera = null;
		inputMulti.removeProcessor(joinWlanGameSprite);
		inputMulti.removeProcessor(open2PlWlanGameSprite);
		inputMulti.removeProcessor(open3PlWlanGameSprite);
		joinWlanGameSprite = null;
		open2PlWlanGameSprite = null;
		open3PlWlanGameSprite = null;
		inputMulti = null;
		wallpaper.dispose();
	}
}