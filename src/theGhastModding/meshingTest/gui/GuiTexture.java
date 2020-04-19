package theGhastModding.meshingTest.gui;

import org.joml.Vector2f;

public class GuiTexture {
	
	private int texture;
	private Vector2f position;
	private Vector2f scale;
	
	private boolean hidden;
	
	public GuiTexture(int texture, Vector2f position, Vector2f scale) {
		super();
		this.texture = texture;
		this.position = position;
		this.scale = scale;
		this.hidden = false;
	}
	
	public int getTexture() {
		return texture;
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public Vector2f getScale() {
		return scale;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public boolean isHidden() {
		return this.hidden;
	}
	
}