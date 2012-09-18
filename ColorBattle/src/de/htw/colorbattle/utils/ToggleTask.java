package de.htw.colorbattle.utils;

import java.util.TimerTask;

public class ToggleTask extends TimerTask{
	private boolean toggle = false;
	
	public boolean toggleState(){
		return toggle;
	}
	
	public void setToggleState(boolean state){
		this.toggle = state;
	}

	@Override
	public void run() {
		toggle = toggle ? false : true;
	}
}
