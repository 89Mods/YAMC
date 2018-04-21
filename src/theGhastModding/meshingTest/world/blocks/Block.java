package theGhastModding.meshingTest.world.blocks;

public class Block {
	
	public static final int BLOCK_FACE_LEFT = 0;
	public static final int BLOCK_FACE_RIGHT = 1;
	public static final int BLOCK_FACE_FRONT = 2;
	public static final int BLOCK_FACE_BACK = 3;
	public static final int BLOCK_FACE_TOP = 4;
	public static final int BLOCK_FACE_BOTTOM = 5;
	
	public static final Block[] allBlocks = new Block[256];
	
	public static final Block air;
	public static final Block stone;
	public static final Block grass;
	public static final Block dirt;
	
	static {
		air = new Block(0,0) {
			@Override
			public boolean shouldRender(int face) { return false; }
		};
		stone = new Block(1,1);
		grass = new Block(2,2);
		dirt = new BlockDirt(3,3);
	}
	
	private int id;
	private int defaultTextureLoc;
	
	public Block(int id, int textureLocation) {
		this.id = id;
		this.defaultTextureLoc = textureLocation;
		allBlocks[id] = this;
	}
	
	public int getTexture(int face) {
		return this.defaultTextureLoc;
	}
	
	public boolean shouldRender(int face) {
		return true;
	}
	
	public int getBlockID() {
		return this.id;
	}
	
	public static Block getBlockFromID(int id) {
		return allBlocks[id];
	}
	
}