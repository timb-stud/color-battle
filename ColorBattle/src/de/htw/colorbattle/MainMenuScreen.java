package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuScreen implements Screen, InputProcessor {
	private ColorBattleGame game;
    private SpriteBatch spriteBatch;
    private Sprite newGameSprite;
    private Sprite exitGameSprite;
    
    /**
     * Constructor for the menue screen
     * @param ColorBattleGame game which called this menue screen.
     */
	public MainMenuScreen(ColorBattleGame game) {
		this.game = game;
		spriteBatch = new SpriteBatch();
		
		newGameSprite = new Sprite(new Texture(Gdx.files.internal("new_game.png")));
		newGameSprite.setPosition((Gdx.graphics.getWidth() - newGameSprite.getWidth()) / 2.0f, Gdx.graphics.getHeight() - 200.0f);
		
		exitGameSprite = new Sprite(new Texture(Gdx.files.internal("exit_game.png")));
		exitGameSprite.setPosition((Gdx.graphics.getWidth() - exitGameSprite.getWidth()) / 2.0f, Gdx.graphics.getHeight() - 400.0f);
		
        Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		newGameSprite.draw(spriteBatch);
		exitGameSprite.draw(spriteBatch);
		spriteBatch.end();

//		// update and draw stuff
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        spriteBatch.begin();
//        spriteBatch.draw(splsh, 0, 0);
//        spriteBatch.end();
        
        if (Gdx.input.justTouched()) {// use your own criterion here
        	Gdx.app.log("MainMenuScreen", "Just Touched");
            game.setScreen(game.gameScreen);
            game.gameScreen.j = System.currentTimeMillis() / 1000;
        }
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// called when this screen is set as the screen with game.setScreen();
	}

	@Override
	public void hide() {
		// called when current screen changes from this to a different screen
	}

	@Override
	public void pause() {		
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		// never called automatically!!!
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (newGameSprite.getBoundingRectangle().contains(x, Gdx.graphics.getHeight() - y)) {
			game.setScreen(game.gameScreen);
		}
		if (exitGameSprite.getBoundingRectangle().contains(x, Gdx.graphics.getHeight() - y)) {
			Gdx.app.exit();
		}
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}