package de.htw.colorbattle;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;



public class GameEndScreen implements Screen {

	private ColorBattleGame game;
	
    private SpriteBatch batch;
    private TouchSprite backSprite;
    private Texture endTexture;
    private Texture scoreTexture;
    private float width;
    private boolean scoreComputed = false;
    private GameResult gameresult;
   
	public GameEndScreen(ColorBattleGame game) {
		this.game = game;
		width = game.camera.viewportWidth;
		batch = new SpriteBatch();
		
		backSprite = new TouchSprite(Gdx.files.internal("Back.png"), game.camera);
		backSprite.setPosition((width - backSprite.getWidth()),5);
		
		endTexture = new Texture(Gdx.files.internal("Finish.png"));
		
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
				batch.draw(scoreTexture, 100, 0);
				batch.end();
			}
		}else {
			this.scoreTexture = gameresult.getScoreScreen(batch);
			scoreComputed = true;
		}
		
		if (backSprite.isTouched()) {
			backSprite.resetIsTouched();
			game.create();
		}
		
	}
	
	public void setGameresult(GameResult gameresult) {
		this.gameresult = gameresult;
	}
	
	// ---------------------- down libgdx Elements ----------------------

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
}
