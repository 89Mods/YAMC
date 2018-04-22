package theGhastModding.meshingTest.world.blocks;

public class BlockGrass extends Block {
	
	public BlockGrass() {
		super(2,2);
	}
	
	public int getTexture(int face) {
		if(face == Block.BLOCK_FACE_LEFT || face == Block.BLOCK_FACE_RIGHT || face == Block.BLOCK_FACE_FRONT || face == Block.BLOCK_FACE_BACK) {
			return 6;
		}
		if(face == Block.BLOCK_FACE_BOTTOM) {
			return 3;
		}
		return this.defaultTextureLoc;
	}
	
	public boolean isOpaque(int face) {
		return true;
	}
	
}