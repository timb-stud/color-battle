package de.htw.colorbattle.input;

import com.badlogic.gdx.Gdx;

public class Accelerometer {
	
	public static final float max = 3;
	public static final float min = 0.5f;
	
	public static float getX(){
		return convert(Gdx.input.getAccelerometerY());
	}
	
	public static float getY(){
		return convert(Gdx.input.getAccelerometerX());
	}
	
	private static float convert(float acc){
		if(acc > 0 && acc < min) acc = 0;
		else if(acc < 0 && acc > -min) acc = 0;
		if(acc > max) acc = max;
		else if(acc < -max) acc = -max;
		return acc;
	}
}
