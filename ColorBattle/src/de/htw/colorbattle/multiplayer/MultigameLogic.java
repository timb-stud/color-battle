package de.htw.colorbattle.multiplayer;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Gdx;

import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.gameobjects.PlayerSimulation;
import de.htw.colorbattle.network.NetworkService;

public class MultigameLogic implements Observer{

	int gameTime;
	int playerCount;
	int joinedPlayers;
	BattleColorConfig bcConfig;
	NetworkService netSvc;
	ArrayList<PlayerSimulation> playerList;
	boolean isGameStarted;
	boolean isServer;
	PlayerSimulation ownPlayer;
	
	public MultigameLogic(BattleColorConfig bcConfig,boolean isServer, PlayerSimulation ownPlayer) throws NetworkException{
		if (bcConfig.isWifiConnected) {
			this.netSvc = NetworkService.getInstance(bcConfig.multicastAddress, bcConfig.multicastPort);
			this.netSvc.addObserver(this);
			
			this.isServer = isServer;
			this.isGameStarted = false;
			this.gameTime = bcConfig.gameTime;
			this.playerCount = bcConfig.multigamePlayerCount;
			this.joinedPlayers = 1; //1 for own Player
			this.isServer = true;
			this.ownPlayer = ownPlayer;

		} else {
			//TODO chould throw exception ?
			Gdx.app.error("Multiplayer Game", "Can't create MultiGame, set PlayerCount to 1");
			this.playerCount = 1;
		}
		checkIfGameCanStart();
	}
	
	public void startServer(){
		playerList = new ArrayList<PlayerSimulation>();
		playerList.add(ownPlayer);
		Gdx.app.debug("Multiplayer Game", "multi game server is started. game time: " + gameTime + " player count: " + playerCount);
	}
	
	public void joinGame(){
		sendJoinMsg(ownPlayer);	
	}
	
	private void checkIfGameCanStart(){
		if(playerCount == joinedPlayers)
			sendGameStartMsg();
	}
	
	private void addPlayerToGame(PlayerSimulation player){
		//TODO set start position of player
		playerList.add(player);
	}
	
	private void sendJoinMsg(PlayerSimulation ownPlayer){
		//TODO add Timer to send messages in intervall
		if(!isGameStarted){
			try{
				netSvc.send(ownPlayer);
				Gdx.app.debug("Multiplayer Game", "sent join msg");
			} catch (NetworkException e) {
				Gdx.app.error("NetworkException", "Can't send join message.", e);
			}
		}
	}
	
	private void sendGameStartMsg(){
		StartGameMsg start = new StartGameMsg();
		start.gameTime = gameTime;
		//TODO add playerList;
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
			isGameStarted = true;
			Gdx.app.debug("Multiplayer Game", "game is started");
		} else if (!isGameStarted && isServer && (obj instanceof PlayerSimulation)) {
			Gdx.app.debug("Multiplayer Game", "new player try to join game");
			PlayerSimulation player = (PlayerSimulation) obj;
			addPlayerToGame(player);
			joinedPlayers++;
			checkIfGameCanStart();
		}
	}

	public boolean isGameStarted() {
		return isGameStarted;
	}

}
