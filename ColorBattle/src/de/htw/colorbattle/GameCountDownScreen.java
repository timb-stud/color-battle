package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.toast.Toast;

public class GameCountDownScreen implements Screen {
	private ColorBattleGame game;
	long oldTime;
	Texture t;
	SpriteBatch batch;
	private short countdown = 3;
	private Toast toast;

	public GameCountDownScreen(ColorBattleGame game) {
		this.game = game;
		oldTime = System.currentTimeMillis();
		t = new Texture(Gdx.files.internal("menu/3.png"));
		batch = new SpriteBatch();
		this.toast  = new Toast(7, 6);
		
		String message = "You are ";
		if (/*game.multiGame.isServer()*/ true) {
			message += "GREEN";
		} else {
			message += "RED";
		}

		toast.makeText(message, "font", 
				Toast.COLOR_PREF.BLUE, Toast.STYLE.NORMAL, Toast.TEXT_POS.middle_down, Toast.TEXT_POS.middle_down, Toast.MED);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (countdown == 0) {
			game.setScreen(game.gameScreen);
		} else if(System.currentTimeMillis() - oldTime > 1000) {
			oldTime = System.currentTimeMillis();
			setTexture();
			countdown--;
		}
		
		
		batch.begin();
		batch.draw(t, (game.camera.viewportWidth - t.getWidth()) / 2, (game.camera.viewportHeight - t.getHeight()) / 2);
		batch.end();
		
		toast.toaster();
	}

	private void setTexture() {
		t = new Texture(Gdx.files.internal("menu/" + countdown + ".png"));
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
		
	}

}
