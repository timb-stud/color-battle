package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.multiplayer.MultigameLogic;

public class MainMenuScreen implements Screen {
	private ColorBattleGame game;
    private SpriteBatch spriteBatch;
    private TouchSprite joinGameSprite;
    private TouchSprite startServerSprite;
    private TouchSprite exitGameSprite;
	public MultigameLogic multiGame;
    
    public boolean isServer = false; //TODO variable only for PoC
    
    /**
     * Constructor for the menue screen
     * @param ColorBattleGame game which called this menue screen.
     */
	public MainMenuScreen(ColorBattleGame game) {
		this.game = game;
		spriteBatch = new SpriteBatch();
		
		joinGameSprite = new TouchSprite("menue_join_game.png");
		joinGameSprite.setPosition((Gdx.graphics.getWidth() - joinGameSprite.getWidth()) / 2.0f, Gdx.graphics.getHeight() - joinGameSprite.getHeight());
		
		startServerSprite = new TouchSprite("menue_start_server.png");
		startServerSprite.setPosition((Gdx.graphics.getWidth() - startServerSprite.getWidth()) / 2.0f,
				( Gdx.graphics.getHeight() - startServerSprite.getHeight()) / 2.0f);
		
		exitGameSprite = new TouchSprite("menue_exit_game.png");
		exitGameSprite.setPosition((Gdx.graphics.getWidth() - exitGameSprite.getWidth()) / 2.0f, 0);
		
		game.inputMultiplexer.addProcessor(joinGameSprite);
		game.inputMultiplexer.addProcessor(startServerSprite);
		game.inputMultiplexer.addProcessor(exitGameSprite);
		Gdx.input.setInputProcessor(game.inputMultiplexer);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		joinGameSprite.draw(spriteBatch);
		startServerSprite.draw(spriteBatch);
		exitGameSprite.draw(spriteBatch);
		spriteBatch.end();
		
		try {
			if (joinGameSprite.isTouched()) {
				joinGameSprite.setIsTouched(false);
				multiGame = new MultigameLogic(game.bcConfig, false, game.gameScreen.getPlayerSimulation());
				multiGame.joinGame();
				game.setScreen(game.joiningScreen);
				//game.setScreen(game.gameScreen);
			} else if (startServerSprite.isTouched()) {
				startServerSprite.setIsTouched(false);
				multiGame = new MultigameLogic(game.bcConfig, true, game.gameScreen.getPlayerSimulation());
				multiGame.startServer();
				game.setScreen(game.joiningScreen);
			} else if (exitGameSprite.isTouched()) {
				Gdx.app.exit();
			}
		} catch (NetworkException e) {
			Gdx.app.error("Network Service", "Mainmenu sending problem");
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