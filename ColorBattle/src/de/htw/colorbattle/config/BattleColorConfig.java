package de.htw.colorbattle.config;

public class BattleColorConfig {
	public boolean isWifiConnected;
	public String multicastAddress;
	public int multicastPort;
	public boolean playSound;
	public int multigamePlayerCount = 1; //only default value, count set in main activity
	public float networkPxlUpdateIntervall = 1;
}
