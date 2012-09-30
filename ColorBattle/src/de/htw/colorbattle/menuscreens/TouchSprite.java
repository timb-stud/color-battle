package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

/**
 * This class provides a touchable Sprite.
 * It extends the libgdx Sprite class and adds touch functionality
 */
public class TouchSprite extends Sprite implements InputProcessor {
	private OrthographicCamera camera;
	public boolean isTouched = false;
	public boolean highlightOnTouch;
	private Texture highlightTexture = null;
	private Texture buttonTexture;
	private Sound click;
	
	/* 
	 * Constructor
	 * @param Texture needed to crate the sprite
	 * @param OrthographicCamera to transform native touch coordinates on the Gamearea
	 */
	public TouchSprite(Texture t, OrthographicCamera camera) {
		super(t);
		this.buttonTexture = t;
		this.camera = camera;
		this.highlightOnTouch = false;
		this.click = Gdx.audio.newSound(Gdx.files.internal("sound/click.mp3"));
	}
	
	/*
	 * Constructor
	 * @param Filehandle Path to File that will be used as Texture
	 * @param OrthographicCamera to transform native touch coordinates on the Gamearea
	 */
	public TouchSprite(FileHandle f, OrthographicCamera camera) {
		super(new Texture(f));
		this.buttonTexture = this.getTexture();
		this.camera = camera;
		this.click = Gdx.audio.newSound(Gdx.files.internal("sound/click.mp3"));
	}
	
	/*
	 * Constructor
	 * @param Filehandle Path to File that will be used as Texture
	 */
	public TouchSprite(FileHandle f) {
		super(new Texture(f));
	}
	
	/*
	 * Method to set another picture to simualate the click effect
	*/
	public void setTouchDownPicture(FileHandle f) {
		highlightTexture = new Texture(f);
	}
	
	/*
	 * Get the current resolution from the OrthographicCamera
	 */
	private Vector3 transformCoordinates(int x, int y) {
		Vector3 touchPos = new Vector3();
	    touchPos.set(x, y, 0);
	    camera.unproject(touchPos);
	    return touchPos;
	}
	
	/*
	 * set the touched state
	 */
	public void setIsTouched(boolean touch){
		this.isTouched = touch;
	}
	
	/*
	 * returned the touched state
	 */
	public boolean isTouched() {
		return isTouched;
	}
	
	/*
	 * reset the touched state
	 */
	public void resetIsTouched() {
		if(click != null)
			click.play();
		isTouched = false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#keyDown(int)
	 */
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#keyUp(int)
	 */
	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#keyTyped(char)
	 */
	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	/*
	 * @see com.badlogic.gdx.InputProcessor#touchDown(int, int, int, int)
	 * transform coordinates of the touch event and check it hits the sprite
	 * if highlight boolean set to true, sprite will be highlight on touchDown
	 */
	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		Vector3 touchPos = transformCoordinates(x, y);
		if (this.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
			if (highlightOnTouch) {
				if (this.highlightTexture != null) {
					this.setTexture(this.highlightTexture);
				} else {
					this.setColor(Color.RED);
				}
			}
			return true;
		}
		return false;
	}

	/*
	 * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
	 * transform coordinates of the touch event and check it hits the sprite
	 * if highlight boolean set to true, highlight will be reseted
	 */
	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		Vector3 touchPos = transformCoordinates(x, y);
		if (this.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
			if (highlightOnTouch) {
				if (this.highlightTexture != null) {
					this.setTexture(this.buttonTexture);
				} else {
					this.setColor(Color.WHITE);
				}
			}
			isTouched = true;
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
	 */
	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#touchMoved(int, int)
	 */
	@Override
	public boolean touchMoved(int x, int y) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#scrolled(int)
	 */
	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	/*
	 * dispose the used textures
	 */
	public void disposeTouchSprite(){
		if (highlightTexture != null){
			highlightTexture.dispose();
		}
		if (buttonTexture != null){
			buttonTexture.dispose();
		}
		if(click != null)
			click.dispose();
	}
}