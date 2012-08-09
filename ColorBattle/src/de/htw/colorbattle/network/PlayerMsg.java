package de.htw.colorbattle.network;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector2;

import de.htw.colorbattle.gameobjects.Player;

public class PlayerMsg implements Serializable{
	public int id = 0; //TODO set ID
	public float x = 0;
	public float y = 0;
	public float radius = 1;
	public float speed = 3;
	public float maxSpeed = 200;
	public Vector2 direction = new Vector2(0, 0);;
	
	public PlayerMsg(){}

	public PlayerMsg(Player player) {
		super();
		this.id = player.id;
		this.x = player.x;
		this.y = player.y;
		this.radius = player.radius;
		this.speed = player.speed;
		this.maxSpeed = player.maxSpeed;
		this.direction = player.direction;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Player").append(id)
								.append("\n Position X: ").append(this.x)
								.append("\n Position Y: ").append(this.y);
		
		return sb.toString();
	}
}
