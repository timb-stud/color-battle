package de.htw.colorbattle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

import de.htw.colorbattle.gameobjects.Player;

public class GameResult {

	private static final int FILTER_COLOR_MIN_FREQUENCY = 100;
	private long pixelNumber;
	private LinkedList<Player> playerList;

	public GameResult(LinkedList<Player> playerList) {
		computeScore();
	}

	private void computeScore() {
		byte[] bytePixelArray = ScreenUtils.getFrameBufferPixels(false);
		HashMap<Integer, Integer> readedPixel = getPixelMap(bytePixelArray);
		System.out.println(readedPixel);
		readedPixel = filterAndInverseMap(readedPixel);
		System.out.println(readedPixel);
	}

	private HashMap<Integer, Integer> getPixelMap(byte[] bytePixelArray) {
		int currentPixel;
		int pixelCount;
		HashMap<Integer, Integer> pixelMap = new HashMap<Integer, Integer>();

		boolean ausgegeben = false;

		for (int i = 0; i < bytePixelArray.length - 4; i = i + 4) {
		
			currentPixel = (bytePixelArray[i] & 0xFF)
					| ((bytePixelArray[i + 1] & 0xFF) << 8)
					| ((bytePixelArray[i + 2] & 0xFF) << 16)
					| ((bytePixelArray[i + 3] & 0xFF) << 24);		

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

	private HashMap<Integer, Integer> filterAndInverseMap(HashMap<Integer, Integer> pixelMap) {
		Color currentColor = new Color();
		HashMap<Integer, Integer> neue = new HashMap<Integer, Integer>();
		Set<Integer> keyset = pixelMap.keySet();
		int currentvalue;
		for (int currentkey : keyset) {
			currentvalue = pixelMap.get(currentkey);
			if (currentvalue > FILTER_COLOR_MIN_FREQUENCY) {
				Color.rgba8888ToColor(currentColor, currentkey);  // das hier kann man ev optimieren
				neue.put(currentColor.toIntBits(), currentvalue);
			}
		}
		return neue;
	}


	/**
	 * ignores alpha component
	 * 
	 * @param colorOne
	 * @param colorTwo
	 * @return
	 */
	private boolean ColorIsSimlarToColor(Color colorOne, Color colorTwo) {
		if (colorOne.b != colorTwo.b){ return false;}
		if (colorOne.g != colorTwo.g){ return false;}
		if (colorOne.r != colorTwo.r){ return false;}
		return true;
		
	}

	private void addScoresToPlayerList() {
	}

	public Texture getScoreScreen() {
		return null;
	}

	public LinkedList<Player> getScoredPlayerList() {
		return null;
	}

}