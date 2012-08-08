package de.htw.colorbattle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

public class GameScreen implements Screen {
	private ColorBattleGame game;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture playerTexture;
	private Texture colorTexture;
	private Circle player;
	private int width;
	private int height;
	private int playerWidth;
	private int playerHeight;
	private int playerVelocity;
	private int maxAccelerometer;

	public GameScreen(ColorBattleGame game) {
		this.game = game;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		maxAccelerometer = 3;
		width = 800;
		height = 480;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);
		batch = new SpriteBatch();

		playerTexture = new Texture(Gdx.files.internal("player.png"));
		colorTexture = new Texture(Gdx.files.internal("color.png"));
		playerWidth = playerTexture.getWidth();
		playerHeight = playerTexture.getHeight();
		playerVelocity = 200;
		player = new Circle(width / 2 - playerWidth / 2, height / 2 - playerHeight / 2, playerWidth / 2);
	}
	
	@Override
	public void render(float delta) {
		Gdx.app.log("GameScreen", "render();");
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		batch.draw(colorTexture, player.x, player.y);
		batch.end();
		
		
		float accX = Gdx.input.getAccelerometerX();
		float accY = Gdx.input.getAccelerometerY();
		if(accX > maxAccelerometer) accX = maxAccelerometer;
		else if(accX < -maxAccelerometer) accX = -maxAccelerometer;
		if(accY > maxAccelerometer) accY = maxAccelerometer;
		else if(accY < -maxAccelerometer) accY = -maxAccelerometer;
		
		player.y -= playerVelocity * accX * Gdx.graphics.getDeltaTime();
		player.x += playerVelocity * accY * Gdx.graphics.getDeltaTime();
		
		if(Gdx.input.isKeyPressed(Keys.UP)) player.y += playerVelocity * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.DOWN)) player.y -= playerVelocity * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.LEFT)) player.x -= playerVelocity * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) player.x += playerVelocity * Gdx.graphics.getDeltaTime();
		
		if(player.x < 0) player.x = 0;
		else if(player.x > width - playerWidth) player.x = width - playerWidth;
		if(player.y < 0) player.y = 0;
		else if(player.y > height -playerHeight) player.y = height - playerHeight;
		batch.begin();
		batch.draw(playerTexture, player.x, player.y);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		Gdx.app.log("GameScreen", "show();");
		
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