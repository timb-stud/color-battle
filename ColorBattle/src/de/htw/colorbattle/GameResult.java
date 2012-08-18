package de.htw.colorbattle;

import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameResult {

	private static final int FILTER_COLOR_MIN_FREQUENCY = 100;
	
	public void getScoreScreen(){}

	public void computeScore() {

		byte[] bytePixelArray = ScreenUtils.getFrameBufferPixels(false);
		HashMap<Integer, Integer> pixelMap = bytes2PixelMap(bytePixelArray);
		

		pixelMap = filterMap(pixelMap);

		System.out.println(pixelMap.size());
		System.out.println(pixelMap.toString());
		
		// COLOR 
		// soll -33619454
		// 33 61 94 54
		// 2 2 -1 -3
	
		
		Color colorx = Color.BLUE;

		//int c = pixelMap.get(blue);
		
		//Color gen = Color.rgba
		
		
		System.out.println("Color 2222 int: "+colorx.toIntBits()+", float "+colorx.toFloatBits() + " ,a: " + colorx.a + " ,g: " + colorx.g + " ,r: " + colorx.r 
				+" ,int float cast: "+Float.floatToIntBits(colorx.toFloatBits()) + " ,int rawn float cast: "+Float.floatToRawIntBits(colorx.toFloatBits())
				+" ,rgba intout: "+ Color.rgba8888(colorx));
		
		int ausg = -33619454;
		
		System.out.println("Ausgelesenes: int: " + ausg + ", intbits2float: " + Float.intBitsToFloat(ausg) + " , ");
		
		
		
		
	}

	private HashMap<Integer, Integer> bytes2PixelMap(byte[] bytePixelArray) {
		int currentPixel;
		int pixelCount;
		HashMap<Integer, Integer> pixelMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < bytePixelArray.length - 4; i = i + 4) {
			System.out.println(bytePixelArray[i] + " "+ 
			bytePixelArray[i + 1]+" "+bytePixelArray[i + 2]+ " "+bytePixelArray[i + 3]);
			byte byteeins = bytePixelArray[i];
			byte bytezwei = bytePixelArray[i+1]; 
			byte bytedrei = bytePixelArray[i+2];
			byte bytevier = bytePixelArray[i+3];
			currentPixel = (bytePixelArray[i] & 0xFF)
					| ((bytePixelArray[i + 1] & 0xFF) << 8)
					| ((bytePixelArray[i + 2] & 0xFF) << 16)
					| ((bytePixelArray[i + 3] & 0xFF) << 24);
			System.out.println(currentPixel);
			
			if (pixelMap.containsKey(currentPixel)) {
				pixelCount = pixelMap.get(currentPixel);
				pixelCount++;
				pixelMap.put(currentPixel, pixelCount);
			} else {
				pixelMap.put(currentPixel, 1);
			}
		}
		return pixelMap;
	}

	
	
	
	
	private HashMap<Integer, Integer> filterMap(HashMap<Integer, Integer> pixelMap){
		Set<Integer> keyset = pixelMap.keySet();		
		HashMap<Integer, Integer>  neue  = new HashMap<Integer, Integer>();		
		int currentvalue;		
		for (int currentkey: keyset) {
			currentvalue = pixelMap.get(currentkey);
			if (currentvalue > FILTER_COLOR_MIN_FREQUENCY){
				//pixelMap.remove(currentkey);
				neue.put(currentkey, currentvalue);
			}
		}		 		
		return neue;//pixelMap;
	}
	
	
	
}