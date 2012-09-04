package de.htw.colorbattle;

import java.util.ArrayList;
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

import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.gameobjects.CountDown;
import de.htw.colorbattle.gameobjects.GameBorder;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.gameobjects.PlayerSimulation;
import de.htw.colorbattle.input.Accelerometer;
import de.htw.colorbattle.network.NetworkService;

public class GameScreen implements Screen, Observer {
	private ColorBattleGame game;
	private SpriteBatch batch;
	private Texture playerTexture;
	private FrameBuffer colorFrameBuffer;
	private TextureRegion flipper;
	private Player player;
	private PlayerSimulation playerSimulation;
	private Player otherPlayer;
	private ArrayList<Player> playerList;
	private GameBorder gameBorder;
	private int width;
	private int height;
	private NetworkService netSvc;
	
	private CountDown countDown;
	private long endTime;
	private boolean gameEnd = false;
	private boolean scoreComputed = false;
	private Texture endTexture;
	
	public GameScreen(ColorBattleGame game) throws NetworkException {
		this.game = game;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		width = (int) this.game.camera.viewportWidth;
		height = (int) this.game.camera.viewportHeight;
		batch = new SpriteBatch();

		colorFrameBuffer = new FrameBuffer(Format.RGBA8888, width, height,
				false);
		flipper = new TextureRegion();
		playerTexture = new Texture(Gdx.files.internal("player.png"));

		gameBorder = new GameBorder(width, height);
		int playerWidth = playerTexture.getWidth();
		int playerHeight = playerTexture.getHeight();
		
		player = new Player(Color.BLUE, playerWidth / 2);
		playerSimulation = new PlayerSimulation(player);
		otherPlayer = new Player(Color.GREEN, playerWidth / 2);

		playerList.add(player);
		playerList.add(otherPlayer);
		for (Player p : playerList){
			p.x = width / 2 - playerWidth / 2;
			p.y = height / 2 - playerHeight / 2;
		}

		if (game.bcConfig.isWifiConnected) {
			this.netSvc = NetworkService.getInstance(game.bcConfig.multicastAddress, game.bcConfig.multicastPort);
			netSvc.addObserver(this);
		}
		countDown = new CountDown(Color.ORANGE, 480);
	}
	
	public void addPlayer(PlayerSimulation ps){
		Player newPlayer = new Player(Color.GREEN, player.radius);
		newPlayer.update(ps);
		this.playerList.add(newPlayer);
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
		batch.end();
		colorFrameBuffer.end();

		flipper.setRegion(colorFrameBuffer.getColorBufferTexture());
		flipper.flip(false, true);

		batch.begin();
		batch.draw(flipper, 0, 0);
		batch.draw(playerTexture, player.x, player.y);
		batch.draw(playerTexture, otherPlayer.x, otherPlayer.y);

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
			game.setScreen(game.gameEndScreen);
			if (scoreComputed){
				if (endTexture != null){
					batch.begin();
					batch.draw(endTexture, 100, 50);
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

		
		if (netSvc != null){
			if(playerSimulation.distance(player) > game.bcConfig.networkPxlUpdateIntervall){
				playerSimulation.update(player);
				sendPosition();
			}
		}
	}

	private void sendPosition() {
		try {
			netSvc.send(playerSimulation);
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "Can't send position update.", e);
			e.printStackTrace(); // TODO Handle exception
		}
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
	public LinkedList<Player> getPlayerList(){
		LinkedList<Player> playerList = new LinkedList<Player>();
		playerList.add(player);
		playerList.add(otherPlayer);
		
		return playerList;
	}

	public PlayerSimulation getPlayerSimulation() {
		return playerSimulation;
	}

	public void setPlayerSimulation(PlayerSimulation playerSimulation) {
		this.playerSimulation = playerSimulation;
	}
	
}