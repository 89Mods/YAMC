package theGhastModding.meshingTest.shaders.post;

public class PostprocessorVerticalBlur extends Postprocessor {
	
	private static final String VERTEX_FILE = "res/shaders/post/blur/verticalVertex.txt";
	private static final String FRAGMENT_FILE = "res/shaders/post/blur/fragment.txt";
	
	private int location_targetHeight;
	
	public PostprocessorVerticalBlur(int outWidth, int outHeight) throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE, outWidth, outHeight);
		this.start();
		this.loadTargetHeight(outHeight);
		this.stop();
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	@Override
	protected void getAllUniformLocations() {
		this.location_targetHeight = super.getUniformLocation("targetHeight");
	}
	
	public void loadTargetHeight(int height) {
		super.loadFloat(location_targetHeight, height);
	}
	
}