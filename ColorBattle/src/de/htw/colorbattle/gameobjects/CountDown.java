package de.htw.colorbattle.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;

public class CountDown {
	
	private int height;
	private Color color;
	private long currentTime = System.currentTimeMillis() / 1000;
	private long seconds = 1;
	
	public int x = 0;
	public int y = 0;
	public static final int WIDTH = 10;
	public Texture countDownTexture;
	

	public CountDown(Color color, int height) {
		this.color = color;
		this.height = height;
	    Pixmap countDownPixmap = new Pixmap(20, 480, Format.RGBA8888);
		countDownPixmap.setColor(color);
		countDownPixmap.fillRectangle(x, y, WIDTH, this.height);//x,y from top, thickness, height
		countDownTexture = new Texture(countDownPixmap);
		countDownPixmap.dispose();
			
	}
	
	
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
	
	
	public void dispose(){
		countDownTexture.dispose();
	}

	public int getHeight() {
		return height;
	}
	
	public Color getColor() {
		return color;
	}

}
