package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
		
		if (joinGameSprite.isTouched()) {
			joinGameSprite.resetIsTouched();
			game.bluetoothActionResolver.connect();
			game.showJoiningScreen();
		} else if (startServerSprite.isTouched()) {
			startServerSprite.resetIsTouched();
			game.bluetoothActionResolver.startServer();
			game.showJoiningScreen();
		} else if (exitGameSprite.isTouched()) {
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