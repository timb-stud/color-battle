package de.htw.colorbattle.multiplayer;

import java.io.Serializable;

public class SpeedDownControlMsg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4745164106053177689L;
	public boolean speedDownControl;
	
	public SpeedDownControlMsg(boolean speedDownControl) {
		this.speedDownControl = speedDownControl;
	}
}
