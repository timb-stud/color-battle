package de.htw.colorbattle;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.gameobjects.PlayerSimulation;
import de.htw.colorbattle.input.Accelerometer;
import de.htw.colorbattle.network.NetworkService;

public class GameScreen implements Screen, Observer {
	private ColorBattleGame game;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture playerTexture;
	private FrameBuffer colorFrameBuffer;
	private TextureRegion flipper;
	private Player player;
	private PlayerSimulation playerSimulation;
	private Player otherPlayer;
	private int width;
	private int height;
	private NetworkService netSvc;
	
	private Texture timerTexture;
	private Circle timer;
	private Texture endTexture;
	private Rectangle end;
	private int timerWidth;
	private int endWidth;
	private int endHeight;
	
	private long i = System.currentTimeMillis() / 1000;
	public long j;
	
	private int ownId;

	public GameScreen(ColorBattleGame game) throws NetworkException {
		this.game = game;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		width = 800;
		height = 480;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);
		batch = new SpriteBatch();
		
		colorFrameBuffer = new FrameBuffer(Format.RGBA8888, width, height, false);
		flipper = new TextureRegion();
		playerTexture = new Texture(Gdx.files.internal("player.png"));
		int playerWidth = playerTexture.getWidth();
		int playerHeight = playerTexture.getHeight();
		player = new Player(Color.BLUE, playerWidth / 2);
		player.x = width / 2 - playerWidth / 2;
		player.y = height / 2 - playerHeight / 2;
		
		playerSimulation = new PlayerSimulation(player);
		
		otherPlayer = new Player(Color.GREEN, playerWidth / 2);
		otherPlayer.x = width / 2 - playerWidth / 2;
		otherPlayer.y = height / 2 - playerHeight / 2;
		
		if (game.bcConfig.isWifiConnected) {
			this.netSvc = new NetworkService(game.bcConfig.multicastAddress, game.bcConfig.multicastPort);
			netSvc.addObserver(this);
		}
		
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
		batch.setColor(player.color);
		batch.begin();
		batch.draw(player.colorTexture, player.x, player.y);
		batch.draw(otherPlayer.colorTexture, otherPlayer.x, otherPlayer.y);
		batch.end();
		colorFrameBuffer.end();
		
		flipper.setRegion(colorFrameBuffer.getColorBufferTexture());
		flipper.flip(false, true);
		
		batch.begin();
		batch.draw(flipper, 0, 0);
		batch.draw(playerTexture, player.x, player.y);
		batch.draw(playerTexture, otherPlayer.x, otherPlayer.y);
		batch.draw(timerTexture,timer.x,timer.y);
		batch.end();
		
		Accelerometer.updateDirection(player.direction);

		player.move();
		playerSimulation.move();
		otherPlayer.move();
		
		if(Gdx.input.isKeyPressed(Keys.UP)) player.y += player.speed * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.DOWN)) player.y -= player.speed * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.LEFT)) player.x -= player.speed * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) player.x += player.speed * Gdx.graphics.getDeltaTime();
		
		if(player.x < 0) player.x = 0;
		else if(player.x > width - player.radius * 2) player.x = width - player.radius * 2;
		if(player.y < 0) player.y = 0;
		else if(player.y > height -player.radius * 2) player.y = height - player.radius * 2;
		
		i = System.currentTimeMillis() / 1000;
		
		if (i == (j+15)){changeTimer("Timer2.png");}
		if (i == (j+30)){changeTimer("Timer3.png");}
		if (i == (j+45)){changeTimer("Timer4.png");}
		if (i == (j+60)){changeTimer("Timer5.png");
							batch.begin();
							batch.draw(endTexture,end.x,end.y);
							batch.end();
							playerTexture.dispose();
							player.dispose();
							colorFrameBuffer.dispose();}
		
		if (netSvc != null){
			if(playerSimulation.distance(player) > game.bcConfig.networkPxlUpdateIntervall){
				playerSimulation.update(player);
				sendPosition();
			}
		}
		
		// testbutton für score
		if(Gdx.input.isKeyPressed(Keys.B)){
			computeScore();
		} 
		
		
	}
	
	private void sendPosition() {
		if (ownId == 0)
			ownId = player.id;
		try {
			netSvc.send(playerSimulation);
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "Can't send position update.", e);
			e.printStackTrace(); //TODO Handle exception
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		Gdx.app.log("GameScreen", "show();");
		
		if (game.bcConfig.playSound){
			game.playSound();
		}
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
		player.dispose();
		timerTexture.dispose();
		endTexture.dispose();
		colorFrameBuffer.dispose();
		batch.dispose();
	}
	
	
	
	public void changeTimer(String timerImage){
		timerTexture = new Texture (Gdx.files.internal(timerImage));
   	 	batch.begin();
		batch.draw(timerTexture,timer.x,timer.y);
		batch.end();
		
	}

	@Override
	public void update(Observable obs, Object obj) {
		if(obj instanceof Player) {
			Player p = (Player)obj;
			if (p.id != ownId){
				//Gdx.app.debug("Player Info", pm.toString());
				otherPlayer.update(p);
			}
		}
	}
	
	private void computeScore(){
		
		int currentPixel;		
		int pixelCount;
		HashMap<Integer, Integer> pixelMap = new HashMap<Integer, Integer>();
		byte[] bytePixelArray = ScreenUtils.getFrameBufferPixels(false);
		
		for(int i= 0 ; i < bytePixelArray.length-4 ; i=i+4){
			currentPixel = (bytePixelArray[i] & 0xFF) 
		            | ((bytePixelArray[i+1] & 0xFF) << 8) 
		            | ((bytePixelArray[i+2] & 0xFF) << 16) 
		            | ((bytePixelArray[i+3] & 0xFF) << 24);
			Integer x = new Integer(currentPixel);
			if(pixelMap.containsKey(currentPixel)){
			    pixelCount = pixelMap.get(currentPixel);
				pixelCount++ ;
				pixelMap.put(currentPixel,pixelCount);
			}else{
				pixelMap.put(currentPixel, 1);
			}
		}
//		System.out.println(pixelMap.size());
//		System.out.println(pixelMap.toString());
		
		
	}
}