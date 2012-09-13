package de.htw.colorbattle;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import de.htw.colorbattle.gameobjects.CountDown;
import de.htw.colorbattle.gameobjects.GameBorder;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.gameobjects.PlayerSimulation;
import de.htw.colorbattle.gameobjects.PowerUp;
import de.htw.colorbattle.input.Accelerometer;

public class GameScreen implements Screen, Observer {
	private ColorBattleGame game;
	private SpriteBatch batch;
	private Texture playerTexture;
	private FrameBuffer colorFrameBuffer;
	private TextureRegion flipper;
	private Player player;
	private PlayerSimulation playerSimulation;
	private Player otherPlayer;
	private PowerUp powerUp;
	private Texture powerUpTexture;
	private GameBorder gameBorder;
	private int width;
	private int height;
	private float extrapolatingTimer = 0;
	private boolean isServer;
	private float powerUpTimer = 0;
	
	private CountDown countDown;
	private long endTime;
	private boolean gameEnd = false;
	private boolean scoreComputed = false;
	public Texture endTexture;
	private LinkedList<Player> playerList = new LinkedList<Player>();
	
	public GameScreen(ColorBattleGame game) {
		this.game = game;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		width = (int) this.game.camera.viewportWidth;
		height = (int) this.game.camera.viewportHeight;
		batch = new SpriteBatch();
		
		isServer = game.bluetoothActionResolver.isServer();

		colorFrameBuffer = new FrameBuffer(Format.RGBA8888, width, height, false);
		flipper = new TextureRegion();
		playerTexture = new Texture(Gdx.files.internal("player.png"));
		powerUp = new PowerUp(0, 0, 64, 64);
		powerUpTexture = new Texture(Gdx.files.internal("powerup.png"));
		
		gameBorder = new GameBorder(width, height);
		int playerWidth = playerTexture.getWidth();
		
		player = new Player(Color.BLUE, playerWidth / 2);
		player.x = 50;
		player.y = 50;
		playerSimulation = new PlayerSimulation(player);
		otherPlayer = new Player(Color.GREEN, playerWidth / 2);
		otherPlayer.x = 600;
		otherPlayer.y = 200;

		playerList.add(player);
		playerList.add(otherPlayer);

		countDown = new CountDown(Color.ORANGE, 480);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
		game.camera.update();
		batch.setProjectionMatrix(game.camera.combined);
		
		colorFrameBuffer.begin();
		batch.begin();
		batch.draw(player.colorTexture, player.x, player.y);
		batch.draw(otherPlayer.colorTexture, otherPlayer.x, otherPlayer.y);
		if(powerUp.isVisible && powerUp.isPickedUpBy(player)) {
			batch.draw(powerUp.getBombTexture(player.color), powerUp.rect.x - powerUp.rect.width, powerUp.rect.y - powerUp.rect.height);
			powerUp.isVisible = false;
		}
		batch.end();
		colorFrameBuffer.end();

		flipper.setRegion(colorFrameBuffer.getColorBufferTexture());
		flipper.flip(false, true);

		batch.begin();
		batch.draw(flipper, 0, 0);
		batch.draw(playerTexture, player.x, player.y);
		batch.draw(playerTexture, otherPlayer.x, otherPlayer.y);
		if(powerUp.isVisible) {
			batch.draw(powerUpTexture, powerUp.rect.x, powerUp.rect.y);
		}
		batch.draw(countDown.countDownTexture, countDown.x, countDown.y);
		batch.end();

		Accelerometer.updateDirection(player.direction);
		if (Gdx.input.isKeyPressed(Keys.UP))
			player.y += player.speed * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.DOWN))
			player.y -= player.speed * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			player.x -= player.speed * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			player.x += player.speed * Gdx.graphics.getDeltaTime();

		player.move();
		playerSimulation.move();
		otherPlayer.move();

		gameBorder.handelCollision(player);
		gameBorder.handelCollision(otherPlayer);
		gameBorder.handelCollision(playerSimulation);
		
		if (!gameEnd){
			gameEnd = countDown.activateCountDown(endTime, game.bcConfig.gameTime);
		}
		if (gameEnd){	
			enterPlayerList();
			game.setScreen(game.gameEndScreen);
			if (scoreComputed){
				if (endTexture != null){
					batch.begin();
//					batch.draw(endTexture, 100, 50);
					batch.end();
				}
			}else {
				this.endTexture = computeScore();
				scoreComputed = true;
			}
		//	playerTexture.dispose();
		//	player.dispose();
		//	colorFrameBuffer.dispose();
		}
		
		powerUpTimer += Gdx.graphics.getDeltaTime();
		if(powerUpTimer > 5){
			powerUpTimer = 0;
			powerUp.shufflePosition();
			powerUp.shuffleType();
			powerUp.isVisible = true;
		}

		extrapolatingTimer += Gdx.graphics.getDeltaTime() * 1000;
		if(playerSimulation.distance(player) > game.bcConfig.networkPxlUpdateIntervall || extrapolatingTimer > 100){
			extrapolatingTimer = 0;
			playerSimulation.update(player);
			sendPosition();
		}
	}

	private void sendPosition() {
		game.bluetoothActionResolver.send(playerSimulation);
	}
	
	public void swapPlayers(){
		Player buffer = player;
		player = otherPlayer;
		otherPlayer = buffer;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		endTime = System.currentTimeMillis() /1000 + game.bcConfig.gameTime;
		Gdx.app.log("GameScreen", "show();");

		if (game.bcConfig.playSound) {
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
		colorFrameBuffer.dispose();
		batch.dispose();
	}

	@Override
	public void update(Observable obs, Object obj) {
		if (obj instanceof PlayerSimulation) {
			PlayerSimulation ps = (PlayerSimulation) obj;
			// Gdx.app.debug("Player Info", pm.toString());
			otherPlayer.update(ps);
		}
	}

	public Texture computeScore() {
		LinkedList<Player> playerList = new LinkedList<Player>();
		playerList.add(player);
		playerList.add(otherPlayer);

		GameResult gr = new GameResult(playerList);
		//System.out.println(gr.getScoredPlayerList().toString());
		//Gdx.app.debug("Player scores", gr.getScoredPlayerList().toString());
		return gr.getScoreScreen(batch);
	}
	
	private void enterPlayerList(){
		this.playerList.add(player);
		this.playerList.add(otherPlayer);
	}
	
	public LinkedList<Player> getPlayerList(){		
		return playerList;
	}

	public PlayerSimulation getPlayerSimulation() {
		return playerSimulation;
	}

	public void setPlayerSimulation(PlayerSimulation playerSimulation) {
		this.playerSimulation = playerSimulation;
	}
	
}