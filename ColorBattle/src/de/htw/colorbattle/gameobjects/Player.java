package de.htw.colorbattle.gameobjects;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;


public class Player implements Serializable{
	public int id; //TODO set ID
	public float x;
	public float y;
	public float radius;
	public float speed;
	public float maxSpeed;
	public float directionX;
	public float directionY;
	public Color color;
	
	public Player() {
		this(new Color(0f, 0f, 0f, 1f));
	}
	
	public Player(Color color){
		x = 0;
		y = 0;
		radius = 1;
		maxSpeed = 3;
		speed = 200;
		directionX = 0;
		directionY = 0;
		this.color = color;
	}
	
	public void move(){
		this.x += this.speed * this.directionX * Gdx.graphics.getDeltaTime();
		this.y -= this.speed * this.directionY * Gdx.graphics.getDeltaTime();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Player").append(id)
								.append("\n Position X: ").append(this.x)
								.append("\n Position Y: ").append(this.y);
		
		return sb.toString();
	}
}
