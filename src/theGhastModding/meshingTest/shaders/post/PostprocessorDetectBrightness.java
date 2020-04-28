package theGhastModding.meshingTest.shaders.post;

public class PostprocessorDetectBrightness extends Postprocessor {
	
	private static final String VERTEX_FILE = "res/shaders/post/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "res/shaders/post/brightness/fragment.txt";
	
	public PostprocessorDetectBrightness(int outWidth, int outHeight) throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE, outWidth, outHeight);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	@Override
	protected void getAllUniformLocations() {
		
	}
	
}