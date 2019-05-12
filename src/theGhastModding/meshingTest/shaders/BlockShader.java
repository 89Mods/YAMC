package theGhastModding.meshingTest.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import theGhastModding.meshingTest.maths.Maths;
import theGhastModding.meshingTest.object.Camera;

public class BlockShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "res/shaders/block/vertexShader.txt";
	private static final String FRAGMENT_FILE = "res/shaders/block/fragmentShader.txt";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_transformationMatrix;
	private int location_selectedPosition;
	
	public BlockShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "light");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_selectedPosition = super.getUniformLocation("selectedPosition");
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera){
		super.loadMatrix(location_viewMatrix, Maths.createViewMatrix(camera));
	}
	
	public void clearViewMatrix() {
		super.loadMatrix(location_viewMatrix, Maths.createStaticViewMatrix());
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadSelectedPosition(Vector3f pos) {
		super.loadVector(location_selectedPosition, pos);
	}
	
}