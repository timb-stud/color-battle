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

		wallpaper = new Texture(Gdx.files.internal("GameScreenWallpaper.png"));

		joinBtGameSprite = new TouchSprite(
				Gdx.files.internal("menu/Join_BT_Button.png"), ownCamera);
		joinBtGameSprite.setPosition(
				(width - joinBtGameSprite.getWidth()) / 2.0f, height
						- joinBtGameSprite.getHeight());

		openBtGameSprite = new TouchSprite(
				Gdx.files.internal("menu/Open_BT_Button.png"), ownCamera);
		openBtGameSprite.setPosition(
				(width - openBtGameSprite.getWidth()) / 2.0f,
				(height - openBtGameSprite.getHeight()) / 2.0f);

		backSprite = new TouchSprite(Gdx.files.internal("menu/back.png"),
				ownCamera);
		backSprite.setPosition((width - backSprite.getWidth()) / 2.0f, 0);

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
			
			// TODO action,dispose
			//game.bluetoothActionResolver.connect(); // das stand in join nach fallunterscheidung wlan bluetooth

		} else if (openBtGameSprite.isTouched()) {
			 openBtGameSprite.resetIsTouched(); 
			
			// TODO action,dispose
			
		} else if (backSprite.isTouched()) {
			gameRef.setScreen(new MainMenu(gameRef));
			this.dispose(); 
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
		inputMulti.removeProcessor(joinBtGameSprite);
		inputMulti.removeProcessor(openBtGameSprite);
		inputMulti.removeProcessor(backSprite);
		joinBtGameSprite = null;
		openBtGameSprite = null;
		backSprite = null;
		inputMulti = null;
		wallpaper.dispose();
	}
}