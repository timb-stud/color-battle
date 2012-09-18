package de.htw.colorbattle;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.htw.colorbattle.config.BattleColorConfig;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "ColorBattle";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 480;
		
		BattleColorConfig bcConfig = new BattleColorConfig();
        bcConfig.isWifiConnected = false;
        bcConfig.multicastAddress = "230.0.0.1"; 
        bcConfig.multicastPort = 1234; //TODO read multicast port from settings view
        bcConfig.playSound = false;
        bcConfig.width = 800;
        bcConfig.height = 480;
		
        // TODO Error auskommentiert
		//new LwjglApplication(new ColorBattleGame(bcConfig), cfg);
	}
}
