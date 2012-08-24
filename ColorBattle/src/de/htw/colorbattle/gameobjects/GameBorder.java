package de.htw.colorbattle.gameobjects;

public class GameBorder {
	public int width;
	public int height;
	
	public GameBorder(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void handelCollision(PlayerSimulation player){
		if(player.x < 0) player.x = 0;
		else if(player.x > width - player.radius * 2) player.x = width - player.radius * 2;
		if(player.y < 0) player.y = 0;
		else if(player.y > height -player.radius * 2) player.y = height - player.radius * 2;
	}
}
