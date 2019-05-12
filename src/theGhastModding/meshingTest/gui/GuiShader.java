package theGhastModding.meshingTest.gui;

import org.joml.Matrix4f;

import theGhastModding.meshingTest.shaders.ShaderProgram;

public class GuiShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "res/shaders/gui/vertexShader.txt";
	private static final String FRAGMENT_FILE = "res/shaders/gui/fragmentShader.txt";
	
	private int location_transformationMatrix;
	
	public GuiShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadTransformation(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
}