package theGhastModding.meshingTest.shaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import theGhastModding.meshingTest.resources.ShaderException;

public abstract class ShaderProgram {
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private FloatBuffer matrixBuffer;
	
	public ShaderProgram(String vertexFile, String fragmentFile) throws Exception {
		matrixBuffer = BufferUtils.createFloatBuffer(16);
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	protected abstract void bindAttributes();
	
	protected void bindAttribute(int attribute, String variableName){
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}
	
	protected abstract void getAllUniformLocations();
	
	protected int getUniformLocation(String uniformName){
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	protected int getUniformBlockIndex(String blockName){
		return GL31.glGetUniformBlockIndex(programID, blockName);
	}
	
	protected void loadDataToBlock(int blockLocation, FloatBuffer data, int offset){
		int bindingPoint = 1;
		int buffer = GL15.glGenBuffers();
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, buffer);
		GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, offset, data);
		GL31.glUniformBlockBinding(programID, blockLocation, bindingPoint);
		GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, bindingPoint, buffer);
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
	}
	
	protected void loadDataToBlock(int blockLocation, FloatBuffer data){
		int bindingPoint = 1;
		int buffer = GL15.glGenBuffers();
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, buffer);
		GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, data, GL15.GL_STATIC_DRAW);
		GL31.glUniformBlockBinding(programID, blockLocation, bindingPoint);
		GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, bindingPoint, buffer);
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
	}
	
	public void start(){
		GL20.glUseProgram(programID);
	}
	
	public void stop(){
		GL20.glUseProgram(0);
	}
	
	public void cleanUp(){
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteProgram(vertexShaderID);
		GL20.glDeleteProgram(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}
	
	private static int loadShader(String file, int type) throws Exception {
		String shader = "";
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
		String currLine = "lol";
		while(currLine != null){
			currLine = reader.readLine();
			if(currLine != null) shader += currLine + "\n";
		}
		shader = shader.substring(0, shader.length() - 1);
		reader.close();
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shader);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE){
			System.out.println(GL20.glGetShaderInfoLog(shaderID));
			throw new ShaderException(type, shaderID, "Could not compile " + (type == GL20.GL_VERTEX_SHADER ? "vertex" : "fragment") + " shader.");
		}
		return shaderID;
	}
	
	protected void loadFloat(int location, float value){
		GL20.glUniform1f(location, value);
	}
	
	protected void loadVector(int location, Vector3f vector){
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}
	
	public void loadVector(int location, Vector2f vector) {
		GL20.glUniform2f(location, vector.x, vector.y);
	}
	
	protected void loadInt(int location, int value){
		GL20.glUniform1i(location, value);
	}
	
	protected void loadBoolean(int location, boolean b){
		if(b){
			loadFloat(location, (float)GL11.GL_TRUE);
		}else{
			loadFloat(location, (float)GL11.GL_FALSE);
		}
	}
	
	protected void loadMatrix(int location, Matrix4f matrix){
		GL20.glUniformMatrix4fv(location, false, matrix.get(matrixBuffer));
	}
	
}