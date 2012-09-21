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

public class GameResult {

	private static final int FILTER_COLOR_MIN_FREQUENCY = 100;
	private int totalPixel;
	private LinkedList<Player> playerList;

	private static final float ROUNDING_FACTOR = 1.0035f;
	// Graphics
	private static final int BAR_HEIGHT = 90;
	private static final int BAR_MAX_WIDTH = 470;

	private static final int WINDOW_HEIGHT = 350;
	private static final int WINDOW_WIDTH = 570;

	/**
	 * generiert aus der aktuellen Ansicht ein Spielergebnis
	 * @param playersList
	 */
	public GameResult(LinkedList<Player> playersList) {
		this.playerList = playersList;
		computeScore();
	}

	private void computeScore() {
		byte[] bytePixelArray = ScreenUtils.getFrameBufferPixels(false);
		this.totalPixel = bytePixelArray.length / 4;
		HashMap<Integer, Integer> readedPixel = getPixelMap(bytePixelArray);
		readedPixel = filterAndInverseMapAndRecalculateTotalPixels(readedPixel);
		readedPixel = mergeSimilarColors(readedPixel);
		addScoresToPlayerList(readedPixel);
	}

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
	 * @param colorPlayer
	 * @param colorReaded
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

	private float round2digitsDown(float value) {
		return (float) Math.round((value - 0.005) * 100) / 100;
	}

	private float round1digit(float value) {
		return (float) Math.round((value) * 10) / 10;
	}

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

	public Texture getScoreScreen(SpriteBatch otherBatch, OrthographicCamera otherCam) {

		Pixmap pm;
		Texture tex;
		FrameBuffer scoreFrameBuffer = new FrameBuffer(Format.RGBA8888,
				WINDOW_WIDTH, WINDOW_HEIGHT, false);

		// Hintergrund
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
	 * prüft ob die Farbwerte in der Hashmap ähnlich sind falls dies der Fall
	 * ist werden die Values also die Anzahl aufaddiert.
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

	private boolean isValueRoundableToValue(float valueOne, float valueTwo) {
		if (valueOne == valueTwo) {
			return true;
		} else if (round1digit(valueOne) == round1digit(valueTwo)) {
			return true;
		}
		return false;
	}

	// TODO später löschen aber vllt brauch ich es wieder
	/**
	 * prüft ob mindestens 2 der 4 rgba Werte gleich sind
	 */
	private boolean areMinTwoRGBAValuesTheSame(Color colorFix, Color colorVar) {
		if (colorFix.a == colorVar.a) {
			if (colorFix.r == colorVar.r) {
				return true;
			} else if (colorFix.g == colorVar.g) {
				return true;
			} else if (colorFix.b == colorVar.b) {
				return true;
			}
		} else if (colorFix.r == colorVar.r) {
			if (colorFix.g == colorVar.g) {
				return true;
			} else if (colorFix.b == colorVar.b) {
				return true;
			}
		} else if (colorFix.g == colorVar.g) {
			if (colorFix.b == colorVar.b) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	// TODO methode kann später mal entfernt werden
	private void testOutput(HashMap<Integer, Integer> readedPixel) {
		Set<Integer> keyset = readedPixel.keySet();
		Color curcolor = new Color();
		for (int currentkey : keyset) {
			Color.rgba8888ToColor(curcolor, currentkey);
			System.out.println("Farbe int-Wert: " + currentkey + " value: "
					+ readedPixel.get(currentkey) + " a: " + curcolor.a
					+ " r: " + curcolor.r + " g: " + curcolor.g + " b: "
					+ curcolor.b);
		}
	}

}