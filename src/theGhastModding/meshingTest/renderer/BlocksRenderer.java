package theGhastModding.meshingTest.renderer;

import java.awt.Dimension;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import theGhastModding.meshingTest.main.MainGameLoop;
import theGhastModding.meshingTest.maths.Maths;
import theGhastModding.meshingTest.object.Camera;
import theGhastModding.meshingTest.resources.BaseModel;
import theGhastModding.meshingTest.resources.textures.BlockTexturemap;
import theGhastModding.meshingTest.shaders.BlockShader;
import theGhastModding.meshingTest.world.ChunkMesh;
import theGhastModding.meshingTest.world.WorldMesher;
import theGhastModding.meshingTest.world.blocks.Block;

public class BlocksRenderer {
	
	private BlockShader shader;
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = (float)Math.sqrt(256.0 * 256.0 * 2.0);
	public static Matrix4f projectionMatrix;
	
	private static final float RED = 0.1f;
	private static final float GREEN = 148f / 256f;
	private static final float BLUE = 1f;
	
	private BaseModel preview[] = null;
	
	public BlocksRenderer(long window) throws Exception {
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
	
	private List<ChunkMesh> allMeshes = null;
	
	public void render(Camera camera, WorldMesher mesher, BlockTexturemap texture) throws Exception {
		if(preview == null) {
			preview = new BaseModel[Block.allBlocks.length];
			for(int i = 0; i < preview.length; i++) {
				if(Block.allBlocks[i] != null) {
					preview[i] = mesher.getBlockPreview(i, texture);
				}
			}
		}
		prepare();
		enableCulling();
		shader.start();
		shader.loadViewMatrix(camera);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
		shader.loadTransformationMatrix(Maths.createTransformationMatrix(new Vector3f(0f,0f,0f), new Vector3f(0f,0f,0f), 1.0f));
		shader.loadSelectedPosition(new Vector3f((int)camera.selectedX, (int)camera.selectedY, (int)camera.selectedZ));
		if(allMeshes == null || mesher.newMeshesAvailable) allMeshes = mesher.getMeshes();
		for(ChunkMesh mesh:allMeshes) {
			if(mesh == null || mesh.isEmpty()) continue;
			GL30.glBindVertexArray(mesh.getModel().getId());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL20.glEnableVertexAttribArray(3);
			GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
		
		shader.clearViewMatrix();
		shader.loadSelectedPosition(new Vector3f(3000.0f, 3000.0f, 3000.0f));
		shader.loadTransformationMatrix(Maths.createTransformationMatrix(new Vector3f(1.25f,1.72f,-2f), new Vector3f(25f, 28f, 13f), 0.125f));
		GL30.glBindVertexArray(preview[camera.selectedBlock].getId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL11.glDrawElements(GL11.GL_TRIANGLES, preview[camera.selectedBlock].getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		
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