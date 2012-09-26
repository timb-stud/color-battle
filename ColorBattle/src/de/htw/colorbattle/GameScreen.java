package de.htw.colorbattle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.gameobjects.CountDown;
import de.htw.colorbattle.gameobjects.GameBorder;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.gameobjects.PlayerSimulation;
import de.htw.colorbattle.gameobjects.PowerUp;
import de.htw.colorbattle.input.Accelerometer;
import de.htw.colorbattle.menuscreens.GameEndMenu;
import de.htw.colorbattle.multiplayer.BombExplodeMsg;
import de.htw.colorbattle.multiplayer.InvertControlMsg;
import de.htw.colorbattle.multiplayer.PowerUpSpawnMsg;
import de.htw.colorbattle.toast.Toast;
import de.htw.colorbattle.toast.Toast.TEXT_POS;

public class GameScreen implements Screen {

	private ColorBattleGame game;
	// Textures and other stuff
	private SpriteBatch batch;
	private Texture playerTexture;
	private FrameBuffer colorFrameBuffer;
	private GameBorder gameBorder;
	private int width;
	private int height;
	private Texture wallpaper;
	private OrthographicCamera ownCamera;
	private boolean drawToastCountdown = false;

	// Players & Network
	private TextureRegion flipper;
	private Player player;
	private Player otherPlayer;
	private PlayerSimulation playerSimulation;
	private HashMap<Integer, Player> playerMap;

	// Powerup
	private PowerUp powerUp;
	private Texture powerUpTexture;
	private float powerUpTimer;
	
	
	//Sound
	private Sound bombSound;
	private Sound invertSound;
	private Sound powerUpSound;
	private boolean playInvertSound = false;

	// Server
	private boolean isServer;

	// Game End Elements
	private CountDown countDown;
	private long endTime;
	private boolean gameEnd = false;

	public GameScreen(ColorBattleGame game) throws NetworkException {
		this.game = game;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		this.ownCamera = new OrthographicCamera();
		this.ownCamera.setToOrtho(false, BattleColorConfig.WIDTH,
				BattleColorConfig.HEIGHT);

		// Playground
		width = BattleColorConfig.WIDTH;
		height = BattleColorConfig.HEIGHT;
		batch = new SpriteBatch();
		colorFrameBuffer = new FrameBuffer(Format.RGBA8888, width, height,
				false);
		gameBorder = new GameBorder(width, height);
		countDown = new CountDown(Color.ORANGE, 480);

		// Player & flipper
		flipper = new TextureRegion();
		playerTexture = new Texture(Gdx.files.internal("player.png"));
		wallpaper = new Texture(Gdx.files.internal("GameScreenWallpaper.png"));

		playerMap = new HashMap<Integer, Player>();

		int playerWidth = playerTexture.getWidth();
		int playerHeight = playerTexture.getHeight();

		// set player color
		player = new Player(Color.GREEN, playerWidth / 2);
		player.setColorInt(Color.GREEN);

		// set player default position
		player.x = width / 2 - playerWidth / 2;
		player.y = height / 2 - playerHeight / 2;

		playerSimulation = new PlayerSimulation(player);

		otherPlayer = new Player(Color.RED, playerWidth / 2);
		otherPlayer.setColorInt(Color.RED);

		// Powerup
		powerUpTexture = new Texture(Gdx.files.internal("powerup.png"));
		powerUp = new PowerUp(0, 0, powerUpTexture.getWidth(),
				powerUpTexture.getHeight());
		
		//Sound
		bombSound = Gdx.audio.newSound(Gdx.files.internal("sound/pop.mp3"));
		invertSound = Gdx.audio.newSound(Gdx.files.internal("sound/8-bit.mp3"));
		powerUpSound = Gdx.audio.newSound(Gdx.files.internal("sound/powerup.mp3"));

		game.toast.toaster();
	}

