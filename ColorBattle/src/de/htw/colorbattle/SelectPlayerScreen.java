package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.multiplayer.MultigameLogic;

public class SelectPlayerScreen implements Screen {
	private ColorBattleGame game;
	
    private SpriteBatch batch;
    private TouchSprite twoPlayerSprite;
    private TouchSprite threePlayerSprite;
    private TouchSprite fourPlayerSprite;
    private float width;
    private float height;
    
    /**
     * Constructor for the menue screen
     * @param ColorBattleGame game which called this menue screen.
     */
	public SelectPlayerScreen(ColorBattleGame game) {
		this.game = game;
		batch = new SpriteBatch();
		
		width = game.camera.viewportWidth;
		height = game.camera.viewportHeight;
		
		twoPlayerSprite = new TouchSprite(Gdx.files.internal("menu/2Player.png"), game.camera);
		twoPlayerSprite.setPosition((width - twoPlayerSprite.getWidth()) / 2.0f,
									height - twoPlayerSprite.getHeight());
		
		threePlayerSprite = new TouchSprite(Gdx.files.internal("menu/3Player.png"), game.camera);
		threePlayerSprite.setPosition((width - threePlayerSprite.getWidth()) / 2.0f,
									  (height - threePlayerSprite.getHeight()) / 2.0f);
		
		fourPlayerSprite = new TouchSprite(Gdx.files.internal("menu/4Player.png"), game.camera);
		fourPlayerSprite.setPosition((width - fourPlayerSprite.getWidth()) / 2.0f, 0);
		
		game.inputMultiplexer.addProcessor(twoPlayerSprite);
		game.inputMultiplexer.addProcessor(threePlayerSprite);
		game.inputMultiplexer.addProcessor(fourPlayerSprite);
		Gdx.input.setInputProcessor(game.inputMultiplexer);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.camera.update();
		batch.setProjectionMatrix(game.camera.combined);
		
		batch.begin();
		twoPlayerSprite.draw(batch);
		threePlayerSprite.draw(batch);
		fourPlayerSprite.draw(batch);
		batch.end();
		
		boolean playerSetted = false;
		if (twoPlayerSprite.isTouched()) {
			twoPlayerSprite.resetIsTouched();
			game.bcConfig.multigamePlayerCount = 2;
			playerSetted = true;
		} else if (threePlayerSprite.isTouched()) {
			threePlayerSprite.resetIsTouched();
			game.bcConfig.multigamePlayerCount = 3;
			playerSetted = true;
		} else if (fourPlayerSprite.isTouched()) {
			fourPlayerSprite.resetIsTouched();
			game.bcConfig.multigamePlayerCount = 4;
			playerSetted = true;
		}
		if (playerSetted) {
			try {
				game.multiGame = new MultigameLogic(game.bcConfig, true,
											   game.gameScreen.getPlayerSimulation());
				game.multiGame.startServer();
				game.setScreen(game.joiningScreen);
			} catch (NetworkException e) {
				Gdx.app.error("Network Service", "Mainmenu sending problem");
			}
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
		game.inputMultiplexer.removeProcessor(twoPlayerSprite);
		game.inputMultiplexer.removeProcessor(threePlayerSprite);
		game.inputMultiplexer.removeProcessor(fourPlayerSprite);
	}

	@Override
	public void pause() {		
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		game.inputMultiplexer.removeProcessor(twoPlayerSprite);
		game.inputMultiplexer.removeProcessor(threePlayerSprite);
		game.inputMultiplexer.removeProcessor(fourPlayerSprite);
	}
}