package de.htw.colorbattle.config;

public class BattleColorConfig {
	public boolean isWifiConnected;
	public String multicastAddress;
	public int multicastPort;
	public boolean playSound;
	//only default value, values updated in main activity
	public int multigamePlayerCount = 1; 
	public float networkPxlUpdateIntervall = 1;
	public int gameTime = 60;
}
