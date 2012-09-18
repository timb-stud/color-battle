package de.htw.colorbattle.toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


/** TOAST like android CLASS
 * 
 * <p>you create it by<br>Toast render_toast = new Toast(7, 6);</p>
 * <p>then you feed it a new text<br>render_toast.makeText("test string", "font", Toast.COLOR_PREF.RED, Toast.STYLE.NORMAL, Toast.TEXT_POS.middle, Toast.TEXT_POS.middle, Toast.ETERNAL);</p>
 * <p>the font should lie in data/</p>
 * <p>and you game Renderer continue to call<br>render_toast.toaster();</p>
 *
 * */

public class Toast {

   public int max_toasts = 15; // default max_toasts
   public int margin = 6;

   public static final int VERY_SHORT = 1; // 1 second (hence it's fading a bit faster cause it disappears if blending < 0.15 if fade_mode is not ROUND)
   public static final int SHORT = 2;
   public static final int MED = 4;
   public static final int LONG = 8;
   public static final int ETERNAL = -1;

   /**
    * Color Prefered (green, red or blue)
    */
   public static interface COLOR_PREF {
      public static final int GREEN = 1;
      public static final int RED = 0;
      public static final int BLUE = 2;
   }

   /**
    * Text position definitions (middle, middle_down, middle_up, middle_left, middle_right)<br>
    * You can setup a custom value, then this value will be used to generate random on the three colors (see makeText code)
    */
   public static interface TEXT_POS {
      public static final float middle = 0;
      public static final float middle_down = -1;
      public static final float middle_up = -2;
      public static final float middle_left = -1;
      public static final float middle_right = -2;
   }

   /**
    * fading, blinking .. style
    *
    */
   public static interface STYLE {
      public static final int NORMAL = 0;
      public static final int ROUND = 1;
      public static final int PSYCOTIC = 2;
   }

   /** the sprite batch : used for the object instancied with new Toast */
   private SpriteBatch spriteBatch;
   private Pixmap [] pix_back = new Pixmap [max_toasts];
   private TextureRegion [] back = new TextureRegion [max_toasts];
   private BitmapFont [] font = new BitmapFont [max_toasts];
   private int [] font_width = new int [max_toasts];
   private int [] font_height = new int [max_toasts];
   private String [] string = new String [max_toasts];
   private float [] time = new float [max_toasts];
   private float [] blend = new float [max_toasts];
   private int [] fade_mode = new int [max_toasts];
   private float [] X = new float [max_toasts];
   private float [] Y = new float [max_toasts];
   private float [] R = new float [max_toasts];
   private float [] G = new float [max_toasts];
   private float [] B = new float [max_toasts];
   /** to count total number of texts living with the Toast */
   private int i = 0;
   /** direction (for ROUND mode that is blink by fading then staining) */
   private boolean [] direction = new boolean [max_toasts];


   /** A new Toast()
    * @param max_toasts maximum number of toast texts
    * @param margin margin to left between texts
    * */
   public Toast(int max_toasts, int margin) {
      this.max_toasts = max_toasts;
      this.margin = margin;
   }


