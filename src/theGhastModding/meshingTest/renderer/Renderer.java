package theGhastModding.meshingTest.renderer;

import java.awt.Dimension;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import theGhastModding.meshingTest.main.MainGameLoop;
import theGhastModding.meshingTest.object.Camera;
import theGhastModding.meshingTest.shaders.BlockShader;
import theGhastModding.meshingTest.util.Maths;
import theGhastModding.meshingTest.world.ChunkMesh;
import theGhastModding.meshingTest.world.WorldMesher;

public class Renderer {
	
	private BlockShader shader;
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 128;
	private Matrix4f projectionMatrix;
	
	private static final float RED = 0.1f;
	private static final float GREEN = 148f / 256f;
	private static final float BLUE = 1f;
	
	public Renderer(long window) throws Exception {
		shader = new BlockShader();
		enableCulling();
		createProjectionMatrix(window);
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void render(Camera camera, WorldMesher mesher, int texture) throws Exception {
		prepare();
		enableCulling();
		shader.start();
		shader.loadViewMatrix(camera);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		shader.loadTransformationMatrix(Maths.createTransformationMatrix(new Vector3f(0f,0f,0f), new Vector3f(0f,0f,0f), 1.0f));
		for(ChunkMesh mesh:mesher.getMeshes()) {
			if(mesh.isEmpty()) continue;
			GL30.glBindVertexArray(mesh.getModel().getId());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	private void prepare() throws Exception {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public void cleanUp(){
		shader.cleanUp();
	}
	
    private void createProjectionMatrix(long window){
    	Dimension screenSize = MainGameLoop.getWindowSize(window);
        float aspectRatio = (float) screenSize.width / (float) screenSize.height;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
 
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
        projectionMatrix.m33(0);
    }
	
}