package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.exception.NetworkException;

/**
 * JoiningScreen erstellt die Oberfl�che,
 * inklusive Buttons und Skalierung,
 * welche zum warten auf andere Spieler benutzt wird.
 */
public class JoiningScreen implements Screen {
	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	private TouchSprite waitingForPlayerSprite;

	public JoiningScreen(ColorBattleGame game) throws NetworkException {
		this.gameRef = game;
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);
		ownBatch = new SpriteBatch();

		float width = BattleColorConfig.WIDTH;
		float height = BattleColorConfig.HEIGHT;

		waitingForPlayerSprite = new TouchSprite(
				Gdx.files.internal("menu/WaitingForPlayer.png"), ownCamera);
		waitingForPlayerSprite.setPosition(
				(width - waitingForPlayerSprite.getWidth()) / 2.0f,
				(height - waitingForPlayerSprite.getHeight()) / 2.0f);
		//TODO back button
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		ownBatch.setProjectionMatrix(ownCamera.combined);

		ownBatch.begin();
		waitingForPlayerSprite.draw(ownBatch);
		ownBatch.end();

		if (gameRef.multiGame.isGameStarted()) {
			gameRef.setScreen(new GameCountDownScreen(gameRef));
			this.dispose();
		}
	}

	@Override
	public void dispose() {
		ownBatch.dispose();
		ownCamera = null;
		waitingForPlayerSprite.disposeTouchSprite();
		waitingForPlayerSprite = null;
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