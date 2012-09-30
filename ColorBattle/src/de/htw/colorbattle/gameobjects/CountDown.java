package de.htw.colorbattle.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;

/**
 * Class to generate and control the countdown texture. 
 * 
 */

public class CountDown {
	
	private int height;
	private Color color;
	private long currentTime = System.currentTimeMillis() / 1000;
	private long seconds = 1;
	
	public int x = 0;
	public int y = 0;
	public static final int WIDTH = 10;
	public Texture countDownTexture;
	

	/**
	 * Constructor to create and configure the pixmap and set it into the countdown texture.
	 * @param color Color of the countdown
	 * @param height Height of the pixmap
	 */
	public CountDown(Color color, int height) {
		this.color = color;
		this.height = height;
	    Pixmap countDownPixmap = new Pixmap(20, 480, Format.RGBA8888);
		countDownPixmap.setColor(color);
		countDownPixmap.fillRectangle(x, y, WIDTH, this.height);//x,y from top, thickness, height
		countDownTexture = new Texture(countDownPixmap);
		countDownPixmap.dispose();
			
	}
	
	
	/**
	 * Method to decrease to countdown bar every second by setting the pixmap y coordinate. 
	 * @param endTime Time when game ends
	 * @param gameTime Play time of the game
	 * @return Boolean if game is finished
	 */
	public Boolean activateCountDown(long endTime, int gameTime){
		currentTime = System.currentTimeMillis() / 1000;
		
		if (currentTime == ((endTime - gameTime)+seconds)){
			this.y = this.y - (this.height / gameTime);
			seconds +=1;
		}
		
		if (currentTime== endTime){
					return true;
				}
		else{
			return false;
		}
	}
	
	/**
	 * Method to dispose countdown texture.
	 */
	public void dispose(){
		countDownTexture.dispose();
	}

	/**
	 * Method to get the remaining time of the game in seconds. 
	 * @param gameTime Play time of the game
	 * @return Remaining time
	 */
	public int getRemainingTimeInSeconds(int gameTime) {
		return (int)(gameTime -  seconds);
	}

}
