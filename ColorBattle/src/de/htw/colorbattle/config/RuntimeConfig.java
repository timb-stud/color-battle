package de.htw.colorbattle.config;


/**
 * Variables to set configuration on runtime.
 */
public class RuntimeConfig {
	public boolean useExtConfigFile;
	public boolean isWifiConnected;
	public String multicastAddress;
	public int multicastPort;
	public boolean playSound;
	public GameMode gameMode;
	public  int gameTime = 60;
	
	public float networkPxlUpdateIntervall = 1;
}

