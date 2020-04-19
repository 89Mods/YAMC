package theGhastModding.meshingTest.sky.celestials;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import theGhastModding.meshingTest.maths.Maths;
import theGhastModding.meshingTest.object.Camera;
import theGhastModding.meshingTest.shaders.ShaderProgram;

public class CelestialShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "res/shaders/celestials/vertexShader.txt";
	private static final String FRAGMENT_FILE = "res/shaders/celestials/fragmentShader.txt";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_transformationMatrix;
	private int location_sunPosition;
	private int location_sunColor;
	
	public CelestialShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_sunPosition = super.getUniformLocation("sunPosition");
		location_sunColor = super.getUniformLocation("sunColor");
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
	
	public void loadSun(Vector3f sunPosition, Vector3f sunColor) {
		super.loadVector(location_sunPosition, sunPosition);
		super.loadVector(location_sunColor,    sunColor);
	}
	
}