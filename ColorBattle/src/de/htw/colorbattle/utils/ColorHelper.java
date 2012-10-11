package de.htw.colorbattle.utils;

import com.badlogic.gdx.graphics.Color;

public class ColorHelper {

	/**
	 * get color from a given color int value
	 * @param colorInt
	 * @return
	 */
	public static Color getColorFromInt(int colorInt){
		Color colorObj = new Color();
		Color.rgba8888ToColor(colorObj, colorInt);
		return colorObj;
	}
	
	/**
	 * creates color int from color
	 * @param colorObj
	 */
	public static int getColorInt(Color colorObj) {
		return Color.rgba8888(colorObj);	
	}
	
	/**
	 * Return the name as String of the given color
	 * @param color
	 * @return
	 */
	public static String getColorName(Color color){
		if (color.equals(Color.GREEN))
			return "GREEN";
		if (color.equals(Color.RED))
			return "RED";
		if (color.equals(Color.BLUE))
			return "BLUE";
		if (color.equals(Color.YELLOW))
			return "YELLOW";
		
		return "<unknown color>";
	}
}
