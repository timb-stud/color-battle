package de.htw.colorbattle.gameobjects;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import de.htw.colorbattle.network.PlayerMsg;

public class Player {
	public int id; 
	public float x;
	public float y;
	public float radius;
	public float speed;
	public float maxSpeed;
	public Color color;
	public Vector2 direction;
	
	public Player() {
		this(new Color(0f, 0f, 0f, 1f));
	}
	
	public Player(Color color){
		id = (int) System.currentTimeMillis(); //TODO create method to set individual id
		x = 0;
		y = 0;
		radius = 1;
		maxSpeed = 3;
		speed = 200;
		this.direction = new Vector2(0, 0);
		this.color = color;
	}
	
	public Player(PlayerMsg pm) {
		this.id = pm.id;
		this.x = pm.x;
		this.y = pm.y;
		this.radius = pm.radius;
		this.speed = pm.speed;
		this.maxSpeed = pm.maxSpeed;
		this.direction = pm.direction;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
	public void move(){
		this.x += this.speed * this.direction.x * Gdx.graphics.getDeltaTime();
		this.y -= this.speed * this.direction.y * Gdx.graphics.getDeltaTime();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Player").append(id)
								.append("\n Position X: ").append(this.x)
								.append("\n Position Y: ").append(this.y);
		
		return sb.toString();
	}
}