	@Override
	public void render(float delta) {
		// clear screen & set camera
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(ownCamera.combined);

		// Server stuff
		if (isServer) {
			powerup();
		}

		// Player draw the player color to the framebuffer// TODO is really everything necessary?
		flipper.setRegion(colorFrameBuffer.getColorBufferTexture());
		flipper.flip(false, true);
		colorFrameBuffer.begin();
		batch.begin();
		batch.draw(player.colorTexture, player.x, player.y);
		batch.draw(otherPlayer.colorTexture, otherPlayer.x, otherPlayer.y);
		if (powerUp.isBombExploded) {
			bombSound.play();
			drawBomb();
		}
		batch.end();
		colorFrameBuffer.end();

		batch.begin();
		batch.draw(wallpaper, 0, 0); // draw Wallpaper
		batch.draw(flipper, 0, 0);
		batch.draw(playerTexture, player.x, player.y);
		batch.draw(playerTexture, otherPlayer.x, otherPlayer.y);
		if (powerUp.isVisible) {
			batch.draw(powerUpTexture, powerUp.rect.x, powerUp.rect.y);
		}
		batch.draw(countDown.countDownTexture, countDown.x, countDown.y); // Zeit
		batch.end();

		// update Player movement from accelerometer
		Accelerometer.updateDirection(player.direction);
		if (powerUp.invertControl) {
			player.direction.mul(-1);
		}
		// checkDesktopControl(); // not supported atm
		player.move();
		gameBorder.handelCollision(player);
		playerSimulation.move();
		gameBorder.handelCollision(playerSimulation);
		otherPlayer.move();
		gameBorder.handelCollision(otherPlayer);

		// NetworkCommunication
		if (playerSimulation.distance(player) > game.bcConfig.networkPxlUpdateIntervall) {
			playerSimulation.update(player);
			send(playerSimulation);
		}
		
		//Sound
		if(playInvertSound){
			invertSound.play();
			playInvertSound = false;
		}

		// Game End
		if (!gameEnd) {
			gameEnd = countDown.activateCountDown(endTime,
					BattleColorConfig.GAME_TIME);
		} else {
			GameEndMenu gen = new GameEndMenu(game);
			gen.setGameresult(this.getGameResult());
			game.setScreen(gen);
			this.dispose();//neu könnte noch probleme verursachen
		}
		
		//toaster
		switch (countDown.getRemainingTimeInSeconds(BattleColorConfig.GAME_TIME)) {
			case 31: drawToastCountdown = true;
					break;
			case 30: 
				if (drawToastCountdown) {
					game.toast.makeText("30 Seconds left",
					"font", Toast.COLOR_PREF.BLUE, Toast.STYLE.NORMAL, TEXT_POS.middle, TEXT_POS.middle_down, Toast.LONG);
					drawToastCountdown = false;
				}
					break;
			case 11: drawToastCountdown = true;
					break;
			case 10:
				if (drawToastCountdown) {
					game.toast.makeText("10 Seconds left",
					"font", Toast.COLOR_PREF.BLUE, Toast.STYLE.NORMAL, TEXT_POS.middle, TEXT_POS.middle_down, Toast.MED);
					drawToastCountdown = false;
				}
					break;
			case 6: drawToastCountdown = true;
					break;
		}
		game.toast.toaster();
	}

	/**
	 * Server side management of the powerups 
	 */
	private void powerup() {
		powerUpTimer += Gdx.graphics.getDeltaTime();
		if (powerUpTimer > 5) {
			powerUpTimer = 0;
			powerUp.spawn();
			powerUpSound.play();
			send(new PowerUpSpawnMsg(powerUp));
		}
		boolean pickedByPlayer = powerUp.isPickedUpBy(player);
		boolean pickedByOtherPlayer = powerUp.isPickedUpBy(otherPlayer);
		if (powerUp.isVisible && (pickedByPlayer || pickedByOtherPlayer)) {
			send(new InvertControlMsg(false));
			powerUp.invertControl = false;
			powerUp.wasPickedUpByServer = pickedByOtherPlayer;
			if (powerUp.type == PowerUp.Type.BOMB) {
				send(new BombExplodeMsg(pickedByPlayer));
				powerUp.isBombExploded = true;
			} else {
				if (pickedByPlayer) {
					powerUp.isVisible = false;
					powerUp.invertControl = true;
					playInvertSound = true;
				} else {
					powerUp.isVisible = false;
					playInvertSound = true;
					send(new InvertControlMsg(true));
				}
			}
		}
	}

	/**
	 * Draws the bomb 
	 */
	private void drawBomb() {
		Color color = powerUp.wasPickedUpByServer ? otherPlayer.color
				: player.color;
		batch.draw(powerUp.getBombTexture(color), powerUp.rect.x
				- powerUp.rect.width, powerUp.rect.y - powerUp.rect.height);
		powerUp.isVisible = false;
		powerUp.isBombExploded = false;
	}

