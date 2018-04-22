package theGhastModding.meshingTest.object;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import theGhastModding.meshingTest.main.MainGameLoop;
import theGhastModding.meshingTest.world.World;

public class Camera {
	
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch;
	private float yaw;
	private float roll;
	private long window;
	
	private static final float RUN_SPEED = 0.02f;
	private static final float GRAVITY = -0.005f;
	private static final float JUMP_POWER = 0.5f;
	private static final float MOUSE_SENSITIVITY = 0.1f;
	private static final float STRAVE_SPEED = 0.02f;
	
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
		float distance = (currentSpeed * MainGameLoop.delta) / 1000;
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
		if(this.getPosition().y() > worldHeight) upwardsSpeed += (GRAVITY * MainGameLoop.delta) / 1000;
		move(-dx, upwardsSpeed, -dz);
		if(this.getPosition().y <= worldHeight){
			this.getPosition().y = worldHeight;
			isInAir = false;
		}
		distance = (currentStraveSpeed * MainGameLoop.delta) / 1000;
		dx = (float)(distance * Math.sin(Math.toRadians(-getYaw() + 90)));
		dz = (float)(distance * Math.cos(Math.toRadians(-getYaw() + 90)));
		move(dx, 0, dz);
		mouse();
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