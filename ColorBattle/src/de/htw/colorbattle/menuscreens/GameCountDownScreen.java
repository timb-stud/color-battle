package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.toast.Toast;

public class GameCountDownScreen implements Screen {
	private ColorBattleGame gameRef;
	private OrthographicCamera ownCamera;
	
	private Texture texture;
	private SpriteBatch batch;
	
	private short countdown = 3;
	private long oldTime;
	
	private Toast toast;

	public GameCountDownScreen(ColorBattleGame game) {
		this.gameRef = game;
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);
		
		batch = new SpriteBatch();
		texture = new Texture(Gdx.files.internal("menu/3.png"));
		
		oldTime = System.currentTimeMillis();
		
		this.toast  = new Toast(7, 6);
		String message = "You are ";
		if (game.multiGame.isServer()) {
			message += "RED";
		} else {
			message += "GREEN";
		}
		toast.makeText(message, "font", 
				Toast.COLOR_PREF.BLUE, Toast.STYLE.NORMAL, Toast.TEXT_POS.middle_down, Toast.TEXT_POS.middle_down, Toast.MED);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		
		batch.setProjectionMatrix(ownCamera.combined);
		batch.begin();
		batch.draw(texture, ((BattleColorConfig.WIDTH-texture.getWidth())/2), (BattleColorConfig.HEIGHT-texture.getHeight())/2);
		batch.end();
		toast.toaster();
		
		if (countdown == 0) {
			gameRef.setScreen(gameRef.gameScreen); //TODO schöner
			this.dispose();
		} else if(System.currentTimeMillis() - oldTime > 1000) {
			oldTime = System.currentTimeMillis();
			setTexture();
			countdown--;
		}
	}

	private void setTexture() {
		texture = new Texture(Gdx.files.internal("menu/" + countdown + ".png"));
	}

	@Override
	public void dispose() {
		ownCamera= null;
		texture.dispose();
		batch.dispose();
		oldTime = 0;
		toast = null;
	}
	
	// other methods not need here
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

}
