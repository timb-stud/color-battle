package de.htw.colorbattle.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;


public class Player {
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
}
