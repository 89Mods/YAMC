package theGhastModding.meshingTest.main;

import javax.swing.JOptionPane;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public class MeshingTest {
	
	public static final String NAME = "TGM's Minecraft Clone";
	public static final String VERSION = "mcc_4212018";
	
	public static void main(String[] args){
		if(!GLFW.glfwInit()){
			JOptionPane.showMessageDialog(null, "Error initializing GLFW", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		long window = GLFW.glfwCreateWindow(1280, 720, NAME, 0, 0);
		if(window == 0){
			JOptionPane.showMessageDialog(null, "Error creating window", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(window, (videoMode.width() - 1280) / 2, (videoMode.height() - 720) / 2);
		GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
		GLFW.glfwShowWindow(window);
		
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		
		MainGameLoop loop = new MainGameLoop(window);
		loop.start();
	}
	
}