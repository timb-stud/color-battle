
package de.htw.colorbattle.multiplayer;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.config.GameMode;
import de.htw.colorbattle.config.RuntimeConfig;
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.gameobjects.PlayerSimulation;
import de.htw.colorbattle.network.NetworkService;
import de.htw.colorbattle.toast.Toast;
import de.htw.colorbattle.toast.Toast.TEXT_POS;

/**
 * Pass incoming network data to classes which handle the data.
 * Also manage the process by start a new multi game and add joined player to game
 */
public class MultigameLogic implements Observer{

	int gameTime;
	int playerCount;
	int joinedPlayers;
	RuntimeConfig bcConfig;
	boolean isGameStarted;
	boolean isServer = false;
	PlayerSimulation ownPlayer;
	ColorBattleGame game;
	HashMap<Integer, PlayerSimulation> logicPlayerMap;
	
	/**
	 * Consturctor - start new server - called (from server) if opened a new game
	 * @param game reference to ColorBattleGame class
	 * @param playerCount	contains the count of players you want to play with
	 */
	public MultigameLogic(ColorBattleGame game, final int playerCount) {
		this(game);
		this.playerCount = playerCount;
		this.isServer = true;
		this.joinedPlayers = 1; //1 for own Player
		
		ownPlayer.id = joinedPlayers;
		ownPlayer.x = 50;
		ownPlayer.y = 430;
		this.logicPlayerMap = new HashMap<Integer, PlayerSimulation>();
		this.logicPlayerMap.put(joinedPlayers, ownPlayer);
		Gdx.app.debug("Multiplayer Game", "player with id " + ownPlayer.id + "has started multiGame server. game time: " + gameTime + " player count: " + playerCount);
		
		game.toast.makeText(" player game has started. GameTime: " + gameTime + " PlayerCount: " + playerCount , "font", 
				Toast.COLOR_PREF.BLUE, Toast.STYLE.ROUND, TEXT_POS.middle, TEXT_POS.middle_down, Toast.MED);
		
		if (bcConfig.gameMode == GameMode.SINGLEPLAYER){ //TODO only needed to test with one device. can be removed in final version
			this.playerCount = 1;
		}
		checkIfGameCanStart();
	}

	/**
	 * Constructor - Set attributes witch used by client and server
	 */
	public MultigameLogic(ColorBattleGame game) {
		
		this.game = game;
		this.bcConfig = game.bcConfig;
		
		this.isGameStarted = false;
		this.gameTime = game.bcConfig.gameTime;
		this.ownPlayer = game.gameScreen.getPlayerSimulation();
		
		if (game.netSvc instanceof NetworkService)
			game.netSvc.addObserver(this);
	}
	
	/**
	 * called by client to join to an open game
	 */
	public void joinGame(){
		sendJoinMsg(ownPlayer);	
	}
	
	/**
	 * called every time if a player has joined the game.
	 * Check if game can be start
	 */
	private void checkIfGameCanStart(){
		if(playerCount == joinedPlayers)
			sendGameStartMsg();
	}
	
	/**
	 * Add new player to game. Also set start position and player id to new player
	 * @param playerSim	received player object
	 */
	private void addPlayerToGame(PlayerSimulation playerSim){
		if(logicPlayerMap.containsValue(playerSim))
			return;
		
		joinedPlayers++;
		Gdx.app.debug("Multiplayer Game", "player with id " + joinedPlayers + " has joined the game.");
		switch (joinedPlayers) {
			case 2: //unten rechts
				playerSim.x = 730;
				playerSim.y = 50;
				playerSim.setColorInt(Color.RED);
				break;
			case 3: //oben rechts
				playerSim.x = 730;
				playerSim.y = 430;
				playerSim.setColorInt(Color.BLUE);
				break;
			case 4: //unten links
				playerSim.x = 50;
				playerSim.y = 50;
				playerSim.setColorInt(Color.YELLOW);
				break;
			default:
				throw new RuntimeException("Undefined number of joined players");
		}
		playerSim.id = joinedPlayers;
		logicPlayerMap.put(playerSim.id, playerSim);
		Gdx.app.debug("Multiplayer Game", "add player with id " + playerSim.id + " to playerMap.");
		checkIfGameCanStart();
	}
	
