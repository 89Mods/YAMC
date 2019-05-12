package theGhastModding.meshingTest.world.blocks;

public class BlockLeaves extends Block {
	
	public BlockLeaves() {
		super(7,19);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public boolean canRenderThrough() {
		return true;
	}
	
}