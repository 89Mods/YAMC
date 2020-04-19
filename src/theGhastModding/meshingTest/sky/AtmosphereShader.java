package theGhastModding.meshingTest.sky;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import theGhastModding.meshingTest.maths.Maths;
import theGhastModding.meshingTest.object.Camera;
import theGhastModding.meshingTest.shaders.ShaderProgram;

public class AtmosphereShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "res/shaders/atmosphere/vertexShader.txt";
	private static final String FRAGMENT_FILE = "res/shaders/atmosphere/fragmentShader.txt";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_transformationMatrix;
	private int location_sunPosition;
	private int location_sunColor;
	private int location_planetPosition;
	private int location_cameraPosition;
	private int location_actualPlanetRadius;
	private int location_planetRadius;
	private int location_atmoRadius;
	private int location_Hr;
	private int location_Hm;
	private int location_betaR;
	private int location_betaM;
	private int location_mieG;
			
	public AtmosphereShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "normal");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix =     super.getUniformLocation("projectionMatrix");
		location_viewMatrix =           super.getUniformLocation("viewMatrix");
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_sunPosition =          super.getUniformLocation("sunPosition");
		location_sunColor =             super.getUniformLocation("sunColor");
		location_planetPosition =       super.getUniformLocation("planetPosition");
		location_cameraPosition =       super.getUniformLocation("cameraPosition");
		location_actualPlanetRadius =   super.getUniformLocation("actualPlanetRadius");
		location_planetRadius =         super.getUniformLocation("planetRadius");
		location_atmoRadius =           super.getUniformLocation("atmoRadius");
		location_Hr =                   super.getUniformLocation("Hr");
		location_Hm =                   super.getUniformLocation("Hm");
		location_betaR =                super.getUniformLocation("betaR");
		location_betaM =                super.getUniformLocation("betaM");
		location_mieG =                 super.getUniformLocation("mieG");
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
	
	public void loadSun(Vector3f sunPos, Vector3f sunColor) {
		super.loadVector(location_sunPosition, sunPos);
		super.loadVector(location_sunColor,    sunColor);
	}
	
	public void loadPlanetInfo(Camera c, float planetRadius) {
		super.loadVector(location_planetPosition,    new Vector3f(c.getPosition().x, -planetRadius - 100f, c.getPosition().z));
		super.loadFloat(location_actualPlanetRadius, planetRadius);
		super.loadVector(location_cameraPosition, c.getPosition());
	}
	
	private Vector3f a = new Vector3f();
	
	public void loadAtmosphere(Atmosphere atmo, float planetRadius) {
		float scale = 1f;
		super.loadFloat(location_planetRadius, atmo.planetRadius);
		super.loadFloat(location_atmoRadius,   atmo.atmoRadius);
		super.loadFloat(location_Hr,           atmo.Hr / scale);
		super.loadFloat(location_Hm,           atmo.Hm / scale);
		atmo.betaR.mul(scale, a);
		super.loadVector(location_betaR,       a);
		atmo.betaM.mul(scale, a);
		super.loadVector(location_betaM,       a);
		super.loadFloat(location_mieG,         atmo.mieG);
	}
	
}