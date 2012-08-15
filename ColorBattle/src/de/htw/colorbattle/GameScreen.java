package de.htw.colorbattle;




import java.util.Date;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

public class GameScreen implements Screen {
	private ColorBattleGame game;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture playerTexture;
	private Texture colorTexture;
	private Circle player;
	private FrameBuffer colorFrameBuffer;
	private TextureRegion flipper;
	private int width;
	private int height;
	private int playerWidth;
	private int playerHeight;
	private int playerVelocity;
	private int maxAccelerometer;
	
	private Texture timerTexture;
	private Circle timer;
	private Texture endTexture;
	private Rectangle end;
	private int timerWidth;
	private int endWidth;
	private int endHeight;
	
	private long i = System.currentTimeMillis() / 1000;
	public long j;
	

	public GameScreen(ColorBattleGame game) {
		this.game = game;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		maxAccelerometer = 3;
		width = 800;
		height = 480;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);
		batch = new SpriteBatch();

		colorFrameBuffer = new FrameBuffer(Format.RGBA8888, width, height, false);
		flipper = new TextureRegion();
		playerTexture = new Texture(Gdx.files.internal("player.png"));
		colorTexture = new Texture(Gdx.files.internal("color.png"));
		playerWidth = playerTexture.getWidth();
		playerHeight = playerTexture.getHeight();
		playerVelocity = 200;
		player = new Circle(width / 2 - playerWidth / 2, height / 2 - playerHeight / 2, playerWidth / 2);
		
		timerTexture = new Texture (Gdx.files.internal("Timer1.png"));
		timerWidth = timerTexture.getWidth();
		timer = new Circle(0,0, timerWidth / 2);
		
		endTexture = new Texture(Gdx.files.internal("End.png"));
		endHeight = endTexture.getHeight();
		endWidth = endTexture.getWidth();
		end = new Rectangle(400,240,endWidth,endHeight);
		
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		colorFrameBuffer.begin();
		batch.begin();
		batch.draw(colorTexture, player.x, player.y);
		batch.end();
		colorFrameBuffer.end();
		
		flipper.setRegion(colorFrameBuffer.getColorBufferTexture());
		flipper.flip(false, true);
		
		batch.begin();
		batch.draw(flipper, 0, 0);
		batch.draw(playerTexture, player.x, player.y);
		batch.draw(timerTexture,timer.x,timer.y);
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
		
		i = System.currentTimeMillis() / 1000;
	
		if (i == (j+15)){changeTimer("Timer2.png");}
		if (i == (j+30)){changeTimer("Timer3.png");}
		if (i == (j+45)){changeTimer("Timer4.png");}
		if (i == (j+60)){changeTimer("Timer5.png");
							batch.begin();
							batch.draw(endTexture,end.x,end.y);
							batch.end();
							playerTexture.dispose();
							colorTexture.dispose();
							colorFrameBuffer.dispose();}
		
				
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
		playerTexture.dispose();
		timerTexture.dispose();
		endTexture.dispose();
		colorTexture.dispose();
		colorFrameBuffer.dispose();
		batch.dispose();
	}
	
	
	
	public void changeTimer(String timerImage){
		timerTexture = new Texture (Gdx.files.internal(timerImage));
   	 	batch.begin();
		batch.draw(timerTexture,timer.x,timer.y);
		batch.end();
		
	}
	
}