	/**
	 * Called to send own player simulation over network
	 * @param ownPlayerSim
	 */
	private void sendJoinMsg(final PlayerSimulation ownPlayerSim){
		try{
			game.netSvc.send(ownPlayerSim);
			Gdx.app.debug("Multiplayer Game", "sent join msg");
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "Can't send join message.", e);
		} 
	}
	
	/**
	 *  Send message to all players that the game can be start.
	 *  After sending map, own player map updates with game onformations
	 */
	private void sendGameStartMsg(){
		Gdx.app.debug("Multiplayer Game", "Game could be start, send message to other players.");
		StartGameMsg start = new StartGameMsg(logicPlayerMap, gameTime);
		try
		{
			game.netSvc.send(start);
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "Can't send start message.", e);
		}
		updatePlayerMap(logicPlayerMap);
		isGameStarted = true;
	}

	/**
	 * Observer pattern - called from network services if a new object receives the device
	 */
	@Override
	public void update(Observable obs, Object obj) {
		// needed if game is started. Set new player informations to game
		if (isGameStarted && (obj instanceof PlayerSimulation)){
			PlayerSimulation playerSim = (PlayerSimulation) obj;
	//		game.gameScreen.getPlayerMap().get(playerSim.id).update(playerSim);
			game.gameScreen.updateOtherPlayer(playerSim); //only for two player mode
	//		Gdx.app.debug("Multiplayer Game", "update player with id " + playerSim.id + " in playerMap.");
			
		// handled by clients just before the game starts. 
		// StartGameMsg contains player infomations witch set by server
		} else if(obj instanceof StartGameMsg) { 
			StartGameMsg startGameMsg = (StartGameMsg) obj;
			game.bcConfig.gameTime = startGameMsg.gameTime;
			this.playerCount = startGameMsg.playerCount;
			updatePlayerMap(startGameMsg.playerMap);
			
//			game.toast.makeText("You are Player " + ownPlayer.id, "font", 
//					Toast.COLOR_PREF.BLUE, Toast.STYLE.NORMAL, TEXT_POS.middle, TEXT_POS.middle_down, Toast.LONG);
			
			isGameStarted = true; //now game will be start
			Gdx.app.debug("Multiplayer Game", "Game started with " + startGameMsg.playerMap.size() + " otherPlayers in playerMap.");
			
		// handled by server if a new player like to join the game
		} else if (!isGameStarted && (obj instanceof PlayerSimulation)) {
//			game.toast.makeText("New player joined", "font", 
//					Toast.COLOR_PREF.BLUE, Toast.STYLE.NORMAL, TEXT_POS.middle, TEXT_POS.middle_down, Toast.MED);
			if (isServer){
				Gdx.app.debug("Multiplayer Game", "new player try to join game");
				PlayerSimulation playerSim = (PlayerSimulation) obj;
				addPlayerToGame(playerSim);
			}
			
		//PowerUps
		} else if (obj instanceof PowerUpSpawnMsg){
			PowerUpSpawnMsg powerUpSpawnMsg = (PowerUpSpawnMsg)obj;
			game.gameScreen.spawnPowerUp(powerUpSpawnMsg);
		} else if (obj instanceof BombExplodeMsg) {
			BombExplodeMsg bombExplodeMsg = (BombExplodeMsg)obj;
			game.gameScreen.explodeBomb(bombExplodeMsg);
		} else if(obj instanceof InvertControlMsg) {
			InvertControlMsg invertControlMsg = (InvertControlMsg)obj;
			game.gameScreen.invertControl(invertControlMsg);
		}else if(obj instanceof SpeedUpControlMsg) {
			SpeedUpControlMsg speedUpControlMsg = (SpeedUpControlMsg)obj;
			game.gameScreen.speedUpControl(speedUpControlMsg);
		}
	}
	
	/**
	 * Removes own player from player map and set own player and playermap to game screen
	 * @param playerMap
	 */
	private void updatePlayerMap(HashMap<Integer, PlayerSimulation> playerMap){
		PlayerSimulation player = getOwnPlayer(playerMap);
		game.gameScreen.setOwnPlayer(player);
		game.gameScreen.getPlayerSimulation().update(player);
//		ownPlayer.id = player.id;
		Gdx.app.debug("Multiplayer Game", "found player id " + player.id + " in playerMap.");
		playerMap.remove(player.id);
		Gdx.app.debug("Multiplayer Game", "removed own player with id " + player.id + " in playerMap.");
		game.gameScreen.setOtherPlayers(playerMap);
//		game.gameScreen.setPlayerMap(playerMap);
	}
	
	/**
	 * Searches for own player in playermap. 
	 * Own player could be found by unique device id.
	 * @return found player
	 */
	private PlayerSimulation getOwnPlayer(HashMap<Integer, PlayerSimulation> playerMap){
		for (PlayerSimulation p : playerMap.values()){
			if (p.deviceId.equals(ownPlayer.deviceId)){
				return p;
			}
		}
		throw new RuntimeException("own player could not found in playerMap");
	}

	public boolean isGameStarted() {
		return isGameStarted;
	}
	
	public boolean isServer() {
		return isServer;
	}

	public void setGameStarted(boolean isGameStarted) {
		this.isGameStarted = isGameStarted;
	}
	
	public int getPlayerCount() {
		return playerCount;
	}
}
