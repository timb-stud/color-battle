package de.htw.colorbattle.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Accelerometer {
	
	public static final float max = 2.5f;
	
	public static void updateDirection(Vector2 direction){
		direction.x = applyMaximum(Gdx.input.getAccelerometerY());
		direction.y = applyMaximum(Gdx.input.getAccelerometerX());
		if(direction.len() > max)
		direction.mul(max / direction.len());
	}
	
	private static float applyMaximum(float acc){
		if(acc > max) acc = max;
		else if(acc < -max) acc = -max;
		return acc;
	}
}
