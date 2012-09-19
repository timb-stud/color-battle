package de.htw.colorbattle.multiplayer;

import java.io.Serializable;

public class InvertControlMsg implements Serializable{
	
	private static final long serialVersionUID = -3167199246503381680L;

	public boolean invertControl;
	
	public InvertControlMsg(boolean invertControl) {
		this.invertControl = invertControl;
	}

}
