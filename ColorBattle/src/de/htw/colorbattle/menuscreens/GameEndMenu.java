package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.StringBuilder;

import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.GameResult;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.toast.Toast;
import de.htw.colorbattle.toast.Toast.TEXT_POS;

/**
 * GameEndMenu creates a GUI and Buttons and shows the Menu at the End of a Game
 */
public class GameEndMenu implements Screen {

	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	private boolean scoreComputed = false;
	private GameResult gameresult;
	private Toast render_toast;

	private Texture wallpaper;
	private Texture scoreTexture;
	private InputMultiplexer inputMulti;
	private TouchSprite backSprite;

	/*
	 * Constructor
	 */
	public GameEndMenu(ColorBattleGame game) {
		this.gameRef = game;
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);
		ownBatch = new SpriteBatch();

		wallpaper = new Texture(
				Gdx.files.internal("menu/GameFinishWallpaper.png"));

		backSprite = new TouchSprite(
				Gdx.files.internal("menu/GameEndBack.png"), ownCamera);
		backSprite.setPosition(5, 5);
		backSprite.highlightOnTouch = true;
		backSprite.setTouchDownPicture(Gdx.files.internal("menu/back_hover.png"));

		// for Touch-Events
		inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(backSprite);
		Gdx.input.setInputProcessor(inputMulti);
		
		this.render_toast  = new Toast(7, 20);
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
		ownBatch.draw(wallpaper, 0, 0);
		backSprite.draw(ownBatch);
		ownBatch.end();

		if (scoreComputed) {
			if (scoreTexture != null) {
				ownBatch.begin();
				ownBatch.draw(scoreTexture, 150, -10);
				ownBatch.end();
			}
		} else if (gameresult != null) {
			this.scoreTexture = gameresult.getScoreScreen(ownBatch, ownCamera);
			scoreComputed = true;
		}
		
		render_toast.toaster();

		if (backSprite.isTouched()) {
			backSprite.resetIsTouched();
			gameRef.setShowSplashScreen(false);
			gameRef.create(); // TODO könnte man auch anderst lösen...
			this.dispose();
		}
	}

	/*
	 * sets a GameResult and print it on the Screen via ToastMessage
	 */
	public void setGameresult(GameResult gameresult) {
		this.gameresult = gameresult;
		
		float score;
		for (Player currentPlayer : gameresult.getScoredPlayerList()){
			score = (float) ((float) Math.round((currentPlayer.getGameScore()) * 100.0) / 100.0);
			render_toast.makeText("PlayerID: " + currentPlayer.id + " Score: "+ score, "font", 
			        Toast.COLOR_PREF.RED, Toast.STYLE.NORMAL, TEXT_POS.middle, TEXT_POS.middle_down, Toast.LONG * 4.0f);
		}
	}

	/*
	 * Dispose all objects used in this class
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
		gameRef.inputMultiplexer.removeProcessor(backSprite);// TODO was macht der input multiplexer vom game überhaupt mit dem backsprite ??? ???
		inputMulti.removeProcessor(backSprite);
		inputMulti = null;
		ownBatch.dispose();
		ownCamera = null;
		wallpaper.dispose();
		scoreTexture.dispose();
		backSprite.disposeTouchSprite();
		backSprite = null;
		gameresult = null;
		render_toast=null;
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
