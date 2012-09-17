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

public class BluetoothMenu implements Screen {

	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	// Buttons
	private TouchSprite wlanGameSprite;
	private TouchSprite btGameSprite;
	private TouchSprite exitGameSprite;
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

		wlanGameSprite = new TouchSprite(
				Gdx.files.internal("menu/Button_WLAN.png"), ownCamera);
		wlanGameSprite.setPosition((width - wlanGameSprite.getWidth()) / 2.0f,
				height - wlanGameSprite.getHeight());

		btGameSprite = new TouchSprite(
				Gdx.files.internal("menu/Button_BT.png"), ownCamera);
		btGameSprite.setPosition((width - btGameSprite.getWidth()) / 2.0f,
				(height - btGameSprite.getHeight()) / 2.0f);

		exitGameSprite = new TouchSprite(
				Gdx.files.internal("menu/ExitGame.png"), ownCamera);
		exitGameSprite.setPosition((width - exitGameSprite.getWidth()) / 2.0f,
				0);

		// for Touch-Events
		inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(wlanGameSprite);
		inputMulti.addProcessor(btGameSprite);
		inputMulti.addProcessor(exitGameSprite);
		Gdx.input.setInputProcessor(inputMulti);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// camera.update();
		ownBatch.setProjectionMatrix(ownCamera.combined);

		ownBatch.begin();
		ownBatch.draw(wallpaper, 0, 0);
		btGameSprite.draw(ownBatch);
		// wlanGameSprite.draw(ownBatch);
		exitGameSprite.draw(ownBatch);
		ownBatch.end();

		if (wlanGameSprite.isTouched()) {
			// wlanGameSprite.resetIsTouched(); // keine Ahnung f�r was man das
			// brauchen soll
			gameRef.setScreen(new WlanMenu(gameRef));
			this.dispose();
		} else if (btGameSprite.isTouched()) {
			 btGameSprite.resetIsTouched(); // keine Ahnung f�r was man das
			// brauchen soll
			 BluetoothMenu bm = new BluetoothMenu(gameRef);
			gameRef.setScreen(bm);
			this.dispose(); // wird noch ausgef�hrt
		} else if (exitGameSprite.isTouched()) {
			Gdx.app.exit();
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
		inputMulti.removeProcessor(wlanGameSprite);
		inputMulti.removeProcessor(btGameSprite);
		inputMulti.removeProcessor(exitGameSprite);
		wlanGameSprite = null;
		btGameSprite = null;
		exitGameSprite = null;
		inputMulti = null;
		wallpaper.dispose();
	}
}