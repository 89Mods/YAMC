package theGhastModding.meshingTest.world.gen;

import java.util.Random;

import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.blocks.Block;

public class WorldGenTree extends WorldGenFeature {
	
	public WorldGenTree() {
		
	}
	
	@Override
	public boolean generate(World world, int x, int y, int z, Random rng) {
		if(world.getChunkAt(x, y, z) == null || world.getChunkAt(x + 5, y, z + 5) == null) return false;
		int barkHeight = 4 + rng.nextInt(3);
		for(int i = y; i < y + barkHeight; i++) {
			if(world.getBlock(x + 2, i, z + 2) != Block.air.getBlockID()) return false;
		}
		for(int i = x; i < x + 5; i++) {
			for(int j = y + barkHeight - 4; j < y + barkHeight + 1; j++) {
				for(int k = z; k < z + 5; k++) {
					if(world.getBlock(i, j, k) != Block.air.getBlockID()) return false;
				}
			}
		}
		if(world.getBlock(x + 2, y - 1, z + 2) != Block.grass.getBlockID()) return false;
		
		boolean removed = false;
		for(int i = x; i < x + 5; i++) {
			for(int j = y + barkHeight - 3; j <= y + barkHeight - 2; j++) {
				for(int k = z; k < z + 5; k++) {
					world.setBlock(i, j, k, Block.leaves.getBlockID(), false);
					if(!removed && (i - x == k - z || (i - x == 0 && k - z == 4) || (i - x == 4 && k - z == 0))) {
						if(rng.nextInt(4) == 0) {
							world.setBlock(i, j, k, Block.air.getBlockID(), false);
							removed = true;
						}
					}
				}
			}
		}
		
		for(int i = x + 1; i < x + 4; i++) {
			for(int j = y + barkHeight - 1; j <= y + barkHeight; j++) {
				world.setBlock(i, j, z + 2, Block.leaves.getBlockID(), false);
			}
		}
		for(int i = z + 1; i < z + 4; i++) {
			for(int j = y + barkHeight - 1; j <= y + barkHeight; j++) {
				world.setBlock(x + 2, j, i, Block.leaves.getBlockID(), false);
			}
		}
		
		for(int i = y; i < y + barkHeight; i++) {
			world.setBlock(x + 2, i, z + 2, Block.log.getBlockID(), false);
		}
		
		world.setBlock(x + 2, y - 1, z + 2, Block.dirt.getBlockID(), false);
		
		return true;
	}
	
}