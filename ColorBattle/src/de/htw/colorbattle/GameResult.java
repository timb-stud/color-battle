package de.htw.colorbattle;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameResult {

	private static final int FILTER_COLOR_MIN_FREQUENCY = 100;

	public void getScoreScreen() {
	}

	public void computeScore() {

		byte[] bytePixelArray = ScreenUtils.getFrameBufferPixels(false);
		HashMap<Integer, Integer> pixelMap = bytes2PixelMap(bytePixelArray);

		pixelMap = filterMap(pixelMap);

		System.out.println("cS 1 size: " + pixelMap.size());
		System.out.println("cS 1" +pixelMap.toString());

		// COLOR
		// soll -33619454
		// 33 61 94 54
		// 2 2 -1 -3

		// showBluedecode();

		/*
		 * {-33488896=472, -33554176=152, -50135294=2675, -50461949=685,
		 * -501153792=126, -33619454=285154, -1=93074}
		 * 
		 * Color 2222 int: -65536, float -1.6947657E38 ,a: 1.0 ,g: 0.0 ,r: 0.0
		 * ,int float cast: -16842752 ,int rawn float cast: -16842752 ,rgba
		 * intout: 65535 Ausgelesenes: int: -33619454, intbits2float:
		 * -4.2370446E37 ,
		 */

		// int ausg = -33619454;

		// System.out.println("Ausgelesenes: int: " + ausg + ", intbits2float: "
		// + Float.intBitsToFloat(ausg) + " , ");

//		andereausgabe();

	}

	private HashMap<Integer, Integer> bytes2PixelMap(byte[] bytePixelArray) {
		int currentPixel;
		int pixelCount;
		HashMap<Integer, Integer> pixelMap = new HashMap<Integer, Integer>();

		boolean ausgegeben = false;

		for (int i = 0; i < bytePixelArray.length - 4; i = i + 4) {
			// System.out.println(bytePixelArray[i] + " "+
			// bytePixelArray[i + 1]+" "+bytePixelArray[i + 2]+
			// " "+bytePixelArray[i + 3]);
			// byte byteeins = bytePixelArray[i];
			// byte bytezwei = bytePixelArray[i+1];
			// byte bytedrei = bytePixelArray[i+2];
			// byte bytevier = bytePixelArray[i+3];
			currentPixel = (bytePixelArray[i] & 0xFF)
					| ((bytePixelArray[i + 1] & 0xFF) << 8)
					| ((bytePixelArray[i + 2] & 0xFF) << 16)
					| ((bytePixelArray[i + 3] & 0xFF) << 24);
			// System.out.println(currentPixel);

			// gib einmal blau aus
			if ((currentPixel == -33619454) && (ausgegeben == false)) {
				System.out.println(bytePixelArray[i] + " "
						+ bytePixelArray[i + 1] + " " + bytePixelArray[i + 2]
						+ " " + bytePixelArray[i + 3]);

				float currentPixelf = (bytePixelArray[i] & 0xFF)
						| ((bytePixelArray[i + 1] & 0xFF) << 8)
						| ((bytePixelArray[i + 2] & 0xFF) << 16)
						| ((bytePixelArray[i + 3] & 0xFF) << 24);
				System.out.println("floatpixel: " + currentPixelf);
				ausgegeben = true;
			}

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

	private HashMap<Integer, Integer> filterMap(HashMap<Integer, Integer> pixelMap) {
		Set<Integer> keyset = pixelMap.keySet();
		HashMap<Integer, Integer> neue = new HashMap<Integer, Integer>();
		int currentvalue;
		for (int currentkey : keyset) {
			currentvalue = pixelMap.get(currentkey);
			if (currentvalue > FILTER_COLOR_MIN_FREQUENCY) {
				// pixelMap.remove(currentkey);
				neue.put(currentkey, currentvalue);
			}
		}
		return neue;// pixelMap;
	}

	private void showBluedecode() {

		Color colorx = Color.BLUE;

		System.out.println("\n \n Color Blue int: " + colorx.toIntBits()
				+ "\n colorx.toFloatBits " + colorx.toFloatBits() + "\n ,a: "
				+ colorx.a + " ,g: " + colorx.g + " ,r: " + colorx.r
				+ "\n ,Float.floatToIntBits floatToIntBits: "
				+ Float.floatToIntBits(colorx.toFloatBits())
				+ "\n ,Float.floatToRawIntBits  floatToRawIntBits: "
				+ Float.floatToRawIntBits(colorx.toFloatBits())
				+ "\n ,rgba intout: " + Color.rgba8888(colorx) + "\n .rgb565 "
				+ Color.rgb565(colorx) + "\n rgb888" + Color.rgb888(colorx)
				+ "\n rgba4444" + Color.rgba4444(colorx) + "\n toString() "
				+ colorx.toString());

	}

	// -------------------- Versuch 2 --------------------------

	/**
	 * bekomm nur 0
	 * 
	 * @param colorFrameBuffer
	 */
	public void computeScorev2(FrameBuffer colorFrameBuffer) {

		// Pixmap pm = colorFrameBuffer.getColorBufferTexture()
		// .getTextureData().consumePixmap();
		// // liefert immer 0

		Pixmap pm = getScreenshot(0, 0, 800, 480, false); // liefert 33751037
		// für blau
		
		int currentPixel;
		int pixelCount;
		HashMap<Integer, Integer> pixelMap = new HashMap<Integer, Integer>();


		for (int i = 0; i < 800; i++) {
			for (int j = 0; j < 480; j++) {
				currentPixel = pm.getPixel(i, j);
				if (pixelMap.containsKey(currentPixel)) {
					pixelCount = pixelMap.get(currentPixel);
					pixelCount++;
					pixelMap.put(currentPixel, pixelCount);
				} else {
					pixelMap.put(currentPixel, 1);
				}

			}
		}
		pixelMap = filterMap(pixelMap);
		
		System.out.println("cS v2 size: " + pixelMap.size());
		System.out.println("cS v2: "+pixelMap.toString());

		/*int c = pm.getPixel(100, 100);
		int d = pm.getPixel(200, 200);
		int e = pm.getPixel(300, 300);
		System.out.println(c + " " + d + " " + e);*/

	}

	private Pixmap getScreenshot(int x, int y, int w, int h, boolean flipY) {
		Gdx.gl.glPixelStorei(GL10.GL_PACK_ALIGNMENT, 1);

		final Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);
		ByteBuffer pixels = pixmap.getPixels();
		Gdx.gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
				pixels);

		final int numBytes = w * h * 4;
		byte[] lines = new byte[numBytes];
		if (flipY) {
			final int numBytesPerLine = w * 4;
			for (int i = 0; i < h; i++) {
				pixels.position((h - i - 1) * numBytesPerLine);
				pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
			}
			pixels.clear();
			pixels.put(lines);
		} else {
			pixels.clear();
			pixels.get(lines);
		}

		return pixmap;
	}

	private void andereausgabe() {
		int read1 = -33619454;
		int read2 = 33751037;
		Color c1 = new Color();
		Color c2 = new Color();

		c1.r = ((read1 & 0xff000000) >>> 24) / 255f;
		c1.g = ((read1 & 0x00ff0000) >>> 16) / 255f;
		c1.b = ((read1 & 0x0000ff00) >>> 8) / 255f;
		c1.a = ((read1 & 0x000000ff)) / 255f;

		c2.r = ((read2 & 0xff000000) >>> 24) / 255f;
		c2.g = ((read2 & 0x00ff0000) >>> 16) / 255f;
		c2.b = ((read2 & 0x0000ff00) >>> 8) / 255f;
		c2.a = ((read2 & 0x000000ff)) / 255f;

		System.out.println("andereausgabe: " + c1.toString() + " "
				+ c2.toString());

	}
	
	
	
	public Texture paintfarbe(){
		andereausgabe();
		
		Color color = new Color();
		
		Color.rgba8888ToColor(color, -33619454);  // color aus v2
		
		Color blau = Color.BLUE;
		
		
		System.out.println(
				blau.a + " a " + color.a + "\n"+
				blau.b + " b " + color.b + "\n"+
				blau.g + " g " + color.g + "\n"+
				blau.r + " r " + color.r 
		);
		
		if (blau.equals(color)){
			System.out.println("glei");
		}
	
		
		Pixmap colorPixmap = new Pixmap(20* 2, 20* 2, Format.RGBA8888);
		colorPixmap.setColor(color);
		colorPixmap.fillCircle(20, 20, 20);
		Texture colorTexture = new Texture(colorPixmap);
		colorPixmap.dispose();
		return colorTexture;
		
		
	}
	
		

}