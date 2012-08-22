package de.htw.colorbattle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

import de.htw.colorbattle.gameobjects.Player;

public class GameResult {

	private static final int FILTER_COLOR_MIN_FREQUENCY = 100;
	private int pixelNumber;
	private LinkedList<Player> playerList;

	// Graphics
	private static final int BAR_HEIGHT = 30;
	private static final int BAR_MAX_WIDTH = 400;

	private static final int WINDOW_HEIGHT = 200;
	private static final int WINDOW_WIDTH = 600;

	public GameResult(LinkedList<Player> playersList) {
		this.playerList = playersList;
		computeScore();
	}

	private void computeScore() {
		byte[] bytePixelArray = ScreenUtils.getFrameBufferPixels(false);
		this.pixelNumber = bytePixelArray.length / 4;
		HashMap<Integer, Integer> readedPixel = getPixelMap(bytePixelArray);
		readedPixel = filterAndInverseMap(readedPixel);
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

	private HashMap<Integer, Integer> filterAndInverseMap(
			HashMap<Integer, Integer> pixelMap) {
		Color currentColor = new Color();
		HashMap<Integer, Integer> newMap = new HashMap<Integer, Integer>();
		Set<Integer> keyset = pixelMap.keySet();
		int currentvalue;
		for (int currentkey : keyset) {
			currentvalue = pixelMap.get(currentkey);
			if (currentvalue > FILTER_COLOR_MIN_FREQUENCY) {
				Color.rgba8888ToColor(currentColor, currentkey);
				// das hier kann man ev optimieren
				newMap.put(currentColor.toIntBits(), currentvalue);
			}
		}
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
					pl.setGameScore(((double) currentvalue / (double) pixelNumber) * 100.0);
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
					(int) ((double) BAR_MAX_WIDTH / 100.0 * pl.getGameScore()),
					BAR_HEIGHT);
			tex = new Texture(pm);
			texList.add(tex);
		}
		return texList;
	}

	public Texture getScoreScreen(SpriteBatch batch) {

		Pixmap pm;
		Texture tex;
		FrameBuffer scoreFrameBuffer = new FrameBuffer(Format.RGBA8888,
				WINDOW_WIDTH, WINDOW_HEIGHT, false);
		// Attention Framebuffer flipped ?

		// Hintergrund
		pm = new Pixmap(WINDOW_WIDTH, WINDOW_HEIGHT, Format.RGBA8888);
		pm.setColor(Color.GRAY);
		pm.fillRectangle(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		tex = new Texture(pm);

		scoreFrameBuffer.begin();
		batch.begin();
		batch.draw(tex, 0, 0);

		int y = 0;
		for (Texture tex2 : this.getPlayerScoreTextures()) {
			batch.draw(tex2, 100, y);
			y += 40;
		}

		batch.end();
		scoreFrameBuffer.end();

		return scoreFrameBuffer.getColorBufferTexture();
	}

	public LinkedList<Player> getScoredPlayerList() {
		return playerList;
	}

}