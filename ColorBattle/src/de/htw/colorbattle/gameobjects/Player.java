package de.htw.colorbattle.gameobjects;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Player {
	public int id; //TODO set ID
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
		x = 0;
		y = 0;
		radius = 1;
		maxSpeed = 3;
		speed = 200;
		this.direction = new Vector2(0, 0);
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
