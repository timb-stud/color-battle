package de.htw.colorbattle.gameobjects;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import de.htw.colorbattle.ColorBattleGame;

public class PlayerSimulation implements Serializable{
	private static final long serialVersionUID = 1525952974653455375L;
	public int id;
	public float x;
	public float y;
	public float radius;
	public float speed;
	public float maxSpeed;
	public Vector2 direction;
	public String deviceId;
	public int colorInt;
	
	public PlayerSimulation(float radius){
		id = 0;
		deviceId = ColorBattleGame.getDeviceId();
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
		this.direction.x = p.direction.x;
		this.direction.y = p.direction.y;
		this.deviceId = p.deviceId;
		this.colorInt = p.colorInt;
//		Gdx.app.debug("PS", "update PlayerSim playerid: " + this.id + " colorInt " + this.colorInt);
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
		StringBuilder sb = new StringBuilder("{id: ").append(id)
								.append(", x: ").append(this.x)
								.append(", y: ").append(this.y)
								.append(", direction: ").append(this.direction)
								.append("}");
		
		return sb.toString();
	}

	/**
	 * liefert ColorObjekt aus int rgba8888
	 * @return
	 */
	public Color getColorInt() {
		Color colorObj = new Color();
		Color.rgba8888ToColor(colorObj, this.colorInt);
		return colorObj;
	}

	/**
	 * speichert ColorObjekt als int rgba8888
	 * @param colorObj
	 */
	public void setColorInt(Color colorObj) {
		this.colorInt = Color.rgba8888(colorObj);	
	}
	
	
	
	
}
