package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.multiplayer.MultigameLogic;

public class GameEndScreen implements Screen {

	private ColorBattleGame game;
	
    private SpriteBatch batch;
    private TouchSprite backSprite;
    private Texture endTexture;
    private Texture scoreTexture;
    private float width;
    private float height;
    private boolean scoreComputed = false;
    

    public boolean isServer = false; //TODO variable only for PoC
    
    /**
     * Constructor for the menue screen
     * @param ColorBattleGame game which called this menue screen.
     */
	public GameEndScreen(ColorBattleGame game) {
		this.game = game;
		width = game.camera.viewportWidth;
		height = game.camera.viewportHeight;
		batch = new SpriteBatch();
		
		backSprite = new TouchSprite(Gdx.files.internal("Back.png"), game.camera);
		backSprite.setPosition((width - backSprite.getWidth()) / 2.0f,0);
		
		endTexture = new Texture(Gdx.files.internal("End.png"));
		
		game.inputMultiplexer.addProcessor(backSprite);
		Gdx.input.setInputProcessor(game.inputMultiplexer);
	}
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.camera.update();
		batch.setProjectionMatrix(game.camera.combined);
		
		batch.begin();
		batch.draw(endTexture,0,0);
		backSprite.draw(batch);
		batch.end();
		
		if (scoreComputed){
			if (scoreTexture != null){
				batch.begin();
				batch.draw(scoreTexture, 100, 50);
				batch.end();
			}
		}else {
			this.scoreTexture = computeScore();
			scoreComputed = true;
		}
		
		
			if (backSprite.isTouched()) {
				backSprite.resetIsTouched();
				game.setScreen(game.mainMenuScreen);
			}
		
	}

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
		game.inputMultiplexer.removeProcessor(backSprite);
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
		game.inputMultiplexer.removeProcessor(backSprite);
	}
	
	private Texture computeScore(){
		GameResult gr = new GameResult(game.gameScreen.getPlayerList());
		return gr.getScoreScreen(batch);
	}

}
