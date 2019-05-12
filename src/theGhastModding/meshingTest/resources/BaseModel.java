package theGhastModding.meshingTest.resources;

public class BaseModel {
	
	private int id;
	private int[] vbos;
	private int vertexCount;
	
	public BaseModel(int id, int[] vbos, int vertexCount){
		this.id = id;
		this.vbos = vbos;
		this.vertexCount = vertexCount;
	}
	
	public int getId() {
		return id;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public int[] getVBOs() {
		return this.vbos;
	}
	
}