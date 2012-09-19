package de.htw.colorbattle.network;

import java.io.Serializable;

public class BombExplodeMsg implements Serializable{

	private static final long serialVersionUID = 5747836838725920664L;
	
	public boolean wasPickedUpByServer;
	
	
	public BombExplodeMsg(boolean wasPickedUpByServer) {
		super();
		this.wasPickedUpByServer = wasPickedUpByServer;
	}
	
}
