package theGhastModding.meshingTest.resources.textures;

import java.awt.image.BufferedImage;

import org.joml.Vector2f;

import theGhastModding.meshingTest.resources.Loader;

public class BlockTexturemap {
	
	private int textureID;
	private int textureWidth;
	private int textureHeight;
	private int textureBlockDim;
	private int blocksPerRow;
	
	public BlockTexturemap(String texturePath, int textureWidth, int textureHeight, int textureBlockDim) throws Exception {
		this.textureID = Loader.loadTextureFromFile(texturePath);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.textureBlockDim = textureBlockDim;
		this.blocksPerRow = textureWidth / textureBlockDim;
	}
	
	public BlockTexturemap(BufferedImage texture, int textureBlockDim) throws Exception {
		this.textureID = Loader.loadTextureFromBufferedImage(texture);
		this.textureWidth = texture.getWidth();
		this.textureHeight = texture.getHeight();
		this.textureBlockDim = textureBlockDim;
		this.blocksPerRow = textureWidth / textureBlockDim;
	}
	
	public BlockTexturemap(int textureID, int textureWidth, int textureHeight, int textureBlockDim) {
		this.textureID = textureID;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.textureBlockDim = textureBlockDim;
		this.blocksPerRow = textureWidth / textureBlockDim;
	}
	
	public Vector2f textureLocationToCoordinates(int textureLocation) {
		return new Vector2f((textureLocation % blocksPerRow) * (float)textureBlockDim, (textureLocation / blocksPerRow) * (float)textureBlockDim);
	}
	
	public int getTextureID() {
		return this.textureID;
	}
	
	public int getTextureWidth() {
		return this.textureWidth;
	}
	
	public int getTextureHeight() {
		return this.textureHeight;
	}
	
	public int getTextureBlockDim() {
		return this.textureBlockDim;
	}
	
	public int getBlocksPerRow() {
		return this.blocksPerRow;
	}
	
}