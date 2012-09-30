package de.htw.colorbattle.gameobjects;

/**
 * Class to create the border of the game and handle if player collides with it.
 *
 */

public class GameBorder {
	public int width;
	public int height;
	
	/**
	 * Constructor to create the border of the game. 
	 * @param width Width of play ground
	 * @param height Height of play ground 
	 */
	public GameBorder(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Method to handle if player collides with the border by setting the x and y coordinates of the player.
	 * @param player Simulation object of the player
	 */
	public void handelCollision(PlayerSimulation player){
		if(player.x < 0) player.x = 0;
		else if(player.x > width - player.radius * 2) player.x = width - player.radius * 2;
		if(player.y < 0) player.y = 0;
		else if(player.y > height -player.radius * 2) player.y = height - player.radius * 2;
	}
}
