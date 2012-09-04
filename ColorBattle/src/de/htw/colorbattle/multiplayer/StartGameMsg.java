package de.htw.colorbattle.multiplayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import de.htw.colorbattle.gameobjects.PlayerSimulation;


public class StartGameMsg implements Serializable{
	private static final long serialVersionUID = 6555904338790084806L;
	public int gameTime = 60; //default value
	public HashMap<Integer, PlayerSimulation> playerMap;
}
