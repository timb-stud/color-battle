package de.htw.colorbattle.multiplayer;

import java.io.Serializable;

public class SpeedUpControlMsg implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1239928542604952062L;
	

public boolean speedUpControl;
	
	public SpeedUpControlMsg(boolean speedUpControl) {
		this.speedUpControl = speedUpControl;
	}
}
