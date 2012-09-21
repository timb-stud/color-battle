package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.GameResult;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.toast.Toast;

/**
 * GameEndMenu erstellt die Oberfläche,
 * inklusive Buttons und Skalierung,
 * zum anzeigen des Spielergebnisses
 */
public class GameEndMenu implements Screen {

	private ColorBattleGame gameRef;
	private SpriteBatch ownBatch;
	private OrthographicCamera ownCamera;

	private boolean scoreComputed = false;
	private GameResult gameresult;
	private Toast render_toast;

	private Texture wallpaper;
	private Texture scoreTexture;
	private InputMultiplexer inputMulti;
	private TouchSprite backSprite;

	public GameEndMenu(ColorBattleGame game) {
		this.gameRef = game;
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);
		ownBatch = new SpriteBatch();

		wallpaper = new Texture(
				Gdx.files.internal("menu/GameFinishWallpaper.png"));

		backSprite = new TouchSprite(
				Gdx.files.internal("menu/GameEndBack.png"), ownCamera);
		backSprite.setPosition(5, 5);

		// for Touch-Events
		inputMulti = new InputMultiplexer();
		inputMulti.addProcessor(backSprite);
		Gdx.input.setInputProcessor(inputMulti);
		
		this.render_toast  = new Toast(7, 6);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		ownBatch.setProjectionMatrix(ownCamera.combined);

		ownBatch.begin();
		ownBatch.draw(wallpaper, 0, 0);
		backSprite.draw(ownBatch);
		ownBatch.end();

		if (scoreComputed) {
			if (scoreTexture != null) {
				ownBatch.begin();
				ownBatch.draw(scoreTexture, 150, -10);
				ownBatch.end();
			}
		} else if (gameresult != null) {
			this.scoreTexture = gameresult.getScoreScreen(ownBatch, ownCamera);
			scoreComputed = true;
		}
		
		render_toast.toaster();

		if (backSprite.isTouched()) {
			backSprite.resetIsTouched();
			gameRef.setShowSplashScreen(false);
			gameRef.create(); // TODO könnte man auch anderst lösen...
			this.dispose();
		}
	}

	public void setGameresult(GameResult gameresult) {
		this.gameresult = gameresult;
		
		String toastMsg = "";
		float score;
		for (Player currentPlayer : gameresult.getScoredPlayerList()){
			score = (float) ((float) Math.round((currentPlayer.getGameScore()) * 100.0) / 100.0);
			toastMsg = toastMsg + " PlayerID: " + currentPlayer.id + " Score: "+ score+"\n"; 
		}
		render_toast.makeText(toastMsg, "font", 
				Toast.COLOR_PREF.BLUE, Toast.STYLE.NORMAL, Toast.TEXT_POS.middle_up, Toast.TEXT_POS.middle_up, Toast.LONG*2.0f);
	
	}

	@Override
	public void dispose() {
		gameRef.inputMultiplexer.removeProcessor(backSprite);// TODO was macht der input multiplexer vom game überhaupt mit dem backsprite ??? ???
		inputMulti.removeProcessor(backSprite);
		inputMulti = null;
		ownBatch.dispose();
		ownCamera = null;
		wallpaper.dispose();
		scoreTexture.dispose();
		backSprite.disposeTouchSprite();
		backSprite = null;
		gameresult = null;
		render_toast=null;
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
