package de.htw.colorbattle.gameobjects;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class PlayerSimulation implements Serializable{
	private static final long serialVersionUID = 1525952974653455375L;
	public int id;
	public float x;
	public float y;
	public float radius;
	public float speed;
	public float maxSpeed;
	public Vector2 direction;
	
	public PlayerSimulation(float radius){
		id = (int) System.currentTimeMillis(); //TODO create method to set individual id
		x = 0;
		y = 0;
		this.radius = radius;
		maxSpeed = 3;
		speed = 200;
		this.direction = new Vector2(0, 0);
	}
	
	public PlayerSimulation(Player player) {
		direction = new Vector2(0,0);
		update((PlayerSimulation) player);
	}
	
	public void update(PlayerSimulation p) {
		this.id = p.id;
		this.x = p.x;
		this.y = p.y;
		this.radius = p.radius;
		this.speed = p.speed;
		this.maxSpeed = p.maxSpeed;
		this.direction = p.direction;
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Player").append(id)
								.append("\n Position X: ").append(this.x)
								.append("\n Position Y: ").append(this.y);
		
		return sb.toString();
	}
}
