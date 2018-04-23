package theGhastModding.meshingTest.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import theGhastModding.meshingTest.util.AdvancedBufferUtils;
import theGhastModding.meshingTest.world.ChunkMesh;

public class Loader {
	
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	public BaseModel loadToVAO(float[] positions, int[] indices, float[] textureCoords, float[] normals) throws Exception {
		if(positions.length % 3 != 0) throw new LoaderException("Invalid vertex count");
		if(indices.length % 3 != 0) throw new LoaderException("Invalid indices count");
		if(textureCoords.length % 2 != 0) throw new LoaderException("Invalid texture coordinate count");
		if(normals.length % 3 != 0) throw new LoaderException("Invalid texture coordinate count");
		int vaoID = createVAO();
		bindIndices(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		return new BaseModel(vaoID, indices.length);
	}
	
	public ChunkMesh loadChunkMesh(float[] positions, int[] indices, float[] textureCoords, float[] normals, float[] lightLevels) throws Exception {
		if(positions.length % 3 != 0) throw new LoaderException("Invalid vertex count");
		if(indices.length % 3 != 0) throw new LoaderException("Invalid indices count");
		if(textureCoords.length % 2 != 0) throw new LoaderException("Invalid texture coordinate count");
		if(normals.length % 3 != 0) throw new LoaderException("Invalid texture coordinate count");
		int vaoID = createVAO();
		bindIndices(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		storeDataInAttributeList(3, 1, lightLevels);
		unbindVAO();
		return new ChunkMesh(new BaseModel(vaoID, indices.length), false);
	}
	
	public BaseModel loadToVAO(float[] positions) throws Exception {
		if(positions.length % 2 != 0) throw new LoaderException("Invalid vertex count");
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, 2, positions);
		unbindVAO();
		return new BaseModel(vaoID, positions.length / 2);
	}
	
	public void updateToVbo(int vbo, float[] data, FloatBuffer buffer) throws Exception {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		unbindVBO(GL15.GL_ARRAY_BUFFER);
	}
	
	public int loadToVAO(float[] positions, float[] textureCoords) throws Exception {
		if(positions.length % 2 != 0) throw new LoaderException("Invalid vertex count");
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, 2, positions);
		this.storeDataInAttributeList(1, 2, textureCoords);
		unbindVAO();
		return vaoID;
	}
	
	public int loadTextureFromBufferedImage(BufferedImage bi) throws Exception {
		int textureID;
		int width = bi.getWidth();
		int height = bi.getHeight();
		
		int[] pixels_raw = new int[width * height * 4];
		pixels_raw = bi.getRGB(0, 0, width, height, null, 0, width);
		
		
		ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
		
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				int pixel = pixels_raw[i * width + j];
				pixels.put((byte)((pixel >> 16) & 0xFF)); //R
				pixels.put((byte)((pixel >> 8) & 0xFF)); //G
				pixels.put((byte)(pixel & 0xFF)); //B
				pixels.put((byte)((pixel >> 24) & 0xFF)); //A
			}
		}
		
		pixels.flip();
		
		textureID = GL11.glGenTextures();
		textures.add(textureID);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		//GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, GL11.GL_NONE, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		if(GL.getCapabilities().GL_EXT_texture_filter_anisotropic){
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
			float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
		}else{
			System.out.println("Anisotropic filtering not supported");
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.5f);
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		return textureID;
	}
	
	public int loadTextureFromFile(String fileName) throws Exception {
		int textureID;
		BufferedImage bi;
		bi = ImageIO.read(new File(fileName));
		int width = bi.getWidth();
		int height = bi.getHeight();
		
		int[] pixels_raw = new int[width * height * 4];
		pixels_raw = bi.getRGB(0, 0, width, height, null, 0, width);
		
		
		ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
		
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				int pixel = pixels_raw[i * width + j];
				pixels.put((byte)((pixel >> 16) & 0xFF)); //R
				pixels.put((byte)((pixel >> 8) & 0xFF)); //G
				pixels.put((byte)(pixel & 0xFF)); //B
				pixels.put((byte)((pixel >> 24) & 0xFF)); //A
			}
		}
		
		pixels.flip();
		
		textureID = GL11.glGenTextures();
		textures.add(textureID);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		//GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, GL11.GL_NONE, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
		//GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		if(GL.getCapabilities().GL_EXT_texture_filter_anisotropic){
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
			float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
		}else{
			System.out.println("Anisotropic filtering not supported");
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.5f);
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		return textureID;
	}
	
	public void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength, int offset){
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	private int createVAO() throws Exception {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private int createVBO(int type) throws Exception {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(type, vboID);
		return vboID;
	}
	
	public int createVBO_S(int floatCount) throws Exception {
		int vboID = createVBO(GL15.GL_ARRAY_BUFFER);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
		unbindVBO(GL15.GL_ARRAY_BUFFER);
		return vboID;
	}
	
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) throws Exception {
		createVBO(GL15.GL_ARRAY_BUFFER);
		FloatBuffer buffer = AdvancedBufferUtils.storeDataInBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		unbindVBO(GL15.GL_ARRAY_BUFFER);
	}
	
	private void unbindVAO() throws Exception {
		GL30.glBindVertexArray(0);
	}
	
	private void unbindVBO(int type) throws Exception {
		GL15.glBindBuffer(type, 0);
	}
	
	private void bindIndices(int[] indices) throws Exception {
		createVBO(GL15.GL_ELEMENT_ARRAY_BUFFER);
		IntBuffer buffer = AdvancedBufferUtils.storeDataInBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	public void cleanUp() throws Exception {
		for(Integer i:vaos){
			GL30.glDeleteVertexArrays(i);
		}
		for(Integer i:vbos){
			GL15.glDeleteBuffers(i);
		}
		for(Integer i:textures){
			GL11.glDeleteTextures(i);
		}
	}
	
}