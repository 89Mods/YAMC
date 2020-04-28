package theGhastModding.meshingTest.shaders.post;

public class PostprocessorContrast extends Postprocessor {
	
	private static final String VERTEX_FILE = "res/shaders/post/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "res/shaders/post/contrast/fragment.txt";
	
	private int location_contrast;
	private float contrast = 0.3f;
	
	public PostprocessorContrast(int outWidth, int outHeight, float contrast) throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE, outWidth, outHeight);
		this.contrast = contrast;
	}
	
	public PostprocessorContrast(int outWidth, int outHeight) throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE, outWidth, outHeight);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_contrast = super.getUniformLocation("contrast");
	}
	
	@Override
	public void applyEffect(int sourceTexture) {
		this.start();
		this.loadContrast();
		super.applyEffect(sourceTexture);
	}
	
	private void loadContrast() {
		super.loadFloat(location_contrast, contrast);
	}
	
	public float getContrast() {
		return this.contrast;
	}
	
	public void setContrast(float newContrast) {
		this.contrast = newContrast;
	}
	
}