package theGhastModding.meshingTest.main;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import theGhastModding.meshingTest.gui.GuiRenderer;
import theGhastModding.meshingTest.gui.GuiTexture;
import theGhastModding.meshingTest.object.Camera;
import theGhastModding.meshingTest.renderer.MasterRenderer;
import theGhastModding.meshingTest.renderer.TextMasterRenderer;
import theGhastModding.meshingTest.resources.BasicFonts;
import theGhastModding.meshingTest.resources.Loader;
import theGhastModding.meshingTest.resources.textures.BlockTexturemap;
import theGhastModding.meshingTest.sky.celestials.CelestialBody;
import theGhastModding.meshingTest.sound.SoundEngine;
import theGhastModding.meshingTest.text.GUIText;
import theGhastModding.meshingTest.util.FileChannelOutputStream;
import theGhastModding.meshingTest.world.Chunk;
import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.WorldMesher;
import theGhastModding.meshingTest.world.gen.WorldGeneratorDefault;

public class MainGameLoop {
	
	private long window;
	private File screenshotsFolder;
	//TODO: find a better way to pass this around
	public static long delta = 0L;
	//TODO: same for this
	public static boolean cursorGrabbed = true;
	
	private double spawnx,spawny;
	
	public MainGameLoop(long window, double spawnx, double spawny){
		try {
			this.window = window;
			this.spawnx = spawnx;
			this.spawny = spawny;
			screenshotsFolder = new File("screenshots/");
			if(!screenshotsFolder.exists()){
				try {
					screenshotsFolder.mkdir();
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, "Error creating screenshots folder: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
			GLFW.glfwMakeContextCurrent(window);
			GL.createCapabilities();
		} catch(Exception e){
			JOptionPane.showMessageDialog(null, "Error creating MainGameLoop: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void start(){
		World world = null;
		Thread worldThread = null;
		WorldMesher mesher = null;
		MasterRenderer renderer = null;
		TextMasterRenderer textRenderer = null;
		GuiRenderer guiRenderer = null;
		BasicFonts basicFonts = null;
		BlockTexturemap texturemap = null;
		SoundEngine sound = null;
		Random rng = new Random();
		GuiTexture loadingScreen = null;
		CelestialBody moon = null;
		//CelestialBody aireos = null;
		List<CelestialBody> celestials = new ArrayList<CelestialBody>();
		try {
			world = new World(640, 128, 640);
			worldThread = new Thread(world);
			renderer = new MasterRenderer(window);
			textRenderer = new TextMasterRenderer();
			guiRenderer = new GuiRenderer();
			basicFonts = new BasicFonts(window);
			sound = new SoundEngine();
			sound.registerSource(new File("res/buzz_mono.wav"), "buzz");
			
			try {
				texturemap = new BlockTexturemap("res/map_placeholder.png", 256, 256, 32);
				loadingScreen = new GuiTexture(Loader.loadTextureFromFile("res/GUI/menubg_placeholder.png"), new Vector2f(0, 0), new Vector2f(1f, 1f));
				moon = CelestialBody.loadPlanet(1738.1f, new Vector3f(0f,36260.0f,0f), new Vector3f(440f,0f,0f), "res/celestials/moon/colors.png", "res/celestials/moon/height.png", 7.5f);
				celestials.add(moon);
				//aireos = CelestialBody.loadPlanet(60268, new Vector3f(168055.5f,69610.8f,0f), new Vector3f(90f,0f,0f), "res/celestials/aireos/colors.png", "res/celestials/aireos/height.png", 1f);
				//celestials.add(aireos);
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Error loading textures: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				System.exit(1);
			}
			
			mesher = new WorldMesher(world, texturemap);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Error creating rendering system: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}
		
		GUIText text = new GUIText("FPS: 0", 1, basicFonts.arial, new Vector2f(0, 0), 1f, false);
		text.setColour(1, 1, 1);
		text.setStyle(GUIText.PLAIN);
		
		GUIText progressText = new GUIText("Generating world: 0%", 1.5f, basicFonts.arial, new Vector2f(0f, 0.45f), 1f, true);
		progressText.setColour(1, 1, 1);
		progressText.setStyle(GUIText.BOLD);
		
		try {
			Camera camera = new Camera(window, world);
			
			textRenderer.loadText(text);
			textRenderer.loadText(progressText);
			guiRenderer.addGui(loadingScreen);
			
			GLFW.glfwPollEvents();
			guiRenderer.render();
			textRenderer.render();
			GLFW.glfwSwapBuffers(window);
			
			camera.getPosition().x = 50;
			camera.getPosition().y = world.getHeight()*5;
			camera.getPosition().z = 50;
			sound.update(camera);
			camera.loadState();
			
			world.loadWorldData();
			world.setWorldGen(new WorldGeneratorDefault(world));
			//world.setWorldGen(new WorldGeneratorHypermaze(world));
			//((WorldGeneratorHypermaze)world.getWorldGen()).roomcnt = 0;
			//world.setWorldGen(new WorldGeneratorBackrooms(world));
			//world.setWorldGen(new WorldGeneratorMandel(world));
			//world.setWorldGen(new WorldGeneratorMaze(world));
			//((WorldGeneratorMaze)world.getWorldGen()).roomcnt = 8192;
			boolean newWorld = camera.getPosition().x == -1;
			world.startGenerateSpawnChunks(newWorld ? world.getChunkWidth() / 2 : (int)camera.getPosition().x / Chunk.CHUNK_WIDTH, newWorld ? world.getChunkHeight() / 2 : (int)camera.getPosition().z / Chunk.CHUNK_DEPTH);
			worldThread.start();
			
			boolean kHeld = false;
			
			double moonOrbitPosition = 0;
			
			Dimension d = getWindowSize(window);
			System.out.println(d.toString());
			long lastTime = System.currentTimeMillis();
			int counter = 0;
			double frameTime = 1000000000D / 60D;
			long frameTimer = System.nanoTime();
			int counter2 = 0;
			Vector3f prevCpos = new Vector3f();
			while(!GLFW.glfwWindowShouldClose(window)){
				if(System.nanoTime() - frameTimer >= frameTime){
					delta = (System.nanoTime() - frameTimer) / 1000;
					frameTimer = System.nanoTime();
					if(world.generatingSpawn) {
						GLFW.glfwPollEvents();
						guiRenderer.render();
						textRenderer.render();
						GLFW.glfwSwapBuffers(window);
						textRenderer.removeText(progressText, true);
						progressText.setText("Generating world: " + String.format("%.1f", world.getGenerationProgress()) + "%");
						textRenderer.loadText(progressText);
						continue;
					}else if(!loadingScreen.isHidden()) {
						loadingScreen.setHidden(true);
						textRenderer.removeText(progressText, true);
						camera.getPosition().x = 50;
						camera.getPosition().y = 128;
						camera.getPosition().z = 50;
						camera.loadState();
						mesher.updateMeshesNow();
						mesher.updateRendererMeshes();
						mesher.startMeshingThread();
						//SoundEngine.current.playSound("buzz", new Vector3f(50, 7, 50), true);
					}
					
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GL11.GL_TRUE){
						GLFW.glfwSetWindowShouldClose(window, true);
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_0) == GL11.GL_TRUE){
						System.out.println(camera.getPosition().toString());
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_R) == GL11.GL_TRUE){
						camera.getPosition().x = 32;
						camera.getPosition().y = world.getHeight() + 1;
						camera.getPosition().z = 32;
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_Z) == GL11.GL_TRUE){
						camera.getPosition().x = (float)spawnx;
						camera.getPosition().y = world.getHeight() + 1;
						camera.getPosition().z = (float)spawny;
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_T) == GL11.GL_TRUE){
						camera.getPosition().y = world.getHeight() + 1;
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_1) == GL11.GL_TRUE){
						camera.setYaw(camera.getYaw() + 1f);
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_2) == GL11.GL_TRUE){
						camera.setYaw(camera.getYaw() - 1f);
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F2) == GL11.GL_TRUE){
						screenshot();
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_K) == GL11.GL_TRUE){
						if(!kHeld) {
							kHeld = true;
							world.setSunlightStrength(rng.nextFloat() * 15.0f);
						}
					}else {
						kHeld = false;
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_7) == GL11.GL_TRUE) {
						world.increaseTime(25L);
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_6) == GL11.GL_TRUE) {
						world.increaseTime(-25L);
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_P) == GL11.GL_TRUE){
						GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
						cursorGrabbed = false;
					}
					if(GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_1) == GL11.GL_TRUE && !cursorGrabbed){
						cursorGrabbed = true;
						GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
					}
					
					moonOrbitPosition = (360.0D - ((world.getWorldTime() % 144000L) / 144000.0D * 360.0D) + (360.0D - world.getTimeOfDay() / 24000.0D * 360.0D));
					moonOrbitPosition %= 360.0;
					moon.getPos().x = (float)Math.sin(Math.toRadians(moonOrbitPosition)) * 36260.0f;
					moon.getPos().y = (float)Math.cos(Math.toRadians(moonOrbitPosition)) * 36260.0f;
					moon.getRot().y = -(float)moonOrbitPosition / 360.0f * 180.0f;
					
					camera.update();
					sound.update(camera);
					mesher.updateRendererMeshes();
					GLFW.glfwPollEvents();
					prevCpos.x = camera.getPosition().x;
					prevCpos.y = camera.getPosition().y;
					prevCpos.z = camera.getPosition().z;
					//camera.strave(-0.2f, 0);
					//renderer.render(camera, mesher, texturemap, 1);
					
					//camera.strave(0.4f, 0);
					renderer.render(camera, mesher, texturemap, 0, world, celestials);
					
					camera.setPosition(prevCpos.x, prevCpos.y, prevCpos.z);
					GL11.glColorMask(true, true, true, true);
					
					guiRenderer.render();
					textRenderer.render();
					if(mesher.e != null) throw mesher.e;
					if(world.e != null) throw world.e;
					GLFW.glfwSwapBuffers(window);
					
					counter++;
				}
				if(System.currentTimeMillis() - lastTime >= 1000){
					lastTime = System.currentTimeMillis();
					System.out.println("FPS: " + Integer.toString(counter));
					textRenderer.removeText(text, true);
					text.setText("FPS: " + Integer.toString(counter));
					textRenderer.loadText(text);
					counter = 0;
					counter2++;
					if(counter2 >= 10){
						//GLFW.glfwSetWindowShouldClose(window, true);
					}
					if(!world.generatingSpawn) {
						Vector3f pos = camera.getPosition();
						world.updateLoadedChunks((int)pos.x, (int)pos.y, (int)pos.z);
					}
					//System.gc();
				}
			}
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
			cursorGrabbed = false;
			screenshot();
			loadingScreen.setHidden(false);
			progressText.setText("Saving World...");
			textRenderer.loadText(progressText);
			guiRenderer.render();
			textRenderer.render();
			GLFW.glfwSwapBuffers(window);
			
			world.running = false;
			mesher.running = false;
			worldThread.join(2048);
			world.join();
			GLFW.glfwPollEvents();
			mesher.join();
			GLFW.glfwPollEvents();
			camera.saveState();
			GLFW.glfwPollEvents();
			world.saveWorld();
			GLFW.glfwPollEvents();
			Loader.cleanUp();
			renderer.cleanUp();
			textRenderer.cleanUp();
			sound.cleanUp();
			GLFW.glfwTerminate();
			GL.destroy();
		} catch(Exception e){
			JOptionPane.showMessageDialog(null, "Error running game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
	
	public void screenshot(){
		try {
			Dimension windowSize = MainGameLoop.getWindowSize(window);
			int width = (int)windowSize.width;
			int height = (int)windowSize.height;
			GL11.glReadBuffer(GL11.GL_FRONT);
			ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
			GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
			RandomAccessFile file = new RandomAccessFile(screenshotsFolder.getPath() + "/" + ZonedDateTime.now().toString().replaceAll(":", "_").replaceAll("/", "_") + MeshingTest.NAME.replaceAll(" ", "_") + "_" + MeshingTest.VERSION + ".png", "rw");
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			   
			for(int x = 0; x < width; x++) {
			    for(int y = 0; y < height; y++) {
			        int i = (x + (width * y)) * 4;
			        int r = buffer.get(i) & 0xFF;
			        int g = buffer.get(i + 1) & 0xFF;
			        int b = buffer.get(i + 2) & 0xFF;
			        image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			    }
			}
			FileChannelOutputStream out = new FileChannelOutputStream(file.getChannel());
			ImageIO.write(image, "png", out);
			out.close();
			file.close();
			System.out.println("Screenshot taken!");
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Error taking screenshot: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}
	}
	
	public static Dimension getWindowSize(long window){
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(window, w, h);
		return new Dimension(w.get(0), h.get(0));
	}
	
}