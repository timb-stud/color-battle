package de.htw.colorbattle.config;


// TODO why Objekts .... make static
public class BattleColorConfig {
	public boolean isWifiConnected;
	public String multicastAddress;
	public int multicastPort;
	public boolean playSound;
	public GameMode gameMode;
	
	//only default value, values updated in main activity
	public int multigamePlayerCount = 1; 
	public float networkPxlUpdateIntervall = 1;
	public int gameTime = 10;
	public static int width = 800;
	public static int height = 480;

}

