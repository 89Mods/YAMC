package theGhastModding.meshingTest.world.blocks;

import java.util.Random;

import theGhastModding.meshingTest.world.World;

public class BlockSand extends Block {
	
	public BlockSand() {
		super(15,4);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rng) {
		update(world, x, y, z, rng);
	}
	
	@Override
	public void onNeighborChanged(World world, int x, int y, int z, int x_n, int y_n, int z_n, Random rng) {
		update(world, x, y, z, rng);
	}
	
	@Override
	public void onBlockPlaced(World world, int x, int y, int z, Random rng) {
		update(world, x, y, z, rng);
	}
	
	private synchronized void update(World world, int x, int y, int z, Random rng) {
		if(world.getBlock(x, y, z) != this.getBlockID()) return;
		int y2 = y - 1;
		while(world.getBlock(x, y2, z) == Block.air.getBlockID() || Block.allBlocks[world.getBlock(x, y2, z)].getBoundingBox() == null) {
			y2--;
		}
		y2++;
		if(y2 != y) {
			world.setBlock(x, y, z, Block.air.getBlockID());
			world.setBlock(x, y2, z, Block.sand.getBlockID());
		}
	}
	
}