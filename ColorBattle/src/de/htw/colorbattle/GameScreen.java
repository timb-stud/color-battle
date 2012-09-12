package de.htw.colorbattle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

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

public class GameScreen implements Screen {
	private ColorBattleGame game;
	private SpriteBatch batch;
	private Texture playerTexture;
	private FrameBuffer colorFrameBuffer;
	private TextureRegion flipper;
	private Player player;
	private Player otherPlayer;
	private PlayerSimulation playerSimulation;
	private HashMap<Integer, PlayerSimulation> playerMap;
	private GameBorder gameBorder;
	private int width;
	private int height;
	private NetworkService netSvc;
	
	private CountDown countDown;
	private long endTime;
	private boolean gameEnd = false;
	private boolean scoreComputed = false;
	private Texture endTexture;
	private int playerListIndex = 0; //TODO set right id
	
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
		otherPlayer = new Player(Color.GREEN, playerWidth / 2);
		playerSimulation = new PlayerSimulation(player);

		player.x = width / 2 - playerWidth / 2;
		player.y = height / 2 - playerHeight / 2;
		
		playerMap = new HashMap<Integer, PlayerSimulation>();

		if (game.bcConfig.isWifiConnected) {
			this.netSvc = NetworkService.getInstance(game.bcConfig.multicastAddress, game.bcConfig.multicastPort);
		}
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
				for (PlayerSimulation ps : playerMap.values()){
					otherPlayer.update(ps);
					batch.draw(otherPlayer.colorTexture, ps.x, ps.y);
				}
				batch.draw(player.colorTexture, player.x, player.y);
			batch.end();
		colorFrameBuffer.end();

		flipper.setRegion(colorFrameBuffer.getColorBufferTexture());
		flipper.flip(false, true);

		batch.begin();
			batch.draw(flipper, 0, 0);
			batch.draw(playerTexture, player.x, player.y);
			for (PlayerSimulation ps : playerMap.values()){
				batch.draw(playerTexture, ps.x, ps.y);
			}
			batch.draw(playerTexture, player.x, player.y);
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
		gameBorder.handelCollision(player);
		gameBorder.handelCollision(playerSimulation);
		for (PlayerSimulation ps : playerMap.values()){
			otherPlayer.update(ps);
			otherPlayer.move();
			gameBorder.handelCollision(otherPlayer);
		}
		player.move();
		playerSimulation.move();
		gameBorder.handelCollision(player);
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


	public Texture computeScore() {
		LinkedList<Player> playerList = new LinkedList<Player>();
		playerList.add(player);
		for (PlayerSimulation ps : playerMap.values()){
			otherPlayer.update(ps);
			playerList.add(otherPlayer);
		}

		GameResult gr = new GameResult(playerList);
		//System.out.println(gr.getScoredPlayerList().toString());
		//Gdx.app.debug("Player scores", gr.getScoredPlayerList().toString());
		return gr.getScoreScreen(batch);
	}
	
	public LinkedList<Player> getPlayerList(){
		LinkedList<Player> playerList = new LinkedList<Player>();
		playerList.add(player);
		for (PlayerSimulation ps : playerMap.values()){
			otherPlayer.update(ps);
			playerList.add(otherPlayer);
		}
		
		return playerList;
	}

	public PlayerSimulation getPlayerSimulation() {
		return playerSimulation;
	}

	public void setPlayerSimulation(PlayerSimulation playerSimulation) {
		this.playerSimulation = playerSimulation;
	}

	public HashMap<Integer, PlayerSimulation> getPlayerMap() {
		return playerMap;
	}

	public void setPlayerMap(HashMap<Integer, PlayerSimulation> playerMap) {
		this.playerMap = playerMap;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}