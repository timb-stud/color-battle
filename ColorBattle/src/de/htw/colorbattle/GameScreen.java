package de.htw.colorbattle;

import java.util.HashMap;
import java.util.Iterator;
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

import de.htw.colorbattle.config.GameMode;
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
	private HashMap<Integer, Player> playerMap;
	private GameBorder gameBorder;
	private int width;
	private int height;
	private NetworkService netSvc;
	private Texture wallpaper;
	private CountDown countDown;
	private long endTime;
	private boolean gameEnd = false;
	private boolean scoreComputed = false;
	public Texture endTexture;
	private LinkedList<Player> playerList = new LinkedList<Player>();

	public GameScreen(ColorBattleGame game) throws NetworkException {
		this.game = game;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// Spielfeld
		width = (int) this.game.camera.viewportWidth;
		height = (int) this.game.camera.viewportHeight;
		batch = new SpriteBatch();
		colorFrameBuffer = new FrameBuffer(Format.RGBA8888, width, height,
				false);
		gameBorder = new GameBorder(width, height);
		countDown = new CountDown(Color.ORANGE, 480);

		// Player Allgemein
		flipper = new TextureRegion();
		playerTexture = new Texture(Gdx.files.internal("player.png"));
		int playerWidth = playerTexture.getWidth();
		int playerHeight = playerTexture.getHeight();
		playerMap = new HashMap<Integer, Player>();

		// spezielle Player
		player = new Player(Color.GREEN, playerWidth / 2);
		player.setColorInt(Color.GREEN);
		player.x = width / 2 - playerWidth / 2;
		player.y = height / 2 - playerHeight / 2;

		playerSimulation = new PlayerSimulation(player);

		otherPlayer = new Player(Color.RED, playerWidth / 2);
		otherPlayer.setColorInt(Color.RED);

		if (game.bcConfig.gameMode == GameMode.WIFI
				&& game.bcConfig.isWifiConnected) {
			this.netSvc = NetworkService
					.getInstance(game.bcConfig.multicastAddress,
							game.bcConfig.multicastPort);
		}

	}

	public void swapPlayers() {
		Player buffer = player;
		player = otherPlayer;
		otherPlayer = buffer;
	}

	@Override
	public void render(float delta) {
		// Screen und Kamera
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.camera.update();
		batch.setProjectionMatrix(game.camera.combined);

		// Player zeichnen // TODO alle Schritte wirklich nötig ?
		flipper.setRegion(colorFrameBuffer.getColorBufferTexture());
		flipper.flip(false, true);
		colorFrameBuffer.begin();
		batch.begin();
		batch.draw(player.colorTexture, player.x, player.y);
		batch.draw(otherPlayer.colorTexture, otherPlayer.x, otherPlayer.y);
		batch.end();
		colorFrameBuffer.end();
		// TODO unterschied colorframebuffer und normaler batch ?
		batch.begin();
		batch.draw(wallpaper, 0, 0); // Hintergrund
		batch.draw(flipper, 0, 0);
		batch.draw(playerTexture, player.x, player.y);
		batch.draw(playerTexture, otherPlayer.x, otherPlayer.y);
		batch.draw(countDown.countDownTexture, countDown.x, countDown.y); // Zeit
		batch.end();

		// Player movement // TODO 3 Bewegungen ?
		Accelerometer.updateDirection(player.direction);
		// checkDesktopControl(); // not supported atm
		player.move();
		gameBorder.handelCollision(player);
		playerSimulation.move();
		gameBorder.handelCollision(playerSimulation);
		otherPlayer.move();
		gameBorder.handelCollision(otherPlayer);

		// NetworkCommunication
		if (netSvc != null || game.bcConfig.gameMode == GameMode.BLUETOOTH) {
			if (playerSimulation.distance(player) > game.bcConfig.networkPxlUpdateIntervall) {
				playerSimulation.update(player);
				sendPosition();
			}
		}

		// Game End
		if (!gameEnd) {
			gameEnd = countDown.activateCountDown(endTime,
					game.bcConfig.gameTime);
		} else {
			enterPlayerList();
			game.gameEndScreen.setGameresult(getGameResult());
			game.setScreen(game.gameEndScreen);
			// TODO ab hier kann man alles disposen
		}
	}

	private void sendPosition() {
		try {
			if (game.bcConfig.gameMode == GameMode.WIFI)
				netSvc.send(playerSimulation);
			else
				game.bluetoothActionResolver.send(playerSimulation);
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "Can't send position update.", e);
			e.printStackTrace(); // TODO Handle exception
		}
	}

	private GameResult getGameResult() {
		LinkedList<Player> playerList = new LinkedList<Player>();
		playerList.add(player);
		for (Player p : playerMap.values()) {
			playerList.add(p);
		}

		return new GameResult(playerList);
	}

	private void enterPlayerList() {
		this.playerList.add(player);
		for (Player p : playerMap.values()) {
			this.playerList.add(p);
		}
	}

	public LinkedList<Player> getPlayerList() {
		return playerList;
	}

	public PlayerSimulation getPlayerSimulation() {
		return playerSimulation;
	}

	public void setPlayerSimulation(PlayerSimulation playerSimulation) {
		this.playerSimulation = playerSimulation;
	}

	public HashMap<Integer, Player> getPlayerMap() {
		return playerMap;
	}

	public void setPlayerMap(HashMap<Integer, Player> playerMap) {
		Iterator<Player> i = playerMap.values().iterator();
		this.otherPlayer.update(i.next()); // TODO only for playing with 2
											// players
		this.playerMap = playerMap;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getOtherPlayer() {
		return otherPlayer;
	}

	public void updateOtherPlayer(PlayerSimulation ps) {
		otherPlayer.update(ps);
	}

	/**
	 * checks Input-Keys for Desktop Version
	 */
	private void checkDesktopControl() {
		if (Gdx.input.isKeyPressed(Keys.UP))
			player.y += player.speed * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.DOWN))
			player.y -= player.speed * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			player.x -= player.speed * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			player.x += player.speed * Gdx.graphics.getDeltaTime();
	}

	// ---------------------- down libgdx Elements ----------------------

	/**
	 * Called when this screen becomes the current screen for a Game. also
	 * einmal beim Spielstart!
	 */
	@Override
	public void show() {
		Gdx.app.log("GameScreen", "show();");
		endTime = System.currentTimeMillis() / 1000 + game.bcConfig.gameTime;

		if (game.bcConfig.playSound) {
			game.playSound();
		}
		// called when this screen is set as the screen with game.setScreen();
		wallpaper = new Texture(Gdx.files.internal("GameScreenWallpaper.png"));
		// TODO vllt kann man den Wallpaper direkt zeichnen auf das Element das
		// nicht gelöscht wird , und nicht extra nochmal im render
	}

	@Override
	public void resize(int width, int height) {
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
		otherPlayer.dispose();
		colorFrameBuffer.dispose();
		batch.dispose();
	}
}