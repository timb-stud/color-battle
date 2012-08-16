package de.htw.colorbattle.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class PlayerSimulation  {
	
	public float x;
	public float y;
	public float radius;
	public float speed;
	public float maxSpeed;
	public Vector2 direction;
	
	public PlayerSimulation(Player player) {
		direction = new Vector2(0,0);
		update(player);
	}

	
	public void update(Player player) {
		this.x = player.x;
		this.y = player.y;
		this.radius = player.radius;
		this.speed = player.speed;
		this.maxSpeed = player.maxSpeed;
		this.direction.x = player.direction.x;
		this.direction.y = player.direction.y;
	}
	
	public float distance(Player player){
		float a = Math.abs(x - player.x);
		float b = Math.abs(y - player.y);
		return (float)Math.sqrt(a*a + b*b);
	}
	
	public void move(){
		this.x += this.speed * this.direction.x * Gdx.graphics.getDeltaTime();
		this.y -= this.speed * this.direction.y * Gdx.graphics.getDeltaTime();
	}
}