   /**  Function to emulate an android like Toast : use toaster() FROM your game Renderer
    *  @param string string to display
    *  @param time time desired into seconds
    *  @param _font font desired, should lie in /data/"_font".png/fnt
    *  @param prefered_color color (see {@linkplain COLOR_PREF}) that is prefered (it will random for the 2 other colors, but for this one, it will stay at 1.0f)
    *  @param fade_mode fading, blinking .. style (see {@link TEXT_POS})
    *  @param x where we want the text on X coords
    *  @param y where we want the text on Y coords, it will offset by number of total texts to display in this Toast
    *  @param time_to_spend the time to diplay THIS text
    * */
   public boolean makeText(String string, String _font, int prefered_color, int fade_mode, float x, float y, float time_to_spend) {

      // no more than max_toasts texts
      if (i >= max_toasts)
         return false;

      spriteBatch = new SpriteBatch(); // a new spriteBatch for a new text

      // we load the desired font
      font[i] = new BitmapFont(Gdx.files.internal("data/"+_font+".fnt"), Gdx.files.internal("data/"+_font+".png"), false);

      font_width[i] = (int) (font[i].getBounds(string).width + margin); // width of the string + margin
      font_height[i] = (int) font[i].getLineHeight() + margin; // height of the string
      float x_bounds = font[i].getBounds(string).width / 2; // we center the string
      float y_bounds = font[i].getLineHeight() + ((font[i].getLineHeight()+margin) * (-i)); // we offset by number of line to display in this Toast

      float width = Gdx.graphics.getWidth(); // becare that all calculations are made here, so if screen display resolution change (it happens with android), Strings will display at wrong place
      float height = Gdx.graphics.getHeight(); // change this behaviour if you want, i only prefered to NOT make calculations IN the for-loop in toaster()
      // you could also implement on ApplicationListener:resize to bring a new call in there

      if (x == TEXT_POS.middle)
         X[i] = (width/2) - x_bounds;
      else if (x == TEXT_POS.middle_left)
         X[i] = (width/3) - x_bounds;
      else if (x == TEXT_POS.middle_right)
         X[i] = (width - (width/3)) -x_bounds;
      if (y == TEXT_POS.middle)
         Y[i] = (height/2) + y_bounds;
      else if (y == TEXT_POS.middle_down)
         Y[i] = (height/3) + y_bounds;
      else if (y == TEXT_POS.middle_up)
         Y[i] = (height - (height/3)) + y_bounds;

      // no more than 9 seconds (except for ETERNAL, see for-loop in toaster())
      if (time_to_spend >= 9)
         this.time[i] = 7f;

      this.string[i] = string;
      this.time[i] = time_to_spend;
      blend[i] = 1f;

      // Color TINT prefered
      java.util.Random r = new java.util.Random();
      switch (prefered_color) {
      case COLOR_PREF.RED: // RED prefered
         G[i] = r.nextFloat();
         B[i] = r.nextFloat();
         R[i] = 1.0f;
         break;
      case COLOR_PREF.GREEN: // GREEN prefered
         R[i] = r.nextFloat();
         B[i] = r.nextFloat();
         G[i] = 1.0f;
         break;
      case COLOR_PREF.BLUE: // BLUE prefered
         R[i] = r.nextFloat();
         G[i] = r.nextFloat();
         B[i] = 1.0f;
         break;
      default: //custom value : we take the custom value just to generate randomness
         R[i] = r.nextInt(prefered_color);
         G[i] = r.nextInt(prefered_color);
         B[i] = r.nextInt(prefered_color);
         break;
      }

      this.fade_mode[i] = fade_mode;

      // transparent background is just a pixmap with a color
      pix_back[i] = new Pixmap(next_power_of_two(font_width[i]), next_power_of_two(font_height[i]), Format.RGBA4444);
      pix_back[i].setColor(R[i]/2, G[i]/2, B[i]/2, 0.25f); // 1/4 transparency for backgroud, you could customize.. factorize well you know :)
      pix_back[i].fill();
      back[i] = new TextureRegion(new Texture(pix_back[i]), X[i], Y[i], font_width[i], font_height[i]);


      // OK to return, we increment i that is a global for the Toast object
      i++;
      return true;
   }


   /** to get a power of two number (for Texture), taken from http://acius2.blogspot.com/2007/11/calculating-next-power-of-2.html
    * @param val value to round on the next power of two
    * */
   private int next_power_of_two(int val) {
      val--;
      val = (val >> 1) | val;
      val = (val >> 2) | val;
      val = (val >> 4) | val;
      val = (val >> 8) | val;
      val = (val >> 16) | val;
      val++; // Val is now the next highest power of 2.
      return val;
   }


   /** Trash all, in order <u>to end an eternal toast</u> */
   public boolean trash_all() {
      // becare race-conditions with toaster (that will never happen with only one thread running, but if you have toaster calling while this for-loop is executing in another thread, it will crash)
      for (int j=0; j < i; j++) {
         font[j].dispose();
         pix_back[j].dispose();
      }
      spriteBatch.dispose();
      i = 0;
      return true;
   }


   /** This function has to be called <u>every</u> frame from your renderer */
   public void toaster() {
      if (spriteBatch != null) {
         spriteBatch.begin();
         for (int j=0; j < i; j++) {
            if (time[j] != ETERNAL)
               time[j] -= Gdx.graphics.getDeltaTime(); // we substrac time
            //we want to draw if some time remains - BUT NOT if NOT "ROUND" fade_mode OR some blend remains (just to avoid 'end blinking' in NORMAL fade_mode)
            if ((time[j] == ETERNAL ||time[j] > 0) && (fade_mode[j] == STYLE.ROUND || blend[j] > 0.15)) {

               switch (fade_mode[j]) { //different modes (you would generally use NORMAL with a timed toast)
               case STYLE.NORMAL:
                  blend[j] -= 0.01 / time[j]; // so it will just and only fade
                  break;

               case STYLE.ROUND:
                  if (direction[j] == true)
                     if (time[j] > 0)
                        blend[j] -= 0.01 * time[j]; //to speed up / down things with time remaining (because you can use ROUND even without using ETERNAL)
                     else
                        blend[j] -= 0.01;
                  else
                     if (time[j] > 0)
                        blend[j] += 0.01 * time[j];
                     else
                        blend[j] += 0.01;
                  if (blend[j] < 0.15) // we define direction (fade or reappear) for ROUND mode
                     direction[j] = false;
                  if (blend[j] > 0.95)
                     direction[j] = true;
                  break;

               case STYLE.PSYCOTIC:
                  blend[j] = (float) (0.15 + Math.random() * (0.95 - 0.15)); //randomness like eyes will love
                  break;
               }

               // we draw .. finally
               spriteBatch.draw(back[j], X[j]-(margin/2), Y[j]-(font_height[j])+(margin/2), font_width[j], font_height[j]);
               font[j].setColor(R[j], G[j], B[j], blend[j]);
               font[j].draw(spriteBatch, string[j], X[j], Y[j]);
            }
         }
         spriteBatch.end();
      }
   }

}