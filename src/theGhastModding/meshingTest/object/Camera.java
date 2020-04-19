package theGhastModding.meshingTest.object;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import theGhastModding.meshingTest.main.MainGameLoop;
import theGhastModding.meshingTest.phys.AABB;
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
	private static final int REACH_DISTANCE = 6;
	
	public boolean DEV_MODE = true;
	
	private float currentSpeed = 0;
	private float upwardsSpeed = 0;
	private float currentStraveSpeed = 0;
	private boolean isInAir = false;
	
	private World world;
	
	private AABB hitbox = new AABB(0.1f, 0.1f, 0.1f, 0.9f, 1.9f, 0.9f);
	
	public int selectedBlock = 1;
	public float selectedX,selectedY,selectedZ;
	
	public Camera(long window, World world){
		this.window = window;
		this.world = world;
	}
	
	public void update(){
		checkInputs();
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
		
		MainGameLoop.delta = Math.min(MainGameLoop.delta, 18000);
		float distance = (currentSpeed * MainGameLoop.delta) / 1000f;
		float distances = (currentStraveSpeed * MainGameLoop.delta) / 1000;
		
		float mdist = Math.max(distance, distances);
		int loops = (int)Math.round(mdist) + 1;
		float perloop1 = distance / (float)loops;
		float perloop2 = distances / (float)loops;
		
		distance = perloop1;
		distances = perloop2;
		
		for(int l = 0; l < loops; l++) {
			
			float dx = (float)(distance * Math.sin(Math.toRadians(-getYaw())));
			float dz = (float)(distance * Math.cos(Math.toRadians(-getYaw())));
			
			float dsx = (float)(distances * Math.sin(Math.toRadians(-getYaw() + 90.0)));
			float dsz = (float)(distances * Math.cos(Math.toRadians(-getYaw() + 90.0)));
			
			dx = -dx + dsx;
			dz = -dz + dsz;
			
			int blockposx = (int)position.x;
			int blockposy = (int)position.y;
			int blockposz = (int)position.z;
			
			for(int i = 0; i < 2; i++) {
				
				for(int a = -3; a <= 3; a++) {
					for(int b = -3; b <= 3; b++) {
						int newblockposx = (int)(position.x + dx);
						int newblockposz = (int)(position.z + dz);
						int blockid = world.getBlock(newblockposx + a, blockposy + i, newblockposz + b);
						//Will moving result in a collision with a block?
						if(hitbox.moveClone(position.x + dx, position.y, position.z + dz).isColliding(Block.allBlocks[blockid].getBoundingBox(newblockposx + a, blockposy + i, newblockposz + b))) { //Hitbox will collide after moving
							//Check if its possible to only cancel movement in one direction
							blockid = world.getBlock(newblockposx + a, blockposy + i, blockposz + b);
							//What if we were to only move along the x axis?
							if(!hitbox.moveClone(position.x + dx, position.y, position.z).isColliding(Block.allBlocks[blockid].getBoundingBox(newblockposx + a, blockposy + i, blockposz + b))) { //Hitbox will collide after moving
								//Cancel only z movement if its possible
								dz = 0;
							}else {
								//What if we were to only move along the z axis?
								blockid = world.getBlock(blockposx + a, blockposy + i, newblockposz + b);
								if(!hitbox.moveClone(position.x, position.y, position.z + dz).isColliding(Block.allBlocks[blockid].getBoundingBox(blockposx + a, blockposy + i, newblockposz + b))) { //Hitbox will collide after moving
									//Cancel only x movement if its possible
									dx = 0;
								}else {
									//Cancel both if neither are possible
									dz = 0;
									dx = 0;
								}
							}
						}
					}
				}
				
			}
			move(dx, 0, dz);
		}
		
		for(int a = -3; a <= 3; a++) {
			int blockposx = (int)position.x;
			int blockposy = (int)(position.y - 1);
			int blockposz = (int)position.z;
			for(int b = -3; b <= 3; b++) {
				int blockid = world.getBlock(blockposx + a, blockposy, blockposz + b);
				if(!hitbox.moveClone(position.x, position.y + GRAVITY, position.z).isColliding(Block.allBlocks[blockid].getBoundingBox(blockposx + a, blockposy, blockposz + b))) { //Hitbox will collide after moving
					upwardsSpeed += (GRAVITY * MainGameLoop.delta) / 1000f;
					isInAir = true;
					b = 99;
					a = 99;
					break;
				}
			}
		}
		
		distance = upwardsSpeed * (MainGameLoop.delta / 1000f);
		loops = (int)Math.round(Math.abs(distance)) + 1;
		perloop1 = distance / (float)loops;
		
		distance = perloop1;
		
		if(!world.isChunkLoaded((int)this.position.x / 16, (int)this.position.z / 16)) {
			upwardsSpeed = 0;
		}else {
			for(int l = 0; l < loops; l++) {
				
				int blockposx = (int)position.x;
				int blockposz = (int)position.z;
				
				for(int i = 0; i < 2; i++) {
					for(int a = -3; a <= 3; a++) {
						for(int b = -3; b <= 3; b++) {
							int newblockposy = (int)(position.y + distance);
							int blockid = world.getBlock(blockposx + a, newblockposy + i, blockposz + b);
							
							if(hitbox.moveClone(position.x, position.y + distance+ i, position.z).isColliding(Block.allBlocks[blockid].getBoundingBox(blockposx + a, newblockposy + i, blockposz + b))) { //Hitbox will collide after moving
								if(distance < 0 && isInAir) {
									isInAir = false;
									if(upwardsSpeed < 0) upwardsSpeed = 0;
								}
								distance = 0;
								break;
							}
						}
					}
				}
				
				move(0, distance, 0);
			}
		}
		
		mouse();
		
		Vector3f a = new Vector3f(0f, 0f, 1f);
		float cosx = (float)Math.cos(Math.toRadians(pitch));
		float cosy = (float)Math.cos(Math.toRadians(yaw));
		float sinx = (float)Math.sin(Math.toRadians(pitch));
		float siny = (float)Math.sin(Math.toRadians(yaw));
		
		float newy = a.y * cosx - a.z * sinx;
		a.z = a.y * sinx + a.z * cosx;
		a.y = newy;
		
		float newx = a.x * cosy + a.z * siny;
		a.z = -a.x * siny + a.z * cosy;
		a.x = newx;
		
		a.x /= 5.0f;
		a.y /= 5.0f;
		a.z /= -5.0f;
		
		boolean selected = true;
		Vector3f start = new Vector3f(position.x + 0.5f, position.y + 2.0f, position.z + 0.5f);
		for(int i = 0; i < REACH_DISTANCE * 5; i++) {
			if(world.getBlock((int)start.x(), (int)start.y(), (int)start.z()) != Block.air.getBlockID()) {
				break;
			}
			start.add(a, start);
			if(i == REACH_DISTANCE * 5 - 1) {
				start = new Vector3f(3000.0f, 3000.0f, 3000.0f);
				selected = false;
			}
		}
		
		/*//Raycast to find the block we're currently looking at
		Matrix4f invertedView = Maths.createViewMatrix(this);
		invertedView.invert(invertedView);
		
		Vector4f clipCoords = new Vector4f(-0.5f, 0, -1.0f, 1.0f);
		
		Matrix4f invertedProjection = new Matrix4f();
		BlocksRenderer.projectionMatrix.invert(invertedProjection);
		invertedProjection.transform(clipCoords, clipCoords);
		clipCoords.z = -1.0f;
		clipCoords.w = 0f;
		Vector4f rayWorld = invertedView.transform(clipCoords);
		Vector3f mouseRay = new Vector3f(rayWorld.x() / 5f, rayWorld.y() / 5f, rayWorld.z() / 5f);
		mouseRay.normalize(mouseRay);
		Vector3f testRay = new Vector3f();
		//System.out.println(mouseRay);
		testRay.set(mouseRay.x() + position.x(), mouseRay.y() + position.y(), mouseRay.z() + position.z());
		for(int i = 0; i < REACH_DISTANCE; i++) {
			if(world.getBlock((int)testRay.x(), (int)testRay.y() , (int)testRay.z()) != Block.air.getBlockID()) {
				break;
			}
			testRay.add(mouseRay, testRay);
		}*/
		selectedX = start.x();
		selectedY = start.y();
		selectedZ = start.z();
		if(selected) {
			//TestRay now contains the position of the selected block
			if(GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_1) == GL11.GL_TRUE) {
				world.setBlock((int)start.x(), (int)start.y(), (int)start.z(), Block.air.getBlockID());
			}
			if(GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_2) == GL11.GL_TRUE) {
				world.setBlock((int)start.x(), (int)start.y(), (int)start.z(), selectedBlock);
				//world.placeLightSource((int)testRay.x(), (int)testRay.y() + 1, (int)testRay.z(), 15);
			}
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
	
	public void strave(float dx, float dz) {
		float dsx = (float)(dx * Math.sin(Math.toRadians(-getYaw() + 90.0)));
		float dsz = (float)(dz * Math.cos(Math.toRadians(-getYaw() + 90.0)));
		move(dsx, 0, dsz);
	}
	
	private boolean released = true;
	private final int maxblock = 21;
	
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
		if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_N) == GL11.GL_TRUE) {
			if(released) {
				selectedBlock--;
				if(selectedBlock <= 0) selectedBlock = maxblock;
			}
			released = false;
		}else if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_M) == GL11.GL_TRUE) {
			if(released) {
				selectedBlock++;
				if(selectedBlock > maxblock) selectedBlock = 1;
			}
			released = false;
		}else {
			released = true;
		}
		
		
	}
	
	public void saveState() throws Exception {
		File outFile = new File("World/player.dat");
		FileOutputStream fos = new FileOutputStream(outFile);
		DataOutputStream dos = new DataOutputStream(fos);
		dos.writeFloat(position.x);
		dos.writeFloat(position.y);
		dos.writeFloat(position.z);
		dos.writeFloat(pitch);
		dos.writeFloat(yaw);
		dos.writeFloat(roll);
		dos.close();
	}
	
	public void loadState() throws Exception {
		File inFile = new File("World/player.dat");
		if(!inFile.exists()) return;
		FileInputStream fis = new FileInputStream(inFile);
		DataInputStream dis = new DataInputStream(fis);
		position = new Vector3f(dis.readFloat(), dis.readFloat(), dis.readFloat());
		pitch = dis.readFloat();
		yaw = dis.readFloat();
		roll = dis.readFloat();
		dis.close();
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public void setRoll(float roll) {
		this.roll = roll;
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