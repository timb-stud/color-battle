package de.htw.colorbattle;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

public class ColorBattleGame implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture playerTexture;
	private Circle player;
	private int width;
	private int height;
	private int playerWidth;
	private int playerHeight;
	private int playerVelocity;
	
	@Override
	public void create() {
		width = 800;
		height = 480;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);
		batch = new SpriteBatch();
		
		playerTexture = new Texture(Gdx.files.internal("player.png"));
		playerWidth = playerTexture.getWidth();
		playerHeight = playerTexture.getHeight();
		playerVelocity = 300;
		player = new Circle(width / 2 - playerWidth / 2, height / 2 - playerHeight / 2, playerWidth / 2);
		
	}

	@Override
	public void dispose() {
		playerTexture.dispose();
		batch.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		batch.draw(playerTexture, player.x, player.y);
		batch.end();
		
		if(Gdx.input.getAccelerometerX() < -2 
				|| Gdx.input.isKeyPressed(Keys.UP)) player.y += playerVelocity * Gdx.graphics.getDeltaTime();
		if(Gdx.input.getAccelerometerX() > 2 
				|| Gdx.input.isKeyPressed(Keys.DOWN)) player.y -= playerVelocity * Gdx.graphics.getDeltaTime();
		if(Gdx.input.getAccelerometerY() < -2  
				|| Gdx.input.isKeyPressed(Keys.LEFT)) player.x -= playerVelocity * Gdx.graphics.getDeltaTime();
		if(Gdx.input.getAccelerometerY() > 2 
				|| Gdx.input.isKeyPressed(Keys.RIGHT)) player.x += playerVelocity * Gdx.graphics.getDeltaTime();
		
		if(player.x < 0) player.x = 0;
		if(player.x > width - playerWidth) player.x = width - playerWidth;
		if(player.y < 0) player.y = 0;
		if(player.y > height -playerHeight) player.y = height - playerHeight;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
