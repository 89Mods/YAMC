package theGhastModding.meshingTest.shaders.post;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import theGhastModding.meshingTest.renderer.MasterRenderer;
import theGhastModding.meshingTest.resources.BaseModel;
import theGhastModding.meshingTest.resources.Loader;
import theGhastModding.meshingTest.shaders.ShaderProgram;

public abstract class Postprocessor extends ShaderProgram {
	
	//Have all of these use the same FBO/RBO, but a different texture.
	private static final float[] POSITIONS = {-1, -1, 1, -1, -1, 1, 1, -1, 1, 1, -1, 1};
	private static final float[] TEXT_COORDS = {0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, 1f, 1f, 0f, 1f};
	public static BaseModel fboModel = null;
	protected int outputTexture;
	protected int fbo,rbo;
	protected int outWidth,outHeight;
	
	public Postprocessor(String vertexFile, String fragmentFile, int outWidth, int outHeight) throws Exception {
		super(vertexFile, fragmentFile);
		this.outWidth = outWidth;
		this.outHeight = outHeight;
		if(fboModel == null) {
			fboModel = Loader.loadToVAOT(POSITIONS, TEXT_COORDS);
		}
		outputTexture = FramebufferUtils.genFramebufferTexture(outWidth, outHeight);
		long fborbo = FramebufferUtils.genFramebufferRenderbuffer(outWidth, outHeight, outputTexture);
		fbo = (int)(fborbo & 0xFFFFFFFFL);
		rbo = (int)((fborbo >>> 32L) & 0xFFFFFFFFL);
	}
	
	public void applyEffect(int sourceTexture) {
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		GL11.glViewport(0, 0, outWidth, outHeight);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(MasterRenderer.CLEAR_RED, MasterRenderer.CLEAR_GREEN, MasterRenderer.CLEAR_BLUE, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		this.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, sourceTexture);
		GL30.glBindVertexArray(fboModel.getId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, fboModel.getVertexCount());
		GL11.glFinish();
		this.stop();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL13.glActiveTexture(0);
		GL30.glBindVertexArray(0);
	}
	
	public int getOutputTexture() {
		return this.outputTexture;
	}
	
}