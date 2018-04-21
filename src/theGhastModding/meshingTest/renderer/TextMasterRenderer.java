package theGhastModding.meshingTest.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL30;

import theGhastModding.meshingTest.resources.Loader;
import theGhastModding.meshingTest.text.FontType;
import theGhastModding.meshingTest.text.GUIText;
import theGhastModding.meshingTest.text.TextMeshData;

public class TextMasterRenderer {
	
	private Loader loader;
	private Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private TextRenderer renderer;
	
	public TextMasterRenderer(Loader loader) throws Exception {
		renderer = new TextRenderer();
		this.loader = loader;
	}
	
	public void render(){
		renderer.render(texts);
	}
	
	public void loadText(GUIText text) throws Exception {
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		List<GUIText> textBatch = texts.get(font);
		if(textBatch == null){
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}
	
	public void removeText(GUIText text, boolean removeFromMemory){
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		if(textBatch.isEmpty()){
			texts.remove(text.getFont());
		}
		if(removeFromMemory){
			GL30.glDeleteVertexArrays(text.getMesh());
		}
	}
	
	public void cleanUp(){
		renderer.cleanUp();
	}
	
}