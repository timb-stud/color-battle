package de.htw.colorbattle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import de.htw.colorbattle.gameobjects.Player;

/**
 * Supplies all functions to compute the result of the GameScreen 
 * and generates a bar chart for the output
 */
public class GameResult {

	private static final int FILTER_COLOR_MIN_FREQUENCY = 100;
	private int totalPixel;
	private LinkedList<Player> playerList;

	private static final float ROUNDING_FACTOR = 1.002f;
	// Graphics
	private static final int BAR_HEIGHT = 80;
	private static final int BAR_MAX_WIDTH = 470;

	private static final int WINDOW_HEIGHT = 350;
	private static final int WINDOW_WIDTH = 570;

	/**
	 * computes the result of the current Screen
	 * @param playersList the Player-List of the current game
	 */
	public GameResult(LinkedList<Player> playersList) {
		this.playerList = playersList;
		computeScore();
	}

	/**
	 * computes the score with the pixels of the current FrameBuffer
	 */
	private void computeScore() {
		byte[] bytePixelArray = ScreenUtils.getFrameBufferPixels(false);
		this.totalPixel = bytePixelArray.length / 4;
		HashMap<Integer, Integer> readedPixel = getPixelMap(bytePixelArray);
		readedPixel = filterAndInverseMapAndRecalculateTotalPixels(readedPixel);
		readedPixel = mergeSimilarColors(readedPixel);
		addScoresToPlayerList(readedPixel);
	}

