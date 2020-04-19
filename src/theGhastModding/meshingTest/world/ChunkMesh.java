package theGhastModding.meshingTest.world;

import theGhastModding.meshingTest.resources.BaseModel;

public class ChunkMesh {
	
	private BaseModel model;
	private boolean isEmpty;
	
	public ChunkMesh(BaseModel model, boolean isEmpty) {
		this.model = model;
		this.isEmpty = isEmpty;
	}
	
	public BaseModel getModel() {
		return this.model;
	}
	
	public boolean isEmpty() {
		return this.isEmpty;
	}
	
	public void deleted() {
		this.isEmpty = true;
	}
	
}