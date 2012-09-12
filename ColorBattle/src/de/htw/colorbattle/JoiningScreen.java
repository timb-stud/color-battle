package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.config.GameMode;
import de.htw.colorbattle.exception.NetworkException;

public class JoiningScreen implements Screen {
	private ColorBattleGame game;
    private SpriteBatch batch;
    private TouchSprite waitingForPlayerSprite;
    private float width;
    private float height;
    int maxPlayer;
	
	public JoiningScreen(ColorBattleGame game) throws NetworkException {
		this.game = game;
		batch = new SpriteBatch();
		
		width = game.camera.viewportWidth;
		height = game.camera.viewportHeight;
		
		waitingForPlayerSprite = new TouchSprite(Gdx.files.internal("menu/WaitingForPlayer.png"), game.camera);
		waitingForPlayerSprite.setPosition((width - waitingForPlayerSprite.getWidth()) / 2.0f,
										   (height - waitingForPlayerSprite.getHeight()) / 2.0f);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.camera.update();
		batch.setProjectionMatrix(game.camera.combined);
		
		batch.begin();
		waitingForPlayerSprite.draw(batch);
		batch.end();
		
//		if(game.bcConfig.gameMode == GameMode.BLUETOOTH) && game.multiGame.isGameStarted())
			game.setScreen(game.gameScreen);
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
	}
}
