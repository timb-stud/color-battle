package de.htw.colorbattle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class TouchSprite extends Sprite implements InputProcessor {
	private boolean isTouched = false;
	public TouchSprite(Texture t) {
		super(t);
	}
	
	/**
	 * 
	 * @param path to the Texture
	 */
	public TouchSprite(String path) {
		super(new Texture(Gdx.files.internal(path)));
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
		Gdx.app.log("touched", "touchDown");
		if (this.getBoundingRectangle().contains(x, Gdx.graphics.getHeight() - y)) {
			this.setColor(Color.RED);
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		Gdx.app.log("touched", "touchUp");
		if (this.getBoundingRectangle().contains(x, Gdx.graphics.getHeight() - y)) {
			isTouched = true;
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