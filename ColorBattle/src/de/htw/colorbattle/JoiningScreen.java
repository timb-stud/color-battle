package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.exception.NetworkException;

public class JoiningScreen implements Screen {
	private ColorBattleGame game;
    private SpriteBatch spriteBatch;
    private TouchSprite waitingForPlayerSprite;
	
	public JoiningScreen(ColorBattleGame game) throws NetworkException {
		this.game = game;
		spriteBatch = new SpriteBatch();
		
		waitingForPlayerSprite = new TouchSprite("game_waiting_for.png");
		waitingForPlayerSprite.setPosition((Gdx.graphics.getWidth() - waitingForPlayerSprite.getWidth()) / 2.0f,
										   ( Gdx.graphics.getHeight() - waitingForPlayerSprite.getHeight()) / 2.0f);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		waitingForPlayerSprite.draw(spriteBatch);
		spriteBatch.end();
		
		if(game.mainMenuScreen.multiGame.isGameStarted())
			game.setScreen(game.gameScreen);
		
		//do some other stuff, waiting for players
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
