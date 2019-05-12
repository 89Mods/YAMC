package theGhastModding.meshingTest.world.blocks;

import theGhastModding.meshingTest.phys.AABB;

public class BlockWater extends Block {
	
	public BlockWater() {
		super(16, 12);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	protected AABB getBoundingBox() { 
		return null;
	}
	
}