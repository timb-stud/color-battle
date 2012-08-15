package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuScreen implements Screen {
	private ColorBattleGame game;
    private SpriteBatch spriteBatch;
    private Texture splsh;
    
    /**
     * Constructor for the splash screen
     * @param g Game which called this splash screen.
     */
	public MainMenuScreen(ColorBattleGame game) {
		this.game = game;
		spriteBatch = new SpriteBatch();
        splsh = new Texture(Gdx.files.internal("menue.png"));
	}
	
	@Override
	public void render(float delta) {
		// update and draw stuff
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(splsh, 0, 0);
        spriteBatch.end();
        
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
}