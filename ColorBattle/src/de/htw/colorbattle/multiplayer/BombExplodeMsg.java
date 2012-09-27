package de.htw.colorbattle.multiplayer;

import java.io.Serializable;

/**
 * 
 * This message gets send to the client who picked up a Bomb PowerUp
 *
 */
public class BombExplodeMsg implements Serializable{

	private static final long serialVersionUID = 5747836838725920664L;
	
	public boolean wasPickedUpByServer;
	
	
	public BombExplodeMsg(boolean wasPickedUpByServer) {
		super();
		this.wasPickedUpByServer = wasPickedUpByServer;
	}
	
}
