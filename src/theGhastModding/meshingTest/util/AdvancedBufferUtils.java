package theGhastModding.meshingTest.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;

public class AdvancedBufferUtils {
	
	public static FloatBuffer storeDataInBuffer(float[] data, int len){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(len);
		buffer.put(data, 0, len);
		buffer.flip();
		return buffer;
	}
	
	public static ShortBuffer storeDataInBuffer(short[] data, int len) {
		ShortBuffer buffer = BufferUtils.createShortBuffer(len);
		buffer.put(data, 0, len);
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
	
	public static IntBuffer storeDataInBuffer(int[] data, int len){
		IntBuffer buffer = BufferUtils.createIntBuffer(len);
		buffer.put(data, 0, len);
		buffer.flip();
		return buffer;
	}
	
}