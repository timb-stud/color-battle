package de.htw.colorbattle.toast;

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
}
