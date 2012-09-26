package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.config.BattleColorConfig;

/**
 * SplashMenu erstellt die Oberfläche des SplashScreens,
 * inklusive Touchfunktion und Skalierung
 */
public class SplashMenu implements Screen {

	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	private TouchSprite splash;
	private InputMultiplexer inputMulti;

	/**
	 * Constructor for the splash screen
	 * 
	 * @param g
	 *            Game which called this splash screen.
	 */
	public SplashMenu(ColorBattleGame game) {
		this.gameRef = game;
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);
		ownBatch = new SpriteBatch();

		splash = new TouchSprite(Gdx.files.internal("splash.png"), ownCamera);
		splash.setPosition(0, 0);

		inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(splash);
		Gdx.input.setInputProcessor(inputMulti);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		ownBatch.setProjectionMatrix(ownCamera.combined);

		ownBatch.begin();
		splash.draw(ownBatch);
		ownBatch.end();

		if (splash.isTouched()) {
			splash.resetIsTouched();
			gameRef.setScreen(new MainMenu(gameRef));
			this.dispose();
		} 
	}

	@Override
	public void dispose() {
		ownBatch.dispose();
		ownCamera = null;
		inputMulti.removeProcessor(splash);
		splash.disposeTouchSprite();
		splash = null;
		inputMulti = null;
	}

	// other methods not need here
	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
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
