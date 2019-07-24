package theGhastModding.meshingTest.world.blocks;

import java.util.Random;

import theGhastModding.meshingTest.phys.AABB;
import theGhastModding.meshingTest.world.World;

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
	
	@Override
	public void onNeighborChanged(World world, int x, int y, int z, int x_n, int y_n, int z_n, Random rng) {
		this.updateTick(world, x, y, z, rng);
	}
	
	@Override
	public void onBlockPlaced(World world, int x, int y, int z, Random rng) {
		//this.updateTick(world, x, y, z, rng);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rng) {
		if(world.getBlock(x, y - 1, z) == Block.water.getBlockID()) return;
		if(world.getBlock(x, y - 1, z) == Block.air.getBlockID()) {
			world.setBlock(x, y - 1, z, Block.water.getBlockID());
			return;
		}else {
			if(world.getBlock(x + 1, y, z) == Block.air.getBlockID()) world.setBlock(x + 1, y, z, Block.water.getBlockID());
			if(world.getBlock(x - 1, y, z) == Block.air.getBlockID()) world.setBlock(x - 1, y, z, Block.water.getBlockID());
			if(world.getBlock(x, y, z + 1) == Block.air.getBlockID()) world.setBlock(x, y, z + 1, Block.water.getBlockID());
			if(world.getBlock(x, y, z - 1) == Block.air.getBlockID()) world.setBlock(x, y, z - 1, Block.water.getBlockID());
		}
	}
	
}