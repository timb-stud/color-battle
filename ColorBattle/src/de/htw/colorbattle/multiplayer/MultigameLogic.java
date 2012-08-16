package de.htw.colorbattle.multiplayer;

import java.util.Observable;
import java.util.Observer;

import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.network.NetworkService;

public class MultigameLogic implements Observer{

	int playerCount;
	BattleColorConfig bcConfig;
	NetworkService netSvc;
	
	public MultigameLogic(){
//		if (game.bcConfig.isWifiConnected) {
//			this.netSvc = new NetworkService(game.bcConfig.multicastAddress, game.bcConfig.multicastPort);
//			netSvc.addObserver(this);
//		}
	}
	
	public void startServer(int playerCount){
		this.playerCount = playerCount;
	}
	
	public void sendJoinMsg(){
		
	}
	
	private void sendGameStartMsg(){
		
	}
	
	@Override
	public void update(Observable obs, Object obj) {
		// TODO Auto-generated method stub
		
	}

}
