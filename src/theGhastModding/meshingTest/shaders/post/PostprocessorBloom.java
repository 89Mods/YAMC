package theGhastModding.meshingTest.shaders.post;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class PostprocessorBloom extends Postprocessor {
	
	private static final String VERTEX_FILE = "res/shaders/post/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "res/shaders/post/bloom/fragment.txt";
	
	private PostprocessorDetectBrightness postBrightness;
	private PostprocessorContrast contrast;
	private PostprocessorHorizontalBlur hBlur1,hBlur2,hBlur3;
	private PostprocessorVerticalBlur vBlur1,vBlur2,vBlur3;
	
	private int location_textureSampler;
	private int location_highlightSampler;
	private int location_bloomStrength;
	
	private float bloomStrength;
	
	public PostprocessorBloom(int outWidth, int outHeight) throws Exception {
		this(outWidth, outHeight, 1f);
	}
	
	public PostprocessorBloom(int outWidth, int outHeight, float bloomStrength) throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE, outWidth, outHeight);
		this.bloomStrength = bloomStrength;
		postBrightness = new PostprocessorDetectBrightness(outWidth / 2, outHeight / 2);
		contrast = new PostprocessorContrast(outWidth / 8, outHeight / 8, 0.3f);
		hBlur1 = new PostprocessorHorizontalBlur(outWidth / 2, outHeight / 2);
		vBlur1 = new PostprocessorVerticalBlur(outWidth / 2, outHeight / 2);
		hBlur2 = new PostprocessorHorizontalBlur(outWidth / 4, outHeight / 4);
		vBlur2 = new PostprocessorVerticalBlur(outWidth / 4, outHeight / 4);
		hBlur3 = new PostprocessorHorizontalBlur(outWidth / 8, outHeight / 8);
		vBlur3 = new PostprocessorVerticalBlur(outWidth / 8, outHeight / 8);
	}
	
	@Override
	public void applyEffect(int sourceTexture) {
		postBrightness.applyEffect(sourceTexture);
		hBlur1.applyEffect(postBrightness.getOutputTexture());
		vBlur1.applyEffect(hBlur1.getOutputTexture());
		hBlur2.applyEffect(vBlur1.getOutputTexture());
		vBlur2.applyEffect(hBlur2.getOutputTexture());
		hBlur3.applyEffect(vBlur2.getOutputTexture());
		vBlur3.applyEffect(hBlur3.getOutputTexture());
		contrast.applyEffect(vBlur3.getOutputTexture());
		
		this.start();
		this.loadTextureLocations();
		this.loadBloomStrength();
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, contrast.getOutputTexture());
		super.applyEffect(sourceTexture);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	@Override
	protected void getAllUniformLocations() {
		this.location_textureSampler = super.getUniformLocation("textureSampler");
		this.location_highlightSampler = super.getUniformLocation("highlightSampler");
		this.location_bloomStrength = super.getUniformLocation("bloomStrength");
	}
	
	private void loadTextureLocations() {
		super.loadInt(location_textureSampler, 0);
		super.loadInt(location_highlightSampler, 1);
	}
	
	private void loadBloomStrength() {
		super.loadFloat(location_bloomStrength, bloomStrength);
	}
	
	@Override
	public void cleanUp() {
		postBrightness.cleanUp();
		hBlur1.cleanUp();
		vBlur1.cleanUp();
		hBlur2.cleanUp();
		vBlur2.cleanUp();
		super.cleanUp();
	}
	
	public float getBloomStrength() {
		return bloomStrength;
	}
	
	public void setBloomStrength(float bloomStrength) {
		this.bloomStrength = bloomStrength;
	}
	
}