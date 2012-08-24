package de.htw.colorbattle.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Accelerometer {
	
	public static final float MAX = 2.5f;
	public static final float ACCURACY = 100;
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
	
	private static float applyMaximum(float acc){
		if(acc > MAX) acc = MAX;
		else if(acc < -MAX) acc = -MAX;
		return acc;
	}
}
