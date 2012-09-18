package de.htw.colorbattle.menuscreens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

public class TouchSprite extends Sprite implements InputProcessor {
	private OrthographicCamera camera;
	public boolean isTouched = false;
	
	public TouchSprite(Texture t, OrthographicCamera camera) {
		super(t);
		this.camera = camera;
	}
	
	public TouchSprite(FileHandle f, OrthographicCamera camera) {
		super(new Texture(f));
		this.camera = camera;
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
	
	public void setIsTouched(boolean touch){
		this.isTouched = touch;
	}
	
	public TouchSprite(FileHandle f) {
		super(new Texture(f));
	}

	
	public boolean isTouched() {
		return isTouched;
	}
	
	public void resetIsTouched() {
		isTouched = false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		Vector3 touchPos = transformCoordinates(x, y);
		if (this.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
			this.setColor(Color.RED);
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		Vector3 touchPos = transformCoordinates(x, y);
		if (this.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
			this.setColor(Color.WHITE);
			isTouched = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}