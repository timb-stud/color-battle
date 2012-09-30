package de.htw.colorbattle.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import de.htw.colorbattle.config.BattleColorConfig;
import de.htw.colorbattle.multiplayer.PowerUpSpawnMsg;

/**
 * Class to generate and control the PowerUps 
 *
 */
public class PowerUp {
	public boolean isVisible;
	public boolean isBombExploded = false;
	public boolean invertControl = false;
	public boolean wasPickedUpByServer = false;
	public Color pickedUpPlayerColor;
	public Type type;
	public Rectangle rect;
	private Rectangle playerRect;

	public enum Type {BOMB, INVERT}

	/**
	 * Constructor to generate a new PowerUp object 
	 * @param x X coordinate of the PowerUp rectangle
	 * @param y Y coordinate of the PowerUp rectangle
	 * @param width WIDTH coordinate of the PowerUp rectangle
	 * @param height  HEIGHT coordinate of the PowerUp rectangle
	 */
	public PowerUp(int x, int y, int width, int height) {
		super();
		isVisible = false;
		rect = new Rectangle(x, y, width, height);
		playerRect = new Rectangle();
		shuffleType();
	}
	
	/**
	 * Method to control when PowerUp is picked up by player
	 * @param player Player object
	 * @return Boolean if player picked up PowerUp
	 */
	public boolean isPickedUpBy(Player player) {
		playerRect.x = player.x;
		playerRect.y = player.y;
		playerRect.width = player.radius * 2;
		playerRect.height = playerRect.width;
		return playerRect.overlaps(rect);
	}
	
	/**
	 * Method to generate the pixmap and texture of the bomb PowerUp
	 * @param color
	 * @return
	 */
	public Texture getBombTexture(Color color) {
		float size = 3.5f;
		float width = rect.width * size;
		float height = rect.height * size;
		Pixmap pixmap = new Pixmap((int)width, (int)height, Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillCircle((int)width/2, (int)height/2, (int)width/2);
		Texture texture = new Texture(pixmap);	
		pixmap.dispose();
		return texture;
	}
	
	/**
	 * Method to set a random position in the play ground to spawn the PowerUp
	 */
	private void shufflePosition(){
		float x = (float)Math.random() * BattleColorConfig.WIDTH;
		if(x + rect.width > BattleColorConfig.WIDTH){
			x = BattleColorConfig.WIDTH - rect.width;
		}
		float y = (float)Math.random() * BattleColorConfig.HEIGHT;
		if(y + rect.height > BattleColorConfig.HEIGHT){
			y = BattleColorConfig.HEIGHT - rect.height;
		}
		rect.x = x;
		rect.y = y;
	}
	
	/**
	 * Method to set the PowerUp type randomly
	 */
	private void shuffleType(){
		if(Math.random() < 0.6){
			type = Type.BOMB;
		}else {
			type = Type.INVERT;
		}
	}
	
	/**
	 * Method to spawn the PowerUp on the play ground
	 */
	public void spawn(){
		shufflePosition();
		shuffleType();
		isVisible = true;
	}
	
	/**
	 * Method to set the coordinates of the PowerUp spawn message
	 * @param powerUpSpawnMsg Message object
	 */
	public void set(PowerUpSpawnMsg powerUpSpawnMsg){
		rect.x = powerUpSpawnMsg.x;
		rect.y = powerUpSpawnMsg.y;
	}
}