	/**
	 * converts the byteArray into an HashMap
	 * @param bytePixelArray the byteArray that represents the rgba8888 pixels
	 * @return a HashMap represents the pixel and the frequency of the pixel
	 */
	private HashMap<Integer, Integer> getPixelMap(byte[] bytePixelArray) {
		int currentPixel;
		int pixelCount;
		HashMap<Integer, Integer> pixelMap = new HashMap<Integer, Integer>();

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

	/**
	 * filters the map of low frequency pixel and inverses the integer value for the use with the Colorobjects
	 * and updates the totalPixel count with a rounding factor
	 * @param pixelMap is a HashMap represents the pixel and the frequency of the pixel
	 * @return a HashMap represents the pixel and the frequency of the pixel
	 */
	private HashMap<Integer, Integer> filterAndInverseMapAndRecalculateTotalPixels(
			HashMap<Integer, Integer> pixelMap) {
		Color currentColor = new Color();
		HashMap<Integer, Integer> newMap = new HashMap<Integer, Integer>();
		Set<Integer> keyset = pixelMap.keySet();
		int currentvalue;
		this.totalPixel = 0;
		for (int currentkey : keyset) {
			currentvalue = pixelMap.get(currentkey);
			if (currentvalue > FILTER_COLOR_MIN_FREQUENCY) {
				Color.rgba8888ToColor(currentColor, currentkey);
				newMap.put(currentColor.toIntBits(), currentvalue);
				this.totalPixel = this.totalPixel + currentvalue;
			}
		}
		this.totalPixel = (int) ((float)totalPixel/ROUNDING_FACTOR);
		return newMap;
	}

	/**
	 * ignores alpha component, and rounds values
	 * 
	 * @param colorPlayer ColorObject of the player
	 * @param colorReaded ColorObject of the readed pixel from the FrameBuffer
	 * @return 
	 */
	private boolean colorIsSimlarToColor(Color colorPlayer, Color colorReaded) {
		if (colorPlayer.b != round2digitsDown(colorReaded.b)) {
			return false;
		}
		if (colorPlayer.g != round2digitsDown(colorReaded.g)) {
			return false;
		}
		if (colorPlayer.r != round2digitsDown(colorReaded.r)) {
			return false;
		}
		return true;
	}

	/**
	 * rounds a float value of 2 digits down
	 * @param value a float value
	 * @return the rounded value
	 */
	private float round2digitsDown(float value) {
		return (float) Math.round((value - 0.005) * 100) / 100;
	}

	/**
	 * rounds a float value of 1 digit
	 * @param value a float value
	 * @return the rounded value
	 */
	private float round1digit(float value) {
		return (float) Math.round((value) * 10) / 10;
	}

	/**
	 * adds a percental value to the Players
	 * @param pixelMap is a HashMap represents the pixel and the frequency of the pixel
	 */
	private void addScoresToPlayerList(HashMap<Integer, Integer> pixelMap) {
		Set<Integer> keyset = pixelMap.keySet();
		Color playerColor;
		Color currentColor = new Color();
		int currentvalue;
		for (Player pl : playerList) {
			playerColor = pl.getColor();
			for (int currentkey : keyset) {
				Color.rgba8888ToColor(currentColor, currentkey);
				if (colorIsSimlarToColor(playerColor, currentColor)) {
					currentvalue = pixelMap.get(currentkey);
					pl.setGameScore(((double) currentvalue / (double) totalPixel) * 100.0);
					break;
				}
			}
		}
	}

	/**
	 * generates a bar chart for the output
	 * @return a list of textures with a bar representing the score
	 */
	private LinkedList<Texture> getPlayerScoreTextures() {
		LinkedList<Texture> texList = new LinkedList<Texture>();
		Pixmap pm;
		Texture tex;
		for (Player pl : playerList) {
			pm = new Pixmap(BAR_MAX_WIDTH, BAR_HEIGHT, Format.RGBA8888);
			pm.setColor(pl.getColor());
			pm.fillRectangle(0, 0,
					(int) ((double) BAR_MAX_WIDTH * (pl.getGameScore()/100)),
					BAR_HEIGHT);
			tex = new Texture(pm);
			texList.add(tex);
		}
		return texList;
	}

	/**
	 * generates a texture with all score-bars
	 * @param otherBatch the batch to draw on it
	 * @param otherCam the Camera for resolution scaling
	 * @return a texture with all score-bars
	 */
	public Texture getScoreScreen(SpriteBatch otherBatch, OrthographicCamera otherCam) {
		Pixmap pm;
		Texture tex;
		FrameBuffer scoreFrameBuffer = new FrameBuffer(Format.RGBA8888,
				WINDOW_WIDTH, WINDOW_HEIGHT, false);

		// background
		pm = new Pixmap(WINDOW_WIDTH, WINDOW_HEIGHT, Format.RGBA8888);
		pm.setColor(Color.WHITE);
		pm.fillRectangle(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		tex = new Texture(pm);


		otherBatch.setProjectionMatrix(otherCam.combined);

		scoreFrameBuffer.begin();
		otherBatch.begin();
		otherBatch.draw(tex, 0, 0);

		int y = 10;
		for (Texture tex2 : this.getPlayerScoreTextures()) {
			otherBatch.draw(tex2, 50, y);
			y += BAR_HEIGHT + 10;
		}
		otherBatch.end();
		scoreFrameBuffer.end();

		return scoreFrameBuffer.getColorBufferTexture();
	}

	public LinkedList<Player> getScoredPlayerList() {
		return playerList;
	}

	/**
	 * checks if the color values ​​in the hashmap are similar if this is the case
	 * so the values ​​are merged
	 * @param pixelMap is a HashMap represents the pixel and the frequency of the pixel
	 * @return the merged HashMap
	 */
	private HashMap<Integer, Integer> mergeSimilarColors(
			HashMap<Integer, Integer> pixelMap) {
		Color mainLoopColor = new Color();
		Color innerLoopColor = new Color();
		HashMap<Integer, Integer> newMap = new HashMap<Integer, Integer>();
		Set<Integer> keyset = pixelMap.keySet();
		int mainLoopValue;
		for (int mainLoopKey : keyset) {
			mainLoopValue = pixelMap.get(mainLoopKey);
			for (int innerLoopKey : keyset) {
				if (mainLoopKey != innerLoopKey) {
					Color.rgba8888ToColor(mainLoopColor, mainLoopKey);
					Color.rgba8888ToColor(innerLoopColor, innerLoopKey);
					if (isColorSimilarEnoughToMerge(mainLoopColor,
							innerLoopColor)) {
						mainLoopValue = mainLoopValue
								+ pixelMap.get(innerLoopKey);
					}
				}
			}
			newMap.put(mainLoopKey, mainLoopValue);
		}
		return newMap;
	}

	/**
	 * checks if the colorFix is similar enough to colorVar for merging the Color 
	 * @param colorFix a Color
	 * @param colorVar a Color
	 * @return true if similar
	 */
	private boolean isColorSimilarEnoughToMerge(Color colorFix, Color colorVar) {
		if ((isValueRoundableToValue(colorFix.a, colorVar.a))
				&& (isValueRoundableToValue(colorFix.r, colorVar.r))
				&& (isValueRoundableToValue(colorFix.g, colorVar.g))
				&& (isValueRoundableToValue(colorFix.b, colorVar.b))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks if the one digit rounded values are the same.
	 * @param valueOne a float value
	 * @param valueTwo a float value
	 * @return true if the same
	 */
	private boolean isValueRoundableToValue(float valueOne, float valueTwo) {
		// alternative maximum deviation of the values ​​would be calculated, it would probably slightly more accurate
		if (valueOne == valueTwo) {
			return true;
		} else if (round1digit(valueOne) == round1digit(valueTwo)) {
			return true;
		}
		return false;
	}
}