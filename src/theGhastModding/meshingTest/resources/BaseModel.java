package theGhastModding.meshingTest.resources;

public class BaseModel {
	
	private int id;
	private int vertexCount;
	
	public BaseModel(int id, int vertexCount){
		this.id = id;
		this.vertexCount = vertexCount;
	}
	
	public int getId() {
		return id;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
}