package de.htw.colorbattle.multiplayer;

import java.io.Serializable;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;

import de.htw.colorbattle.gameobjects.PlayerSimulation;


/**
 * 
 * This message gets send to the clients if all clients are connected and the game starts.
 */
public class StartGameMsg implements Serializable{
	private static final long serialVersionUID = 6555904338790084806L;
	public int gameTime = 60; //default value
	public int playerCount;
	public HashMap<Integer, PlayerSimulation> playerMap;
	
	public StartGameMsg(HashMap<Integer, PlayerSimulation> pMap, int gameTime) {
//		playerMap = pMap;
		playerMap = new HashMap<Integer, PlayerSimulation>();
		playerMap.putAll(pMap);
		this.gameTime = gameTime;
		this.playerCount = playerMap.size();
		Gdx.app.debug("StartMessage", "Send StartMsg with " + playerCount + " players in map");
	}
}
