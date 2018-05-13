package theGhastModding.meshingTest.object;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import theGhastModding.meshingTest.main.MainGameLoop;
import theGhastModding.meshingTest.renderer.BlocksRenderer;
import theGhastModding.meshingTest.util.Maths;
import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.blocks.Block;

public class Camera {
	
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch;
	private float yaw;
	private float roll;
	private long window;
	
	private static final float RUN_SPEED = 0.02f;
	private static final float GRAVITY = -0.0001f;
	private static final float JUMP_POWER = 0.025f;
	private static final float MOUSE_SENSITIVITY = 0.1f;
	private static final float STRAVE_SPEED = 0.02f;
	private static final int REACH_DISTANCE = 4;
	
	public boolean DEV_MODE = true;
	
	private float currentSpeed = 0;
	private float upwardsSpeed = 0;
	private float currentStraveSpeed = 0;
	private boolean isInAir = false;
	
	private World world;
	
	public Camera(long window, World world){
		this.window = window;
		this.world = world;
	}
	
	public void update(){
		checkInputs();
		float distance = (currentSpeed * MainGameLoop.delta) / 1000f;
		if(this.yaw >= 361){
			this.yaw += -360;
		}
		if(this.yaw <= -361){
			this.yaw += 360;
		}
		if(this.pitch >= 361){
			this.pitch += -360;
		}
		if(this.pitch <= -361){
			this.pitch += 360;
		}
		int worldHeight = world.getWorldHeightAt((int)this.position.x, (int)this.position.z);
		float dx = (float)(distance * Math.sin(Math.toRadians(-getYaw())));
		float dz = (float)(distance * Math.cos(Math.toRadians(-getYaw())));
		if(this.getPosition().y() > worldHeight) {
			upwardsSpeed += (GRAVITY * MainGameLoop.delta) / 1000;
			isInAir = true;
		}
		move(-dx, upwardsSpeed * (MainGameLoop.delta / 1000f), -dz);
		if(this.getPosition().y <= worldHeight){
			this.getPosition().y = worldHeight;
			isInAir = false;
		}
		distance = (currentStraveSpeed * MainGameLoop.delta) / 1000;
		dx = (float)(distance * Math.sin(Math.toRadians(-getYaw() + 90)));
		dz = (float)(distance * Math.cos(Math.toRadians(-getYaw() + 90)));
		move(dx, 0, dz);
		mouse();
		//Raycast to find the block we're currently looking at
		Matrix4f invertedView = Maths.createViewMatrix(this);
		invertedView.invert(invertedView);
		Vector4f clipCoords = new Vector4f(-0.5f, 0, -1.0f, 1.0f);
		Matrix4f invertedProjection = new Matrix4f();
		BlocksRenderer.projectionMatrix.invert(invertedProjection);
		invertedProjection.transform(clipCoords, clipCoords);
		clipCoords.z = -1.0f;
		clipCoords.w = 0.0f;
		Vector4f rayWorld = invertedView.transform(clipCoords);
		Vector3f mouseRay = new Vector3f(rayWorld.x(), rayWorld.y(), rayWorld.z());
		mouseRay.normalize(mouseRay);
		Vector3f testRay = new Vector3f();
		//System.out.println(mouseRay);
		testRay.set(mouseRay.x() + position.x(), mouseRay.y() + position.y(), mouseRay.z() + position.z());
		for(int i = 0; i < REACH_DISTANCE; i++) {
			if(world.getBlock((int)testRay.x(), (int)testRay.y() , (int)testRay.z()) != Block.air.getBlockID()) {
				break;
			}
			testRay.add(mouseRay, testRay);
		}
		//TestRay now contains the position of the selected block
		if(GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_1) == GL11.GL_TRUE) {
			world.setBlock((int)testRay.x(), (int)testRay.y(), (int)testRay.z(), Block.air.getBlockID());
		}
		if(GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_2) == GL11.GL_TRUE) {
			world.setBlock((int)testRay.x(), (int)testRay.y(), (int)testRay.z(), Block.iron.getBlockID());
			world.placeLightSource((int)testRay.x(), (int)testRay.y() + 1, (int)testRay.z(), 15);
		}
	}
	
	private void jump(){
		if(!DEV_MODE){
			if(!isInAir){ 
				this.upwardsSpeed = JUMP_POWER; 
				isInAir = true;
			}
		}else{
			this.upwardsSpeed = JUMP_POWER; 
			isInAir = true;
		}
	}
	
	public void move(float dx, float dy, float dz){
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	
	private void checkInputs(){
		if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GL11.GL_TRUE){
			this.currentSpeed = RUN_SPEED;
		}else if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GL11.GL_TRUE){
			this.currentSpeed = -RUN_SPEED;
		}else{
			this.currentSpeed = 0;
		}
		if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GL11.GL_TRUE){
			this.currentStraveSpeed = STRAVE_SPEED;
		}else if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GL11.GL_TRUE){
			this.currentStraveSpeed = -STRAVE_SPEED;
		}else{
			this.currentStraveSpeed = 0;
		}
		if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GL11.GL_TRUE){
			jump();
		}
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getRoll() {
		return roll;
	}
	
	private double lastDx = 0;
	private double lastDy = 0;
	
	private void mouse(){
		double[] a = new double[1];
		double[] b = new double[1];
		double dy = 0;
		double dx = 0;
		GLFW.glfwGetCursorPos(window, a, b);
		dx = a[0] - lastDx;
		dy = b[0] - lastDy;
		lastDx = a[0];
		lastDy = b[0];
		if(!MainGameLoop.cursorGrabbed) return;
		float pitchChange = -(float) (dy * (double)MOUSE_SENSITIVITY);
		pitch -= pitchChange;
		float yawChange = (float) (dx * (double)MOUSE_SENSITIVITY);
		yaw += yawChange;
	}
	
}