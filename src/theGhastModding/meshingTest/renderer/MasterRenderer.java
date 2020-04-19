package theGhastModding.meshingTest.renderer;

import java.awt.Dimension;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import theGhastModding.meshingTest.main.MainGameLoop;
import theGhastModding.meshingTest.maths.Maths;
import theGhastModding.meshingTest.object.Camera;
import theGhastModding.meshingTest.resources.BaseModel;
import theGhastModding.meshingTest.resources.textures.BlockTexturemap;
import theGhastModding.meshingTest.sky.Atmosphere;
import theGhastModding.meshingTest.sky.AtmosphereShader;
import theGhastModding.meshingTest.sky.celestials.CelestialBody;
import theGhastModding.meshingTest.sky.celestials.CelestialRenderer;
import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.WorldMesher;

public class MasterRenderer {
	
	private AtmosphereShader skyShader;
	
	public static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = (float)Math.sqrt(256.0 * 256.0 * 2.0);
	public static Matrix4f projectionMatrix,skyProjectionMatrix;
	
	private static final float CLEAR_RED = 0f;
	private static final float CLEAR_GREEN = 0f;
	private static final float CLEAR_BLUE = 0f;
	
	private Atmosphere atmo = Atmosphere.DEFAULT;
	private BaseModel skydome;
	
	private BlocksRenderer blocksRenderer;
	private CelestialRenderer celestialRenderer;
	
	public MasterRenderer(long window) throws Exception {
		skyShader = new AtmosphereShader();
		enableCulling();
		createProjectionMatrix(window);
		createSkyProjectionMatrix(window);
		skyShader.start();
		skyShader.loadProjectionMatrix(skyProjectionMatrix);
		skyShader.stop();
		skydome = Atmosphere.generateSkydome(6420e3f);
		blocksRenderer = new BlocksRenderer();
		celestialRenderer = new CelestialRenderer();
	}
	
	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void render(Camera camera, WorldMesher mesher, BlockTexturemap texture, int stereoMode, World world, List<CelestialBody> celestials) throws Exception {
		prepare(stereoMode);
		
		double x = (world.getTimeOfDay() - 12000) / 24000.0D;
		x *= 2.0 * Math.PI;
		double sunStrength = Math.max(0, Math.min(1, Math.sin(x) * 1.5));
		world.setSunlightStrength((float)sunStrength * 15.0f);
		
		Vector3f sunPosition = new Vector3f(-10000000000000.0f * (float)Math.cos(world.getTimeOfDay() / 24000.0f * 2.0f * (float)Math.PI),-10000000000000.0f * (float)Math.sin(world.getTimeOfDay() / 24000.0f * 2.0f * (float)Math.PI),0f);
		Vector3f sunColor = new Vector3f(1f,1f,1f);
		
		enableCulling();
		celestialRenderer.render(camera, celestials, sunPosition, sunColor);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		disableCulling();
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE);
		skyShader.start();
		skyShader.loadViewMatrix(camera);
		skyShader.loadTransformationMatrix(Maths.createTransformationMatrix(new Vector3f(camera.getPosition().x, -6360e3f - 100f,camera.getPosition().z), new Vector3f(0f,0f,0f), 1.0f));
		skyShader.loadPlanetInfo(camera, 6360e3f);
		skyShader.loadSun(sunPosition, sunColor);
		skyShader.loadAtmosphere(atmo, 6360e3f);
		GL30.glBindVertexArray(skydome.getId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL11.glDrawElements(GL11.GL_TRIANGLES, skydome.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		skyShader.stop();
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
		
		blocksRenderer.render(camera, mesher, texture, stereoMode, world, sunColor);
		GL11.glFinish();
	}
	
	private void prepare(int stereoMode) throws Exception {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		
		if(stereoMode == 1) {
			GL11.glColorMask(true, false, false, false);
		}else if(stereoMode == 2) {
			GL11.glColorMask(false, true, true, false);
		}
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor(CLEAR_RED, CLEAR_GREEN, CLEAR_BLUE, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
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
    
    private void createSkyProjectionMatrix(long window) {
    	float far = 10000e3f;
    	Dimension screenSize = MainGameLoop.getWindowSize(window);
        float aspectRatio = (float) screenSize.width / (float) screenSize.height;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = far - NEAR_PLANE;
 
        skyProjectionMatrix = new Matrix4f();
        skyProjectionMatrix.m00(x_scale);
        skyProjectionMatrix.m11(y_scale);
        skyProjectionMatrix.m22(-((far + NEAR_PLANE) / frustum_length));
        skyProjectionMatrix.m23(-1);
        skyProjectionMatrix.m32(-((2 * NEAR_PLANE * far) / frustum_length));
        skyProjectionMatrix.m33(0);
    }
    
    public void cleanUp() {
    	skyShader.cleanUp();
    	blocksRenderer.cleanUp();
    	celestialRenderer.cleanUp();
    }
	
}