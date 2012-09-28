package de.htw.colorbattle.config;


/**
 * Fuer Konfigurationen zur Laufzeit
 */
public class RuntimeConfig {
	public boolean isWifiConnected;
	public String multicastAddress;
	public int multicastPort;
	public boolean playSound;
	public GameMode gameMode;
	public  int gameTime = 60;
	
	public float networkPxlUpdateIntervall = 1;
}

