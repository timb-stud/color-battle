package de.htw.colorbattle.multiplayer;

import java.io.Serializable;

import de.htw.colorbattle.gameobjects.PowerUp;

/**
 * 
 * This messages gets send to the clients if a new PowerUp spawns on the server.
 * 
 */
public class PowerUpSpawnMsg implements Serializable{

	private static final long serialVersionUID = 6790677959888014107L;
	
	public float x;
	public float y;
	
	public PowerUpSpawnMsg(PowerUp powerUp) {
		super();
		this.x = powerUp.rect.x;
		this.y = powerUp.rect.y;
	}
	
	
}
