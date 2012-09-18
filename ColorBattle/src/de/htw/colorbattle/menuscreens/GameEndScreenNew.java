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
import de.htw.colorbattle.TouchSprite;
import de.htw.colorbattle.config.BattleColorConfig;

public class GameEndScreenNew implements Screen {

	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	private boolean scoreComputed = false;
	private GameResult gameresult;

	private Texture wallpaper;
	private Texture scoreTexture;
	private InputMultiplexer inputMulti;
	private TouchSprite backSprite;

	public GameEndScreenNew(ColorBattleGame game) {
		this.gameRef = game;
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);
		ownBatch = new SpriteBatch();

		float width = BattleColorConfig.WIDTH;

		wallpaper = new Texture(Gdx.files.internal("menu/GameFinishWallpaper.png"));

		backSprite = new TouchSprite(Gdx.files.internal("menu/GameEndBack.png"), ownCamera);
		backSprite.setPosition((width - backSprite.getWidth()), 5);

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
			gameRef.create(); // TODO könnte man auch anderst lösen...
		}

	}

	public void setGameresult(GameResult gameresult) {
		this.gameresult = gameresult;
	}

	// ---------------------- down libgdx Elements ----------------------

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		gameRef.inputMultiplexer.removeProcessor(backSprite);
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
		// TODO Auto-generated method stub
		gameRef.inputMultiplexer.removeProcessor(backSprite);
	}
}
