package theGhastModding.meshingTest.shaders.post;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import theGhastModding.meshingTest.main.MainGameLoop;
import theGhastModding.meshingTest.renderer.MasterRenderer;
import theGhastModding.meshingTest.shaders.ShaderProgram;

public class PostprocessingQueue extends ShaderProgram {
	
	private static final String VERTEX_FILE = "res/shaders/post/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "res/shaders/post/simpleFragment.txt";
	
	private List<Postprocessor> postprocessors;
	
	private int finalWidth,finalHeight;
	
	public PostprocessingQueue(long window) throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
		this.postprocessors = new ArrayList<Postprocessor>();
		Dimension d = MainGameLoop.getWindowSize(window);
		this.finalWidth = d.width;
		this.finalHeight = d.height;
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	@Override
	protected void getAllUniformLocations() {
		
	}
	
	public void apply(int texture) {
		int prevTexture = texture;
		for(int i = 0; i < postprocessors.size(); i++) {
			postprocessors.get(i).applyEffect(prevTexture);
			prevTexture = postprocessors.get(i).getOutputTexture();
		}
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, finalWidth, finalHeight);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(MasterRenderer.CLEAR_RED, MasterRenderer.CLEAR_GREEN, MasterRenderer.CLEAR_BLUE, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		this.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTexture);
		GL30.glBindVertexArray(Postprocessor.fboModel.getId()); //TODO: Handle the way this model is used differently
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, Postprocessor.fboModel.getVertexCount());
		GL11.glFinish();
		this.stop();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL13.glActiveTexture(0);
		GL30.glBindVertexArray(0);
	}
	
	public void addPostprocessor(Postprocessor p) {
		postprocessors.add(p);
	}
	
	public void cleanUp() {
		for(Postprocessor p:postprocessors) p.cleanUp();
	}
	
}