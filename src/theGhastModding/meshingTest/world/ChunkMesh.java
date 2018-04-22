package theGhastModding.meshingTest.world;

public class ChunkMesh {
	
	private int id;
	private int vertexCount;
	private boolean isEmpty;
	
	public ChunkMesh(int id, int vertexCount, boolean isEmpty) {
		this.id = id;
		this.vertexCount = vertexCount;
		this.isEmpty = isEmpty;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getVertexCount() {
		return this.vertexCount;
	}
	
	public boolean isEmpty() {
		return this.isEmpty;
	}
	
}