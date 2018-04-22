package theGhastModding.meshingTest.main;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.time.ZonedDateTime;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import theGhastModding.meshingTest.object.Camera;
import theGhastModding.meshingTest.renderer.Renderer;
import theGhastModding.meshingTest.renderer.TextMasterRenderer;
import theGhastModding.meshingTest.resources.BasicFonts;
import theGhastModding.meshingTest.resources.Loader;
import theGhastModding.meshingTest.resources.textures.BlockTexturemap;
import theGhastModding.meshingTest.text.GUIText;
import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.WorldMesher;

public class MainGameLoop {
	
	private long window;
	private File screenshotsFolder;
	//TODO: find a better way to pass this around
	public static long delta = 0L;
	//TODO: same for this
	public static boolean cursorGrabbed = true;
	
	public MainGameLoop(long window){
		try {
			this.window = window;
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
		Loader loader = null;
		World world = null;
		WorldMesher mesher = null;
		Renderer renderer = null;
		TextMasterRenderer textRenderer = null;
		BasicFonts basicFonts = null;
		BlockTexturemap texturemap = null;
		try {
			loader = new Loader();
			world = new World(World.DEFAULT_WIDTH, World.DEFAULT_HEIGHT, World.DEFAULT_DEPTH);
			mesher = new WorldMesher(world);
			renderer = new Renderer(window);
			textRenderer = new TextMasterRenderer(loader);
			basicFonts = new BasicFonts(loader, window);
			texturemap = new BlockTexturemap("res/map.png", loader, 48, 48, 16);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Error creating rendering system: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}
		world.generate();
		GUIText text = new GUIText("FPS: 0", 1, basicFonts.arial, new Vector2f(0, 0), 1f, false);
		text.setColour(1, 1, 1);
		text.setStyle(GUIText.PLAIN);
		try {
			mesher.updateMeshes(loader, texturemap);
			textRenderer.loadText(text);
			Dimension d = getWindowSize(window);
			System.out.println(d.toString());
			long lastTime = System.currentTimeMillis();
			int counter = 0;
			double frameTime = 1000000000D / 60D;
			long frameTimer = System.nanoTime();
			Camera camera = new Camera(window, world);
			int counter2 = 0;
			while(!GLFW.glfwWindowShouldClose(window)){
				if(System.nanoTime() - frameTimer >= frameTime){
					delta = (System.nanoTime() - frameTimer) / 1000;
					frameTimer = System.nanoTime();
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GL11.GL_TRUE){
						GLFW.glfwSetWindowShouldClose(window, true);
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_0) == GL11.GL_TRUE){
						System.out.println(camera.getPosition().toString());
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F2) == GL11.GL_TRUE){
						screenshot();
					}
					if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_P) == GL11.GL_TRUE){
						GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
						cursorGrabbed = false;
					}
					if(GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_1) == GL11.GL_TRUE && !cursorGrabbed){
						cursorGrabbed = true;
						GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
					}
					camera.update();
					GLFW.glfwPollEvents();
					renderer.render(camera, mesher, texturemap.getTextureID());
					textRenderer.render();
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
					//System.gc();
				}
			}
			screenshot();
			loader.cleanUp();
			renderer.cleanUp();
			textRenderer.cleanUp();
			GLFW.glfwTerminate();
			GL.destroy();
		} catch(Exception e){
			JOptionPane.showMessageDialog(null, "Error running game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void screenshot(){
		try {
			Dimension windowSize = MainGameLoop.getWindowSize(window);
			int width = (int)windowSize.width;
			int height = (int)windowSize.height;
			GL11.glReadBuffer(GL11.GL_FRONT);
			ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
			GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
			File file = new File(screenshotsFolder.getPath() + "/" + ZonedDateTime.now().toString().replaceAll(":", "_").replaceAll("/", "_") + MeshingTest.NAME.replaceAll(" ", "_") + "_" + MeshingTest.VERSION + ".png");
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			   
			for(int x = 0; x < width; x++)
			{
			    for(int y = 0; y < height; y++)
			    {
			        int i = (x + (width * y)) * 4;
			        int r = buffer.get(i) & 0xFF;
			        int g = buffer.get(i + 1) & 0xFF;
			        int b = buffer.get(i + 2) & 0xFF;
			        image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			    }
			}
			ImageIO.write(image, "png", file);
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