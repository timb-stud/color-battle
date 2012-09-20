package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.GameResult;
import de.htw.colorbattle.config.BattleColorConfig;

/**
 * GameEndMenu erstellt die Oberfl�che,
 * inklusive Buttons und Skalierung,
 * zum anzeigen des Spielergebnisses
 */
public class GameEndMenu implements Screen {

	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	private boolean scoreComputed = false;
	private GameResult gameresult;

	private Texture wallpaper;
	private Texture scoreTexture;
	private InputMultiplexer inputMulti;
	private TouchSprite backSprite;

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

		// for Touch-Events
		inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(backSprite);
		Gdx.input.setInputProcessor(inputMulti);
	}

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
			this.scoreTexture = gameresult.getScoreScreen(ownBatch);
			scoreComputed = true;
		}

		if (backSprite.isTouched()) {
			backSprite.resetIsTouched();
			gameRef.create(); // TODO k�nnte man auch anderst l�sen...
			this.dispose();
		}
	}

	public void setGameresult(GameResult gameresult) {
		this.gameresult = gameresult;
	}

	@Override
	public void dispose() {
		gameRef.inputMultiplexer.removeProcessor(backSprite);// TODO was macht der input multiplexer vom game �berhaupt mit dem backsprite ??? ???
		inputMulti.removeProcessor(backSprite);
		inputMulti = null;
		ownBatch.dispose();
		ownCamera = null;
		wallpaper.dispose();
		scoreTexture.dispose();
		backSprite.disposeTouchSprite();
		backSprite = null;
		gameresult = null;
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
