package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.config.BattleColorConfig;

/**
 * Classn to Display a SplashMenu
 */
public class SplashMenu implements Screen {

	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	private TouchSprite splash;
	private InputMultiplexer inputMulti;
	private Sound screenTouchedSound;

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
		
		screenTouchedSound = Gdx.audio.newSound(Gdx.files.internal("sound/pop.mp3"));

		inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(splash);
		Gdx.input.setInputProcessor(inputMulti);
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		ownBatch.setProjectionMatrix(ownCamera.combined);

		ownBatch.begin();
		splash.draw(ownBatch);
		ownBatch.end();

		if (splash.isTouched()) {
			splash.resetIsTouched();
			gameRef.stopSound();
			screenTouchedSound.play();
			gameRef.setScreen(new MainMenu(gameRef));
			this.dispose();
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
		inputMulti.removeProcessor(splash);
		splash.disposeTouchSprite();
		splash = null;
		inputMulti = null;
		if(screenTouchedSound != null)
			screenTouchedSound.dispose();
	}

	/*
	 * Plays a sound when the Screen is shown
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
		gameRef.playSound();
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
