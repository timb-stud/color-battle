package de.htw.colorbattle.config;

/**
 * Für eine feste Konfiguration
 */
public class BattleColorConfig {
	public boolean isWifiConnected;
	public String multicastAddress;
	public int multicastPort;
	public boolean playSound;
	public GameMode gameMode;
	public float networkPxlUpdateIntervall = 1;
	
	//only default value, values updated in main activity
	public static String DEVICE_ID;
	public static final int GAME_TIME = 60;
	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;
}
