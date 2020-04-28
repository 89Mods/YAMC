package theGhastModding.meshingTest.shaders.post;

public class PostprocessorHorizontalBlur extends Postprocessor {
	
	private static final String VERTEX_FILE = "res/shaders/post/blur/horizontalVertex.txt";
	private static final String FRAGMENT_FILE = "res/shaders/post/blur/fragment.txt";
	
	private int location_targetWidth;
	
	public PostprocessorHorizontalBlur(int outWidth, int outHeight) throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE, outWidth, outHeight);
		this.start();
		this.loadTargetWidth(outWidth);
		this.stop();
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	@Override
	protected void getAllUniformLocations() {
		this.location_targetWidth = super.getUniformLocation("targetWidth");
	}
	
	public void loadTargetWidth(int width) {
		super.loadFloat(location_targetWidth, width);
	}
	
}