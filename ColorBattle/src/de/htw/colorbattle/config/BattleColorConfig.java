package de.htw.colorbattle.config;


// TODO why Objekts .... make static ... oder will da einer was zur laufzeit ändern
public class BattleColorConfig {
	public boolean isWifiConnected;
	public String multicastAddress;
	public int multicastPort;
	public boolean playSound;
	public GameMode gameMode;
	
	//only default value, values updated in main activity
	public int multigamePlayerCount = 1; 
	public float networkPxlUpdateIntervall = 1;
	public static final int GAME_TIME = 60;
	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;
	
}

