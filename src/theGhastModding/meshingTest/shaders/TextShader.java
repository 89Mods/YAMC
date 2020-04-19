package theGhastModding.meshingTest.shaders;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class TextShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "res/shaders/text/vertexShader.txt";
	private static final String FRAGMENT_FILE = "res/shaders/text/fragmentShader.txt";
	
	private int location_color;
	private int location_translation;
	
	public TextShader() throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
		location_translation = super.getUniformLocation("translation");
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	public void loadColor(Vector3f color){
		super.loadVector(location_color, color);
	}
	
	public void loadTranslation(Vector2f translation){
		super.loadVector(location_translation, translation);
	}
	
}