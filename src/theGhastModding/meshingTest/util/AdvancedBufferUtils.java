package theGhastModding.meshingTest.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class AdvancedBufferUtils {
	
	public static FloatBuffer storeDataInBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public static FloatBuffer storeDatasInBuffer(float[][] datas){
		int totalLength = 0;
		for(float[] f:datas){
			totalLength += f.length;
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(totalLength);
		for(float[] f:datas){
			buffer.put(f);
		}
		buffer.flip();
		return buffer;
	}
	
	public static IntBuffer storeDataInBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
}