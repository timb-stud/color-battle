package de.htw.colorbattle.multiplayer;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.htw.colorbattle.ColorBattleGame;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.gameobjects.PlayerSimulation;
import de.htw.colorbattle.network.NetworkService;

public class MultigameLogic implements Observer{

	int gameTime;
	int playerCount;
	int joinedPlayers;
	BattleColorConfig bcConfig;
	NetworkService netSvc;
	boolean isGameStarted;
	boolean isServer;
	Player ownPlayer;
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
			this.ownPlayer = game.gameScreen.getPlayer();
			
			if (playerCount == 1){ //TODO only needed to test with one device. can be removed in final version
				Player playerBuffer = new Player(Color.MAGENTA, 64 / 2);
				playerBuffer.update(ownPlayer);
				game.gameScreen.getPlayerMap().put(1, playerBuffer);
			}
		} else {
			//TODO could throw exception ?
			Gdx.app.error("Multiplayer Game", "Can't create MultiGame, set PlayerCount to 1");
			this.playerCount = 1;
		}
		checkIfGameCanStart();
	}
	
	public void startServer(){
		ownPlayer.id = 1; //joinedPlayers;
		game.gameScreen.getPlayerMap().put(joinedPlayers, ownPlayer);
		Gdx.app.debug("Multiplayer Game", "multi game server is started. game time: " + gameTime + " player count: " + playerCount);
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
		playerSim.id = joinedPlayers;
		
		//TODO set start position of player
		Gdx.app.debug("Multiplayer Game", "player with id " + playerSim.id + " has joined the game.");
		switch (joinedPlayers) {
			case 2: //unten rechts
					// player.x = 
					// player.y =
				playerBuffer = new Player(Color.GREEN, 64 / 2); //TODO replace 64 with player width var
				break;
			case 3: //oben rechts
					// player.x = 
					// player.y =
				playerBuffer = new Player(Color.RED, 64 / 2); //TODO replace 64 with player width var
				break;
			case 4: //unten links
					// player.x = 
					// player.y =
				playerBuffer = new Player(Color.ORANGE, 64 / 2); //TODO replace 64 with player width var
				break;
			default:
				throw new RuntimeException("Undefined number of joined players");
		}
		playerBuffer.update(playerSim);
		playerMap.put(playerBuffer.id, playerBuffer);
		checkIfGameCanStart();
	}
	
	private void sendJoinMsg(final PlayerSimulation ownPlayerSim){
//		new Thread(new Runnable() {
//			public void run() {
//				ToggleTask toggleTask = new ToggleTask();
//				Timer timer = new Timer();
//				timer.schedule(toggleTask, 3000);
//				while(!isGameStarted){
					try{
//						if(!toggleTask.toggleState()){
//							toggleTask.setToggleState(true);
							netSvc.send(ownPlayerSim);
							Gdx.app.debug("Multiplayer Game", "sent join msg");
//						}
					} catch (NetworkException e) {
						Gdx.app.error("NetworkException", "Can't send join message.", e);
					}
//				}
//				timer.cancel();
//			} 
//		}).start(); 
	}
	
	private void sendGameStartMsg(){
		Gdx.app.debug("Multiplayer Game", "Game could be start, send message to other players.");
		HashMap<Integer, Player> playerMap = game.gameScreen.getPlayerMap();
		
		StartGameMsg start = new StartGameMsg(playerMap, gameTime);
		
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
			updatePlayerMap(startGameMsg.getPlayerMap());
			isGameStarted = true;
			Gdx.app.debug("Multiplayer Game", "Game started with " + startGameMsg.playerMap.size() + " otherPlayers in playerMap.");
		} else if (!isGameStarted && isServer && (obj instanceof PlayerSimulation)) {
			Gdx.app.debug("Multiplayer Game", "new player try to join game");
			PlayerSimulation playerSim = (PlayerSimulation) obj;
			addPlayerToGame(playerSim);
		} else if (isGameStarted && (obj instanceof PlayerSimulation)){
			PlayerSimulation playerSim = (PlayerSimulation) obj;
			game.gameScreen.getPlayerMap().get(playerSim.id).update(playerSim);
			
//			Gdx.app.debug("Multiplayer Game", "update player with id " + playerSim.id + " in playerMap.");
		}
	}
	
	private void updatePlayerMap(HashMap<Integer, Player> playerMap){
		int ownId = getAndSetOwnId(playerMap);
		
		Player player = playerMap.get(ownId);
		if (player == null)
			throw new RuntimeException("own player not in playerMap");
		game.gameScreen.setPlayer(player);
		
		playerMap.remove(ownId);
		Gdx.app.debug("Multiplayer Game", "removed own player with id " + ownId + " in playerMap.");
		game.gameScreen.setPlayerMap(playerMap);
	}
	
	private int getAndSetOwnId(HashMap<Integer, Player> playerMap){
		for (Player p : playerMap.values()){
			if (p.networkIdentifier.equals(ownPlayer.networkIdentifier)){
				game.gameScreen.setPlayer(p);
				PlayerSimulation playerSim = game.gameScreen.getPlayerSimulation();
				playerSim.id = p.id;
				game.gameScreen.setPlayerSimulation(playerSim);
				ownPlayer.id = p.id;
				Gdx.app.debug("Multiplayer Game", "found player id " + p.id + " in playerMap.");
				return p.id;
			}
		}
		throw new RuntimeException("own player could not found in playerMap");
	}

	public boolean isGameStarted() {
		return isGameStarted;
	}

}
