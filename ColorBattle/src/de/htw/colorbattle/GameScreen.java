package de.htw.colorbattle;

import com.badlogic.gdx.Application;
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
import com.badlogic.gdx.math.Vector2;

import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.network.NetworkService;
import de.htw.colorbattle.network.PlayerMsg;

import de.htw.colorbattle.gameobjects.Player;

public class GameScreen implements Screen {
	private ColorBattleGame game;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture playerTexture;
	private Texture colorTexture;
	private FrameBuffer colorFrameBuffer;
	private TextureRegion flipper;
	private Player player;
	private int width;
	private int height;
	private int playerWidth;
	private int playerHeight;
	private int maxAccelerometer;
	
	private Vector2 last = new Vector2(0, 0);
	private Vector2 current = new Vector2(0,0);
	private NetworkService netSvc;

	public GameScreen(ColorBattleGame game) throws NetworkException {
		this.netSvc = new NetworkService("230.0.0.1", 1234); //TODO: make address and port dynamic
		
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
		player = new Player();
		player.x = width / 2 - playerWidth / 2;
		player.y = height / 2 - playerHeight / 2;
		player.radius = playerWidth / 2;
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
		batch.end();
		
		float accX = Gdx.input.getAccelerometerX();
		float accY = Gdx.input.getAccelerometerY();
		if(accX > maxAccelerometer) accX = maxAccelerometer;
		else if(accX < -maxAccelerometer) accX = -maxAccelerometer;
		if(accY > maxAccelerometer) accY = maxAccelerometer;
		else if(accY < -maxAccelerometer) accY = -maxAccelerometer;
		player.directionX = accY;
		player.directionY = accX ;
		
		player.move();
		
		if(Gdx.input.isKeyPressed(Keys.UP)) player.y += player.speed * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.DOWN)) player.y -= player.speed * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.LEFT)) player.x -= player.speed * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) player.x += player.speed * Gdx.graphics.getDeltaTime();
		
		if(player.x < 0) player.x = 0;
		else if(player.x > width - playerWidth) player.x = width - playerWidth;
		if(player.y < 0) player.y = 0;
		else if(player.y > height -playerHeight) player.y = height - playerHeight;
		
		current.set(player.x, player.y);
        if(current.dst2(last) > 3){
                last.set(player.x, player.y);
                sendPosition();
        }
	}

	private void sendPosition() {
		PlayerMsg msg = new PlayerMsg(player.x, player.y);
		try {
			netSvc.send(msg);
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "Can't create network connection.", e);
			e.printStackTrace(); //TODO Handle exception
		}
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
		colorTexture.dispose();
		colorFrameBuffer.dispose();
		batch.dispose();
	}
}