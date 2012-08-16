package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuScreen implements Screen {
	private ColorBattleGame game;
    private SpriteBatch spriteBatch;
    private TouchSprite newGameSprite;
    private TouchSprite exitGameSprite;
    
    /**
     * Constructor for the menue screen
     * @param ColorBattleGame game which called this menue screen.
     */
	public MainMenuScreen(ColorBattleGame game) {
		this.game = game;
		spriteBatch = new SpriteBatch();
		
		newGameSprite = new TouchSprite("new_game.png");
		newGameSprite.setPosition((Gdx.graphics.getWidth() - newGameSprite.getWidth()) / 2.0f, Gdx.graphics.getHeight() - 200.0f);
		
		exitGameSprite = new TouchSprite("exit_game.png");
		exitGameSprite.setPosition((Gdx.graphics.getWidth() - exitGameSprite.getWidth()) / 2.0f, Gdx.graphics.getHeight() - 400.0f);
		
		game.inputMultiplexer.addProcessor(newGameSprite);
		game.inputMultiplexer.addProcessor(exitGameSprite);
		Gdx.input.setInputProcessor(game.inputMultiplexer);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		newGameSprite.draw(spriteBatch);
		exitGameSprite.draw(spriteBatch);
		spriteBatch.end();
		
		if (newGameSprite.isTouched()) {
			game.setScreen(game.gameScreen);
		}
		
		if (exitGameSprite.isTouched()) {
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
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {		
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);
	}
}