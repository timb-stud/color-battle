package de.htw.colorbattle.multiplayer;

import java.io.Serializable;
import java.util.HashMap;
import com.badlogic.gdx.Gdx;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.gameobjects.PlayerSimulation;


public class StartGameMsg implements Serializable{
	
	public StartGameMsg(HashMap<Integer, Player> pMap, int gameTime) {
		playerMap = new HashMap<Integer, PlayerSimulation>();
		this.gameTime = gameTime;
		setPlayerMap(pMap);
	}
	
	private void setPlayerMap(HashMap<Integer, Player> pMap){
		for (Player p : pMap.values()){
			PlayerSimulation ps = new PlayerSimulation(p);
			playerMap.put(ps.id, ps);
		}
	}
	
	public HashMap<Integer, Player> getPlayerMap(){
		HashMap<Integer, Player> pMap = new HashMap<Integer, Player>();
		for (PlayerSimulation ps : playerMap.values()){
			Player p = new Player(ps.getColorInt(), ps.radius);
			p.update(ps);
			Gdx.app.debug("Multiplayer Game", "player with id " + p.id + " colorTexture: " + p.colorTexture);
			pMap.put(p.id, p);
		}
		return pMap;
	}
	
	private static final long serialVersionUID = 6555904338790084806L;
	public int gameTime = 60; //default value
	public HashMap<Integer, PlayerSimulation> playerMap;
}
