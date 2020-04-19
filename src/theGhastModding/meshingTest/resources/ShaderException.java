package theGhastModding.meshingTest.resources;

@SuppressWarnings("serial")
public class ShaderException extends Exception {
	
	private int type;
	private int id;
	
	public ShaderException(int type, int id){
		super();
		this.type = type;
		this.id = id;
	}
	
	public ShaderException(int type, int id, String message){
		super(message);
		this.type = type;
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	
	public int getId() {
		return id;
	}
	
}