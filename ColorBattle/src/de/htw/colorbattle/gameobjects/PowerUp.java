package de.htw.colorbattle.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import de.htw.colorbattle.multiplayer.PowerUpSpawnMsg;



public class PowerUp {
	public boolean isVisible;
	public boolean isBombExploded = false;
	public boolean wasPickedUpByServer = false;
	public Type type;
	public Rectangle rect;
	private Rectangle playerRect;

	public enum Type {BOMB, INVERT}
	

	public PowerUp(int x, int y, int width, int height) {
		super();
		isVisible = false;
		rect = new Rectangle(x, y, width, height);
		playerRect = new Rectangle();
		shuffleType();
	}
	
	public boolean isPickedUpBy(Player player) {
		playerRect.x = player.x;
		playerRect.y = player.y;
		playerRect.width = player.radius * 2;
		playerRect.height = playerRect.width;
		return playerRect.overlaps(rect);
	}
	
	public Texture getBombTexture(Color color) {
		float size = 2.5f;
		float width = rect.width * size;
		float height = rect.height * size;
		Pixmap pixmap = new Pixmap((int)width, (int)height, Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillCircle((int)width/2, (int)height/2, (int)width/2);
		Texture texture = new Texture(pixmap);	
		pixmap.dispose();
		return texture;
	}
	
	public void shufflePosition(){
		float x = (float)Math.random() * 800;
		if(x + rect.width > 800){
			x = 800 - rect.width;
		}
		float y = (float)Math.random() * 480;
		if(y + rect.height > 480){
			y = 480 - rect.height;
		}
		rect.x = x;
		rect.y = y;
	}
	
	public void shuffleType(){
		if(Math.random() < 0.5){
			type = Type.BOMB;
		}else {
			type = Type.INVERT;
		}
	}
	
	public void spawn(){
		shufflePosition();
		shuffleType();
		isVisible = true;
	}
	
	public void set(PowerUpSpawnMsg powerUpSpawnMsg){
		rect.x = powerUpSpawnMsg.x;
		rect.y = powerUpSpawnMsg.y;
	}
	
}
