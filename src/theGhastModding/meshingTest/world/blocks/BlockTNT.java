package theGhastModding.meshingTest.world.blocks;

public class BlockTNT extends Block {
	
	public BlockTNT() {
		super(19,34);
	}
	
	public int getTexture(int face) {
		if(face == Block.BLOCK_FACE_LEFT || face == Block.BLOCK_FACE_RIGHT || face == Block.BLOCK_FACE_FRONT || face == Block.BLOCK_FACE_BACK) {
			return 33;
		}
		if(face == Block.BLOCK_FACE_BOTTOM) {
			return 34;
		}
		return this.defaultTextureLoc;
	}
	
	public boolean isOpaque(int face) {
		return true;
	}
	
}