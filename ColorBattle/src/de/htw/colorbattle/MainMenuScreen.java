package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.config.GameMode;
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.multiplayer.MultigameLogic;

public class MainMenuScreen implements Screen {
	private ColorBattleGame game;
	
    private SpriteBatch batch;
    private TouchSprite joinGameSprite;
    private TouchSprite startServerSprite;
    private TouchSprite exitGameSprite;
    private float width;
    private float height;

    public boolean isServer = false; //TODO variable only for PoC
    
    /**
     * Constructor for the menue screen
     * @param ColorBattleGame game which called this menue screen.
     */
	public MainMenuScreen(ColorBattleGame game) {
		this.game = game;
		width = game.camera.viewportWidth;
		height = game.camera.viewportHeight;
		batch = new SpriteBatch();
		
		joinGameSprite = new TouchSprite(Gdx.files.internal("menu/JoinGame.png"), game.camera);
		joinGameSprite.setPosition((width - joinGameSprite.getWidth()) / 2.0f,
									height - joinGameSprite.getHeight());
		
		startServerSprite = new TouchSprite(Gdx.files.internal("menu/StartServer.png"), game.camera);
		startServerSprite.setPosition((width - startServerSprite.getWidth()) / 2.0f,
									  (height - startServerSprite.getHeight()) / 2.0f);
		
		exitGameSprite = new TouchSprite(Gdx.files.internal("menu/ExitGame.png"), game.camera);
		exitGameSprite.setPosition((width - exitGameSprite.getWidth()) / 2.0f, 0);
		
		game.inputMultiplexer.addProcessor(joinGameSprite);
		game.inputMultiplexer.addProcessor(startServerSprite);
		game.inputMultiplexer.addProcessor(exitGameSprite);
		Gdx.input.setInputProcessor(game.inputMultiplexer);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.camera.update();
		batch.setProjectionMatrix(game.camera.combined);
		
		batch.begin();
		joinGameSprite.draw(batch);
		startServerSprite.draw(batch);
		exitGameSprite.draw(batch);
		batch.end();
		
		try {
			if (joinGameSprite.isTouched()) {
				joinGameSprite.resetIsTouched();
				 if(game.bcConfig.gameMode == GameMode.WIFI){
					 game.multiGame = new MultigameLogic(game, false);
					game.multiGame.joinGame();
				} else {
					game.bluetoothActionResolver.connect();
				}
				game.setScreen(game.joiningScreen);
			} else if (startServerSprite.isTouched()) {
				startServerSprite.resetIsTouched();
				game.setScreen(game.selectplayerScreen);
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
		game.inputMultiplexer.removeProcessor(startServerSprite);
		game.inputMultiplexer.removeProcessor(joinGameSprite);
		game.inputMultiplexer.removeProcessor(exitGameSprite);
	}

	@Override
	public void pause() {		
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		game.inputMultiplexer.removeProcessor(startServerSprite);
		game.inputMultiplexer.removeProcessor(joinGameSprite);
		game.inputMultiplexer.removeProcessor(exitGameSprite);
	}
}