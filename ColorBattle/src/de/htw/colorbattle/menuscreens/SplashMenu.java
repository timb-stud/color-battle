package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.toast.Toast;

public class SplashMenu implements Screen {

	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	private TouchSprite splash;
	private InputMultiplexer inputMulti;
	private Toast render_toast;

	/**
	 * Constructor for the splash screen
	 * 
	 * @param g
	 *            Game which called this splash screen.
	 */
	public SplashMenu(ColorBattleGame game) {
		this.gameRef = game;
		this.render_toast  = new Toast(7, 6);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		ownBatch.setProjectionMatrix(ownCamera.combined);

		ownBatch.begin();
		splash.draw(ownBatch);
		ownBatch.end();
		
		render_toast.toaster();

		if (splash.isTouched()) {
			splash.resetIsTouched();
			gameRef.setScreen(new MainMenu(gameRef));
			this.dispose();
		} 
	}

	@Override
	public void show() {
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);
		ownBatch = new SpriteBatch();

		splash = new TouchSprite(Gdx.files.internal("splash.png"), ownCamera);
		splash.setPosition(0, 0);

		inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(splash);
		Gdx.input.setInputProcessor(inputMulti);
		
		render_toast.makeText("Welcome to Game", "font", 
				Toast.COLOR_PREF.BLUE, Toast.STYLE.NORMAL, Toast.TEXT_POS.middle_up, Toast.TEXT_POS.middle_up, Toast.MED);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		ownBatch.dispose();
		ownCamera = null;
		inputMulti.removeProcessor(splash);
		inputMulti = null;
	}

}