package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.htw.colorbattle.toast.Toast;

public class SplashScreen implements Screen{
	
    private SpriteBatch spriteBatch;
    private Texture splash;
    private ColorBattleGame game;
    private Toast render_toast = new Toast(7, 6);
    
    /**
     * Constructor for the splash screen
     * @param g Game which called this splash screen.
     */
    public SplashScreen(ColorBattleGame g)
    {
            game = g;
    }

    @Override
    public void render(float delta)
    {
    		Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    		game.camera.update();
    		spriteBatch.setProjectionMatrix(game.camera.combined);
            
            spriteBatch.begin();
            spriteBatch.draw(splash, 0, 0);
            spriteBatch.end();
            
            render_toast.toaster();
            
            if(Gdx.input.justTouched())
                    game.setScreen(game.mainMenuScreen);
    }
    
    @Override
    public void show()
    {
            spriteBatch = new SpriteBatch();
            splash = new Texture(Gdx.files.internal("splash.png"));
            
    		render_toast.makeText("Welcome to Game", "font", 
					Toast.COLOR_PREF.BLUE, Toast.STYLE.NORMAL, Toast.TEXT_POS.middle_up, Toast.TEXT_POS.middle_up, Toast.MED);
    }

	@Override
	public void resize(int width, int height) {
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

	}

}
