package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class JoiningScreen implements Screen {
	private ColorBattleGame game;
    private SpriteBatch batch;
    private TouchSprite waitingForPlayerSprite;
    private float width;
    private float height;
    private int joinedPlayer = 0;
    private int maxPlayer;
	
	public JoiningScreen(ColorBattleGame game) {
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
		
		if (joinedPlayer == maxPlayer) {
			//game can start, switch to game screen
		}
	}
	
	public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
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