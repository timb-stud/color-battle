package de.htw.colorbattle.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * Handles all input actions from the accelerometer.
 *
 */
public class Accelerometer {
	
	public static final float MAX = 2.5f;
	public static final float ACCURACY = 100;
	
	/**
	 * Updates a direction vector to the current accelerometer input values.
	 * @param direction to update
	 */
	public static void updateDirection(Vector2 direction){
		int buff;
		direction.x = applyMaximum(Gdx.input.getAccelerometerY());
		direction.y = applyMaximum(Gdx.input.getAccelerometerX());
		if(direction.len() > MAX)
		direction.mul(MAX / direction.len());
		buff = Math.round(direction.x * ACCURACY);
		direction.x = buff / ACCURACY;
		buff = Math.round(direction.y * ACCURACY);
		direction.y = buff / ACCURACY;
	}
	
	/**
	 * Applies the maximum from MAX to the given parameter acc
	 * @param acc
	 * @return acc if acc lies between MAX or -MAX otherwise MAX or -MAX is returned
	 */
	private static float applyMaximum(float acc){
		if(acc > MAX) acc = MAX;
		else if(acc < -MAX) acc = -MAX;
		return acc;
	}
}
