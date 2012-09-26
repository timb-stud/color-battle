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

public class GameScreen implements Screen {

	private ColorBattleGame game;
	// zeichnen und Screen
	private SpriteBatch batch;
	private Texture playerTexture;
	private FrameBuffer colorFrameBuffer;
	private GameBorder gameBorder;
	private int width;
	private int height;
	private Texture wallpaper;
	private OrthographicCamera ownCamera;

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

		// Spielfeld
		width = BattleColorConfig.WIDTH;
		height = BattleColorConfig.HEIGHT;
		batch = new SpriteBatch();
		colorFrameBuffer = new FrameBuffer(Format.RGBA8888, width, height,
				false);
		gameBorder = new GameBorder(width, height);
		countDown = new CountDown(Color.ORANGE, 480);

		// Player Allgemein
		flipper = new TextureRegion();
		playerTexture = new Texture(Gdx.files.internal("player.png"));
		wallpaper = new Texture(Gdx.files.internal("GameScreenWallpaper.png"));


		int playerWidth = playerTexture.getWidth();
		int playerHeight = playerTexture.getHeight();

		// spezielle Player
		player = new Player(Color.GREEN, playerWidth / 2);
		player.setColorInt(Color.GREEN);

		// set player default position
		player.x = width / 2 - playerWidth / 2;
		player.y = height / 2 - playerHeight / 2;

		playerSimulation = new PlayerSimulation(player);

		otherPlayer = new Player(Color.RED, playerWidth / 2);
		otherPlayer.setColorInt(Color.RED);
		playerMap = new HashMap<Integer, Player>();
		playerMap.put(2, otherPlayer);
		
		// Powerup
		powerUpTexture = new Texture(Gdx.files.internal("powerup.png"));
		powerUp = new PowerUp(0, 0, powerUpTexture.getWidth(),
				powerUpTexture.getHeight());

		game.toast.toaster();
	}

	@Override
	public void render(float delta) {
		// Screen und Kamera
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(ownCamera.combined);

//		// Server stuff
//		if (isServer) {
//			powerup();
//		}

		// Player zeichnen // TODO alle Schritte wirklich nötig ?
		flipper.setRegion(colorFrameBuffer.getColorBufferTexture());
		flipper.flip(false, true);
		colorFrameBuffer.begin();
		batch.begin();
		batch.draw(player.colorTexture, player.x, player.y);
		for (Player p : playerMap.values())
			batch.draw(p.colorTexture, p.x, p.y);
//		if (powerUp.isBombExploded) {
//			drawBomb();
//		}
		batch.end();
		colorFrameBuffer.end();

		batch.begin();
		batch.draw(wallpaper, 0, 0); // Hintergrund
		batch.draw(flipper, 0, 0);
		batch.draw(playerTexture, player.x, player.y);
		for (Player p : playerMap.values())
			batch.draw(playerTexture, p.x, p.y);
		if (powerUp.isVisible) {
			batch.draw(powerUpTexture, powerUp.rect.x, powerUp.rect.y);
		}
		batch.draw(countDown.countDownTexture, countDown.x, countDown.y); // Zeit
		batch.end();

		// Player movement
		Accelerometer.updateDirection(player.direction);
		if (powerUp.invertControl) {
			player.direction.mul(-1);
		}
		// checkDesktopControl(); // not supported atm
		player.move();
		gameBorder.handelCollision(player);
		playerSimulation.move();
		gameBorder.handelCollision(playerSimulation);
		for (Player p : playerMap.values()){
			p.move();
			gameBorder.handelCollision(p);
		}

		// NetworkCommunication
		if (playerSimulation.distance(player) > game.bcConfig.networkPxlUpdateIntervall) {
			playerSimulation.update(player);
			send(playerSimulation);
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
	}

	private void powerup() {
		powerUpTimer += Gdx.graphics.getDeltaTime();
		if (powerUpTimer > 5) {
			powerUpTimer = 0;
			powerUp.spawn();
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
				} else {
					send(new InvertControlMsg(true));
				}
			}
		}
	}

	private void drawBomb() {
		Color color = powerUp.wasPickedUpByServer ? otherPlayer.color
				: player.color;
		batch.draw(powerUp.getBombTexture(color), powerUp.rect.x
				- powerUp.rect.width, powerUp.rect.y - powerUp.rect.height);
		powerUp.isVisible = false;
		powerUp.isBombExploded = false;
	}

	public void swapPlayers() {
		Player buffer = player;
		player = otherPlayer;
		otherPlayer = buffer;
	}

	private void send(Object obj) {
		try {
			game.netSvc.send(obj);
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "Can't send position update.", e);
			e.printStackTrace(); // TODO Handle exception
		}
	}

	/**
	 * liest die Player aus der playerMap und erzeug das Spielergebnis für den
	 * aktuellen Screen
	 * 
	 * @return
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

	public void setOtherPlayers(HashMap<Integer, PlayerSimulation> initPlayerMap) {
		Iterator<PlayerSimulation> i = initPlayerMap.values().iterator();
		for(Player p : this.playerMap.values()){
			if (i.hasNext()){
				PlayerSimulation initP = i.next();
				p.update(initP);
				p.setNewColor(initP.colorInt); //not needed. set in update methode
//				p.color = Color.MAGENTA;
				p.repaintColorTexture();
			}
		}
//		this.otherPlayer.update(i.next()); // TODO only for playing with 2 players
	}

	public Player getPlayer() {
		return player;
	}

	public void updateOtherPlayer(PlayerSimulation ps) {
//		otherPlayer.update(ps);
		for(int key : this.playerMap.keySet()){
			Player p = this.playerMap.get(key);
			if(p.id == ps.id){
				p.update(ps);
				playerMap.put(key, p);
			}
		}
	}

	public void spawnPowerUp(PowerUpSpawnMsg powerUpSpawnMsg) {
		powerUp.set(powerUpSpawnMsg);
		powerUp.isVisible = true;
	}

	public void explodeBomb(BombExplodeMsg bombExplodeMsg) {
		powerUp.wasPickedUpByServer = bombExplodeMsg.wasPickedUpByServer;
		powerUp.isBombExploded = true;
	}

	public void invertControl(InvertControlMsg invertControlMsg) {
		powerUp.invertControl = invertControlMsg.invertControl;
		powerUp.isVisible = false;
	}

	public void setOwnPlayer(PlayerSimulation p) {
		this.player.update(p);
		this.player.setNewColor(p.colorInt);
//		this.player.color = Color.ORANGE;
		this.player.repaintColorTexture();
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
	 * Called when this screen becomes the current screen for a Game. also
	 * einmal beim Spielstart!
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
	 * wird zum Beispiel mit drücken des HomeButtons aufgerufen
	 */
	@Override
	public void pause() {
	}

	/**
	 * wird beim zurückkehren vom HomeScreen aufgerufen
	 */
	@Override
	public void resume() {
		//andy: ich hab auf den ersten Blick keine Ahnung warum die Texturen verloren gehen, bei den Menues passiert es nicht...
		//damits halbwegs was aussieht:
		player.repaintColorTexture();
		otherPlayer.repaintColorTexture();
	}

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