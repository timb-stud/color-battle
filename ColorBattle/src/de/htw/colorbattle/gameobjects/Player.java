package de.htw.colorbattle.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;

public class Player extends PlayerSimulation {
	private static final long serialVersionUID = 5797914124072045957L;
	public Color color;
	public Texture colorTexture;
	private double gameScore;
	private int playerRadius;

	public Player() {
		this(Color.MAGENTA, 30);
	}

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
	
	public void dispose() {
		colorTexture.dispose();
	}

	public double getGameScore() {
		return gameScore;
	}

	public void setGameScore(double gameScore) {
		this.gameScore = gameScore;
	}

	public Color getColor() {
		return color;
	}
	
	public void setNewColor(int colorInt){
		this.colorInt = colorInt;
		this.color = this.getColorInt();
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
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
