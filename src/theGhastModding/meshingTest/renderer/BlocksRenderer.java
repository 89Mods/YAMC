package theGhastModding.meshingTest.renderer;

import java.util.List;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import theGhastModding.meshingTest.maths.Maths;
import theGhastModding.meshingTest.object.Camera;
import theGhastModding.meshingTest.resources.BaseModel;
import theGhastModding.meshingTest.resources.textures.BlockTexturemap;
import theGhastModding.meshingTest.shaders.BlockShader;
import theGhastModding.meshingTest.world.ChunkMesh;
import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.WorldMesher;
import theGhastModding.meshingTest.world.blocks.Block;

public class BlocksRenderer {
	
	private BlockShader shader;
	
	private BaseModel preview[] = null;
	
	BlocksRenderer() throws Exception {
		shader = new BlockShader();
		MasterRenderer.enableCulling();
		shader.start();
		shader.loadProjectionMatrix(MasterRenderer.projectionMatrix);
		shader.stop();
	}
	
	private List<ChunkMesh> allMeshes = null;
	
	public void render(Camera camera, WorldMesher mesher, BlockTexturemap texture, int stereoMode, World world, Vector3f sunColor) throws Exception {
		if(preview == null) {
			preview = new BaseModel[Block.allBlocks.length];
			for(int i = 0; i < preview.length; i++) {
				if(Block.allBlocks[i] != null) {
					preview[i] = mesher.getBlockPreview(i, texture);
				}
			}
		}
		
		MasterRenderer.disableCulling();
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadSunlightStrength(world.getSunlightStrength());
		shader.loadSun(sunColor);
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
		
		shader.stop();
		shader.start();
		
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		shader.clearViewMatrix();
		shader.loadSunlightStrength(15.0f);
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
	
	public void cleanUp(){
		shader.cleanUp();
	}
	
}