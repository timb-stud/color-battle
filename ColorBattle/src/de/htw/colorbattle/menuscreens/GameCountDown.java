package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.toast.Toast;
import de.htw.colorbattle.toast.Toast.TEXT_POS;
import de.htw.colorbattle.utils.ColorHelper;

/**
 * GameCountDown prints a Countdown to start the Game
 * This Screen is called from JoiningScreen, when all player joined
 */
public class GameCountDown implements Screen {
	private ColorBattleGame gameRef;
	private OrthographicCamera ownCamera;

	private Texture texture;
	private SpriteBatch batch;

	private short countdown = 3;
	private long oldTime;

	private Toast toast;

	/*
	 * Constructor
	 */
	public GameCountDown(ColorBattleGame game) {
		this.gameRef = game;
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);

		batch = new SpriteBatch();
		texture = new Texture(Gdx.files.internal("menu/3.png"));

		oldTime = System.currentTimeMillis();
		
		//Prints own player informations as toast message
		Player player = game.gameScreen.getPlayer();
		String colorName = ColorHelper.getColorName(player.color);
		String message = "You are Player " + player.id + " with " + colorName + " color!";
		game.toast.makeText(message, "font", Toast.COLOR_PREF.BLUE,
				Toast.STYLE.NORMAL, TEXT_POS.middle, TEXT_POS.middle_down,
				Toast.LONG);
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(1, 1, 1, 1);

		batch.setProjectionMatrix(ownCamera.combined);
		batch.begin();
		batch.draw(texture,
				((BattleColorConfig.WIDTH - texture.getWidth()) / 2),
				(BattleColorConfig.HEIGHT - texture.getHeight()) / 2);
		batch.end();

		if (System.currentTimeMillis() - oldTime > 1000) { // Eine Sekunde
															// vergangen
			if (countdown == 1) {
				gameRef.setScreen(gameRef.gameScreen);
			} else {
				oldTime = System.currentTimeMillis();
				countdown--;
				setTexture();
			}
		}
		if (toast != null) {
			toast.toaster();
		}
	}

	/*
	 * Sets the a new Texture and dispose the old one
	 */
	private void setTexture() {
		if (texture != null) {
			texture.dispose();
			texture = null;
		}
		texture = new Texture(Gdx.files.internal("menu/" + countdown + ".png"));
	}

	/*
	 * Dispose all objects used in this class
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
		ownCamera = null;
		texture.dispose();
		batch.dispose();
		oldTime = 0;
		toast = null;
	}

	// other methods not need here
	
	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	@Override
	public void resume() {
	}
}
