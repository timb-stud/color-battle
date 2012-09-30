package de.htw.colorbattle.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;

/**
 * Class extends player simulation and generates the player object
 *
 */

public class Player extends PlayerSimulation {
	private static final long serialVersionUID = 5797914124072045957L;
	public Color color;
	public Texture colorTexture;
	private double gameScore;
	private int playerRadius;

	public Player() {
		this(Color.MAGENTA, 30);
	}

	/**
	 * Constructor to generate the player pixmap with color and size and add into a texture
	 * @param color Color of the player
	 * @param radius Radius of the player pixmap
	 */
	public Player(Color color, float radius) {
		super(radius);
		int r = Math.round(radius);
		this.playerRadius = r;
		Pixmap colorPixmap = new Pixmap(r * 2, r * 2, Format.RGBA8888);
		colorPixmap.setColor(color);
		colorPixmap.fillCircle(r, r, r);
		colorTexture = new Texture(colorPixmap);
		colorPixmap.dispose();
		this.color = color;
		this.setColorInt(this.color);
	}
	/**
	 * Method to dispose the texture
	 */
	public void dispose() {
		colorTexture.dispose();
	}

	/**
	 * Method the return the score of the player
	 * @return Game score
	 */
	public double getGameScore() {
		return gameScore;
	}

	/**
	 * Method to set the game score of the player
	 * @param gameScore Game score
	 */
	public void setGameScore(double gameScore) {
		this.gameScore = gameScore;
	}

	/**
	 * Method to get the color value
	 * @return Color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Method the set the color value in the player simulation
	 * @param colorInt Color value
	 */
	public void setNewColor(int colorInt){
		this.colorInt = colorInt;
		this.color = this.getColorInt();
	}
	
	/**
	 * Method the set the color value of the player
	 * @param color Color
	 */
	public void setColor(Color color){
		this.color = color;
	}
	
	/**
	 * Method to generate a new player pixmap and add it into a texture
	 */
	public void repaintColorTexture(){
		int r = playerRadius;
		Pixmap colorPixmap = new Pixmap(r * 2, r * 2, Format.RGBA8888);
		colorPixmap.setColor(color);
		colorPixmap.fillCircle(r, r, r);
		this.colorTexture = new Texture(colorPixmap);
		colorPixmap.dispose();
	}

	@Override
	public String toString() {
		return "Player [color=" + color + ", colorTexture=" + colorTexture
				+ ", gameScore=" + gameScore + "]";
	}

}
