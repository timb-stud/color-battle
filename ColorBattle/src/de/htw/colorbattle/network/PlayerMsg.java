package de.htw.colorbattle.network;

import java.io.Serializable;

public class PlayerMsg implements Serializable{

	private long currentTimeMillies;

	private float posX;
	private float posY;
	
	public PlayerMsg(float posX, float posY){
		this.currentTimeMillies = System.currentTimeMillis();
		this.posX = posX;
		this.posY = posY;
	}

	public long getCurrentTimeMillies() {
		return currentTimeMillies;
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}
	
}
