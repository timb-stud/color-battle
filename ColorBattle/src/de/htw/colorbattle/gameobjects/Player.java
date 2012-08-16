package de.htw.colorbattle.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
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
	public Texture colorTexture;
	
	public Player() {
		this(Color.MAGENTA, 30);
	}
	
	public Player(Color color, float radius){
		id = (int) System.currentTimeMillis(); //TODO create method to set individual id
		x = 0;
		y = 0;
		this.radius = radius;
		maxSpeed = 3;
		speed = 200;
		this.direction = new Vector2(0, 0);
		this.color = color;
		int r = Math.round(radius);
		Pixmap colorPixmap = new Pixmap(r* 2, r* 2, Format.RGBA8888);
		colorPixmap.setColor(color);
		colorPixmap.fillCircle(r, r, r);
		colorTexture = new Texture(colorPixmap);
		colorPixmap.dispose();
	}
	
	public void update(PlayerMsg pm) {
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
	
	public void dispose(){
		colorTexture.dispose();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Player").append(id)
								.append("\n Position X: ").append(this.x)
								.append("\n Position Y: ").append(this.y);
		
		return sb.toString();
	}
}
