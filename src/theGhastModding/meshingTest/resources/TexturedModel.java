package theGhastModding.meshingTest.resources;

public class TexturedModel {
	
	private ModelTexture texture;
	private BaseModel model;
	
	public TexturedModel(BaseModel model, ModelTexture texture) {
		this.texture = texture;
		this.model = model;
	}
	
	public BaseModel getModel(){
		return model;
	}
	
	public ModelTexture getTexture() {
		return texture;
	}
	
}