package theGhastModding.meshingTest.sky.celestials;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import theGhastModding.meshingTest.maths.Maths;
import theGhastModding.meshingTest.object.Camera;
import theGhastModding.meshingTest.renderer.MasterRenderer;

public class CelestialRenderer {
	
	private CelestialShader shader;
	
	public CelestialRenderer() throws Exception {
		shader = new CelestialShader();
		shader.start();
		shader.loadProjectionMatrix(MasterRenderer.skyProjectionMatrix);
		shader.stop();
		MasterRenderer.enableCulling();
	}
    
    public void render(Camera camera, List<CelestialBody> celestials, Vector3f sunPosition, Vector3f sunColor) throws Exception {
    	shader.start();
    	shader.loadViewMatrix(camera);
    	shader.loadSun(sunPosition, sunColor);
    	shader.stop();
    	for(int i = 0; i < celestials.size(); i++) {
    		CelestialBody body = celestials.get(i);
    		Matrix4f transformMatrix = Maths.createTransformationMatrix(body.getPos(), body.getRot(), 1.0f);
    		shader.start();
    		shader.loadTransformationMatrix(transformMatrix);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, body.getModel().getTexture().getTextureID());
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL30.glBindVertexArray(body.getModel().getModel().getId());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL11.glDrawElements(GL11.GL_TRIANGLES, body.getModel().getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
    		shader.stop();
    	}
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL13.glActiveTexture(0);
		
		GL30.glBindVertexArray(0);
    }
    
    public void cleanUp() {
    	shader.cleanUp();
    }
	
}