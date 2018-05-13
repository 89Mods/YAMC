package theGhastModding.meshingTest.util;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import theGhastModding.meshingTest.object.Camera;

public class Maths {
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(new Vector3f(translation.x, translation.y, 0), matrix);
		matrix.scale(new Vector3f(scale.x, scale.y, 1f), matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale){
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(translation, matrix);
		matrix.rotate((float)Math.toRadians(rx), new Vector3f(1,0,0), matrix);
		matrix.rotate((float)Math.toRadians(ry), new Vector3f(0,1,0), matrix);
		matrix.rotate((float)Math.toRadians(rz), new Vector3f(0,0,1), matrix);
		matrix.scale(new Vector3f(scale, scale, scale), matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale){
		return createTransformationMatrix(translation, rotation.x, rotation.y, rotation.z, scale);
	}
	
    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix);
        viewMatrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix);
        viewMatrix.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        viewMatrix.translate(negativeCameraPos, viewMatrix);
        return viewMatrix;
    }
	
    public static float distance(int x1, int y1, int z1, int x2, int y2, int z2) {
    	float x3 = x1 - x2;
    	float y3 = y1 - y2;
    	float z3 = z1 - z2;
    	return (float) Math.sqrt(x3 * x3 + y3 * y3 + z3 * z3);
    }
    
}