	/**
	 * The server swaps player and otherPlayer in order to have different color and startpositions.
	 */
	public void swapPlayers() {
		Player buffer = player;
		player = otherPlayer;
		otherPlayer = buffer;
	}
	
	/**
	 * Sends gameobjects to the other devices.
	 * @param obj
	 */
	private void send(Object obj) {
		try {
			game.netSvc.send(obj);
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "Can't send position update.", e);
			e.printStackTrace(); // TODO Handle exception
		}
	}

	/**
	 * 
	 * Reads all Players from the playerMap and creates the gameresult for the game result screen.
	 * 
	 * @return gameResult
	 */
	private GameResult getGameResult() {
		LinkedList<Player> playerList = new LinkedList<Player>();
		playerList.add(player);
		playerList.add(otherPlayer);
		// for (Player p : playerMap.values()) {
		// playerList.add(p);
		// }
		return new GameResult(playerList);
	}

	public PlayerSimulation getPlayerSimulation() {
		return playerSimulation;
	}

	public HashMap<Integer, Player> getPlayerMap() {
		return playerMap;
	}

	public void setPlayerMap(HashMap<Integer, Player> playerMap) {
		Iterator<Player> i = playerMap.values().iterator();
		this.otherPlayer.update(i.next()); // TODO only for playing with 2
											// players
		this.playerMap = playerMap;
		// for(Player p : playerMap.values()){
		// this.playerMap.get(p.id).update(p);
		// }
	}

	public Player getPlayer() {
		return player;
	}

	public void updateOtherPlayer(PlayerSimulation ps) {
		otherPlayer.update(ps);
	}

	/**
	 * Gets called if the server sends a msg that a new powerup has spawned
	 * @param powerUpSpawnMsg
	 */
	public void spawnPowerUp(PowerUpSpawnMsg powerUpSpawnMsg) {
		powerUpSound.play();
		powerUp.set(powerUpSpawnMsg);
		powerUp.isVisible = true;
	}
	/**
	 * Gets called if the server sends a message that a player has picked up the Bomb PowerUp
	 * @param bombExplodeMsg
	 */
	public void explodeBomb(BombExplodeMsg bombExplodeMsg) {
		powerUp.wasPickedUpByServer = bombExplodeMsg.wasPickedUpByServer;
		powerUp.isBombExploded = true;
	}
	
	/**
	 * Gets called if the server sends a message that a player has picked up the Invert PowerUp
	 * @param invertControlMsg
	 */
	public void invertControl(InvertControlMsg invertControlMsg) {
		powerUp.invertControl = invertControlMsg.invertControl;
		powerUp.isVisible = false;
		playInvertSound = true;
	}

	/**
	 * checks Input-Keys for Desktop Version
	 */
	@SuppressWarnings("unused")
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
	 * Called when this screen becomes visible. (Just once on gamestart)
	 * 
	 */
	@Override
	public void show() {
		Gdx.app.log("GameScreen", "show();");
		endTime = System.currentTimeMillis() / 1000
				+ BattleColorConfig.GAME_TIME;

		if (game.bcConfig.playSound) {
			game.playSound();
		}		
		// Server
		isServer = game.multiGame.isServer();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void hide() {
	}

	/**
	 * Gets called when the homebutton is pressed
	 */
	@Override
	public void pause() {
	}

	/**
	 * Gets called when returning from homescreen
	 */
	@Override
	public void resume() {
		//andy: ich hab auf den ersten Blick keine Ahnung warum die Texturen verloren gehen, bei den Menues passiert es nicht...
		//damits halbwegs was aussieht:
		player.repaintColorTexture();
		otherPlayer.repaintColorTexture();
	}

	/**
	 * Disposes all objects
	 */
	@Override
	public void dispose() {
		playerTexture.dispose();
		player.dispose();
		otherPlayer.dispose();
		colorFrameBuffer.dispose();
		batch.dispose();
		wallpaper.dispose();
		countDown.dispose();
		powerUpTexture.dispose();
		playerSimulation = null;
		playerMap = null;
		flipper = null;
		gameBorder = null;
		ownCamera = null;
	}
	
	/**
	 * Disposes all objects
	 */
	public void disposeFromGameScreen() {
		playerTexture.dispose();
		player.dispose();
		otherPlayer.dispose();
		colorFrameBuffer.dispose();
		countDown.dispose();
		powerUpTexture.dispose();
		playerSimulation = null;
		playerMap = null;
		flipper = null;
		gameBorder = null;
		ownCamera = null;
	}
}