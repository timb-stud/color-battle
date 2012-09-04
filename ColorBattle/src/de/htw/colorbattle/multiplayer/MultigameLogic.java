package de.htw.colorbattle.multiplayer;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;

import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.gameobjects.PlayerSimulation;
import de.htw.colorbattle.network.NetworkService;
import de.htw.colorbattle.utils.ToggleTask;

public class MultigameLogic implements Observer{

	int gameTime;
	int playerCount;
	int joinedPlayers;
	BattleColorConfig bcConfig;
	NetworkService netSvc;
	boolean isGameStarted;
	boolean isServer;
	PlayerSimulation ownPlayerSim;
	ColorBattleGame game;
	
	public MultigameLogic(ColorBattleGame game,boolean isServer) throws NetworkException{
		this.game = game;
		this.bcConfig = game.bcConfig;
		if (bcConfig.isWifiConnected) {
			this.netSvc = NetworkService.getInstance(bcConfig.multicastAddress, bcConfig.multicastPort);
			this.netSvc.addObserver(this);
			
			this.isServer = isServer;
			this.isGameStarted = false;
			this.gameTime = bcConfig.gameTime;
			this.playerCount = bcConfig.multigamePlayerCount;
			this.joinedPlayers = 1; //1 for own Player
			this.ownPlayerSim = game.gameScreen.getPlayerSimulation();
			
			if (playerCount == 1) //TODO only needed to test with one device. can be removed in final version
				game.gameScreen.getPlayerMap().put(1, ownPlayerSim);
		} else {
			//TODO could throw exception ?
			Gdx.app.error("Multiplayer Game", "Can't create MultiGame, set PlayerCount to 1");
			this.playerCount = 1;
		}
		checkIfGameCanStart();
	}
	
	public void startServer(){
		ownPlayerSim.id = 1; //joinedPlayers;
		game.gameScreen.getPlayerMap().put(joinedPlayers, ownPlayerSim);
		Gdx.app.debug("Multiplayer Game", "multi game server is started. game time: " + gameTime + " player count: " + playerCount);
	}
	
	public void joinGame(){
		sendJoinMsg(ownPlayerSim);	
	}
	
	private void checkIfGameCanStart(){
		if(playerCount == joinedPlayers)
			sendGameStartMsg();
	}
	
	private void addPlayerToGame(PlayerSimulation playerSim){
		//TODO set start position of player
		Gdx.app.debug("Multiplayer Game", "player with id " + playerSim.id + " has joined the game.");
		//TODO set player color
		switch (joinedPlayers) {
			case 2: //unten rechts
					// player.x = 
					// player.y = 
				break;
			case 3: //oben rechts
					// player.x = 
					// player.y = 
				break;
			case 4: //unten links
					// player.x = 
					// player.y = 
				break;
		}
		game.gameScreen.getPlayerMap().put(playerSim.id, playerSim);
	}
	
	private void sendJoinMsg(final PlayerSimulation ownPlayerSim){
		//TODO add Timer to send messages in intervall
		ToggleTask toggleTask = new ToggleTask();
		Timer timer = new Timer();
		timer.schedule(toggleTask, 3000);
//		while(!isGameStarted){
			try{
//				if(!toggleTask.toggleState()){
					netSvc.send(ownPlayerSim);
//				}
				Gdx.app.debug("Multiplayer Game", "sent join msg");
			} catch (NetworkException e) {
				Gdx.app.error("NetworkException", "Can't send join message.", e);
			}
//		}
		timer.cancel();
	}
	
	private void sendGameStartMsg(){
		Gdx.app.debug("Multiplayer Game", "Game could be start, send message to other players.");
		HashMap<Integer, PlayerSimulation> playerMap 
											= game.gameScreen.getPlayerMap();
		StartGameMsg start = new StartGameMsg();
		start.gameTime = gameTime;
		start.playerMap = playerMap;
		
		updatePlayerMap(playerMap);
		
		try
		{
			netSvc.send(start);
			isGameStarted = true;
		} catch (NetworkException e) {
			Gdx.app.error("NetworkException", "Can't send start message.", e);
		}
	}
	
	@Override
	public void update(Observable obs, Object obj) {
		if(obj instanceof StartGameMsg) {
			StartGameMsg startGameMsg = (StartGameMsg) obj;
			
			updatePlayerMap(startGameMsg.playerMap);
			
			isGameStarted = true;
			Gdx.app.debug("Multiplayer Game", "game is started");
		} else if (!isGameStarted && isServer && (obj instanceof PlayerSimulation)) {
			Gdx.app.debug("Multiplayer Game", "new player try to join game");
			PlayerSimulation playerSim = (PlayerSimulation) obj;
			joinedPlayers++;
			playerSim.id = joinedPlayers;
			addPlayerToGame(playerSim);
			checkIfGameCanStart();
		} else if (isGameStarted && (obj instanceof PlayerSimulation)){
			PlayerSimulation playerSim = (PlayerSimulation) obj;
			game.gameScreen.getPlayerMap().put(playerSim.id, playerSim);
			Gdx.app.debug("Multiplayer Game", "update player with id " + playerSim.id + " in playerMap.");
		}
	}
	
	private void updatePlayerMap(HashMap<Integer, PlayerSimulation> playerMap){
		int ownId = getAndSetOwnId(playerMap);
		
		Player player = game.gameScreen.getPlayer();
		PlayerSimulation playerSim = playerMap.get(ownId);
		if (playerSim == null)
			throw new RuntimeException("own player not in playerMap");
		player.update(playerSim);
		
		game.gameScreen.getPlayerMap().remove(ownId);
		game.gameScreen.setPlayerMap(playerMap);
	}
	
	private int getAndSetOwnId(HashMap<Integer, PlayerSimulation> playerMap){
		int id;
		for (PlayerSimulation ps : playerMap.values()){
			if (ps.networkIdentifier.equals(ownPlayerSim.networkIdentifier)){
				id = ps.id;
				Player player = game.gameScreen.getPlayer();
				player.id = id;
				game.gameScreen.setPlayer(player);
				PlayerSimulation playerSim = game.gameScreen.getPlayerSimulation();
				playerSim.id = id;
				game.gameScreen.setPlayerSimulation(playerSim);
				Gdx.app.debug("Multiplayer Game", "found player id " + id + " in playerMap.");
				return id;
			}
		}
		throw new RuntimeException("own player could not found in playerMap");
	}

	public boolean isGameStarted() {
		return isGameStarted;
	}

}
