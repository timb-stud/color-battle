package de.htw.colorbattle;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.htw.colorbattle.config.RuntimeConfig;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "ColorBattle";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 480;
		
		RuntimeConfig rcConfig = new RuntimeConfig();
        rcConfig.isWifiConnected = false;
        rcConfig.multicastAddress = "230.0.0.1"; 
        rcConfig.multicastPort = 1234; //TODO read multicast port from settings view
        rcConfig.playSound = false;
		
        // TODO Error auskommentiert
    //  ColorBattleGame cbg = new ColorBattleGame(rcConfig,null,null);
	//	new LwjglApplication(cbg, cfg);
	}
}
