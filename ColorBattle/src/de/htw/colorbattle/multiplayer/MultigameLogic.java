package de.htw.colorbattle.multiplayer;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Logger;

import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.config.GameMode;
import de.htw.colorbattle.config.RuntimeConfig;
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.gameobjects.PlayerSimulation;
import de.htw.colorbattle.network.NetworkService;
import de.htw.colorbattle.toast.Toast;

public class MultigameLogic implements Observer{

	int gameTime;
	int playerCount;
	int joinedPlayers;
	RuntimeConfig bcConfig;
	boolean isGameStarted;
	boolean isServer = false;
	Player ownPlayer;
	ColorBattleGame game;
	
	public MultigameLogic(ColorBattleGame game, final int playerCount) {
		this(game);
		this.playerCount = playerCount;
		this.isServer = true;
		this.joinedPlayers = 1; //1 for own Player

//		game.gameScreen.swapPlayers();
		ownPlayer.id = joinedPlayers;
		ownPlayer.x = 50;
		ownPlayer.y = 50;
		game.gameScreen.getPlayerMap().put(joinedPlayers, ownPlayer);
		Gdx.app.debug("Multiplayer Game", "player with id " + ownPlayer.id + "has started multiGame server. game time: " + gameTime + " player count: " + playerCount);
		
		game.toast.makeText(" player game has started. GameTime: " + gameTime + " PlayerCount: " + playerCount , "font", 
				Toast.COLOR_PREF.BLUE, Toast.STYLE.ROUND, Toast.TEXT_POS.middle, Toast.TEXT_POS.middle_down, Toast.LONG);
		
		if (bcConfig.gameMode == GameMode.SINGLEPLAYER){ //TODO only needed to test with one device. can be removed in final version
			this.playerCount = 1;
		}
		checkIfGameCanStart();
	}

	public MultigameLogic(ColorBattleGame game) {
		
		this.game = game;
		this.bcConfig = game.bcConfig;
		
		this.isGameStarted = false;
		this.gameTime = BattleColorConfig.GAME_TIME;
		this.ownPlayer = game.gameScreen.getPlayer();
		
		if (game.netSvc instanceof NetworkService)
			game.netSvc.addObserver(this);
	}
	
	public void joinGame(){
		PlayerSimulation playerSimulation = new PlayerSimulation(ownPlayer);
		sendJoinMsg(playerSimulation);	
	}
	
	private void checkIfGameCanStart(){
		if(playerCount == joinedPlayers)
			sendGameStartMsg();
	}
	
	private void addPlayerToGame(PlayerSimulation playerSim){
		Player playerBuffer;
		HashMap<Integer, Player> playerMap = game.gameScreen.getPlayerMap();
		if(playerMap.containsValue(playerSim))
			return;
		
		joinedPlayers++;
		//TODO set start position of playerSim. HINT: playerSim will update playerBuffer
		Gdx.app.debug("Multiplayer Game", "player with id " + joinedPlayers + " has joined the game.");
		switch (joinedPlayers) {
			case 2: //unten rechts
				playerSim.x = 600;
				playerSim.y = 200;
				playerBuffer = new Player(Color.RED, 64 / 2); //‚TODO replace 64 with player width var
				playerSim.setColorInt(Color.RED);
				break;
			case 3: //oben rechts
				playerSim.x = 600;
				playerSim.y = 200;
				playerBuffer = new Player(Color.BLUE, 64 / 2); //TODO replace 64 with player width var
				playerSim.setColorInt(Color.BLUE);
				break;
			case 4: //unten links
				playerSim.x = 600;
				playerSim.y = 200;
				playerBuffer = new Player(Color.PINK, 64 / 2); //TODO replace 64 with player width var
				playerSim.setColorInt(Color.PINK);
				break;
			default:
				throw new RuntimeException("Undefined number of joined players");
		}
		playerBuffer.update(playerSim);
		playerBuffer.id = joinedPlayers;
		playerMap.put(playerBuffer.id, playerBuffer);
		Gdx.app.debug("Multiplayer Game", "add player with id " + playerBuffer.id + " to playerMap.");
		checkIfGameCanStart();
	}
	
	private void sendJoinMsg(final PlayerSimulation ownPlayerSim){
		try{
			game.netSvc.send(ownPlayerSim);
			Gdx.app.debug("Multiplayer Game", "sent join msg");
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "Can't send join message.", e);
		} 
	}
	
	private void sendGameStartMsg(){
		Gdx.app.debug("Multiplayer Game", "Game could be start, send message to other players.");
		HashMap<Integer, Player> playerMap = game.gameScreen.getPlayerMap();
		
		StartGameMsg start = new StartGameMsg(playerMap, gameTime);
		
		updatePlayerMap(playerMap);
		
		try
		{
			game.netSvc.send(start);
			isGameStarted = true;
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "Can't send start message.", e);
		}
	}
	
	@Override
	public void update(Observable obs, Object obj) {
		if(obj instanceof StartGameMsg) {
			StartGameMsg startGameMsg = (StartGameMsg) obj;
			updatePlayerMap(startGameMsg.getPlayerMap());
			
			isGameStarted = true;
			Gdx.app.debug("Multiplayer Game", "Game started with " + startGameMsg.playerMap.size() + " otherPlayers in playerMap.");
		} else if (!isGameStarted && isServer && (obj instanceof PlayerSimulation)) {
			Gdx.app.debug("Multiplayer Game", "new player try to join game");
			PlayerSimulation playerSim = (PlayerSimulation) obj;
			addPlayerToGame(playerSim);
		} else if (isGameStarted && (obj instanceof PlayerSimulation)){
			PlayerSimulation playerSim = (PlayerSimulation) obj;
//			game.gameScreen.getPlayerMap().get(playerSim.id).update(playerSim);
			game.gameScreen.updateOtherPlayer(playerSim); //only for two player mode
			
//			Gdx.app.debug("Multiplayer Game", "update player with id " + playerSim.id + " in playerMap.");
		} else if (obj instanceof PowerUpSpawnMsg){
			PowerUpSpawnMsg powerUpSpawnMsg = (PowerUpSpawnMsg)obj;
			game.gameScreen.spawnPowerUp(powerUpSpawnMsg);
		} else if (obj instanceof BombExplodeMsg) {
			BombExplodeMsg bombExplodeMsg = (BombExplodeMsg)obj;
			game.gameScreen.explodeBomb(bombExplodeMsg);
		} else if(obj instanceof InvertControlMsg) {
			InvertControlMsg invertControlMsg = (InvertControlMsg)obj;
			game.gameScreen.invertControl(invertControlMsg);
		}
	}
	
	private void updatePlayerMap(HashMap<Integer, Player> playerMap){
		Player player = getOwnPlayer(playerMap);
		game.gameScreen.getPlayer().update(player);
		game.gameScreen.getPlayerSimulation().update(player);
		ownPlayer.id = player.id;
		Gdx.app.debug("Multiplayer Game", "found player id " + player.id + " in playerMap.");
		playerMap.remove(player.id);
		Gdx.app.debug("Multiplayer Game", "removed own player with id " + player.id + " in playerMap.");
//		game.gameScreen.setOtherPlayers(playerMap);
		game.gameScreen.setPlayerMap(playerMap);
	}
	
	private Player getOwnPlayer(HashMap<Integer, Player> playerMap){
		for (Player p : playerMap.values()){
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
}
