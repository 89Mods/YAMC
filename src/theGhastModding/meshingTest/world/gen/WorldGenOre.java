package theGhastModding.meshingTest.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.blocks.Block;

public class WorldGenOre extends WorldGenFeature {
	
	private Block oreBlock;
	private int veinSize;
	
	public WorldGenOre(Block oreBlock, int veinSize) {
		this.oreBlock = oreBlock;
		this.veinSize = veinSize;
	}
	
	@Override
	public boolean generate(World world, int x, int y, int z, Random rng) {
		if(world.getBlock(x, y, z) != Block.stone.getBlockID()) return false;
		
		world.setBlock(x, y, z, oreBlock.getBlockID(), false);
		int finalVeinSize = Math.min(2, veinSize);
		finalVeinSize += rng.nextInt(veinSize - finalVeinSize + 1);
		
		int maxDist = (int)Math.sqrt(veinSize * veinSize * 2) + 1;
		
		List<int[]> arr = new ArrayList<int[]>();
		arr.add(new int[] {x + 1, y, z});
		arr.add(new int[] {x - 1, y, z});
		arr.add(new int[] {x, y + 1, z});
		arr.add(new int[] {x, y - 1, z});
		arr.add(new int[] {x, y, z + 1});
		arr.add(new int[] {x, y, z - 1});
		while(true) {
			int[] r = arr.remove(rng.nextInt(arr.size()));
			if(world.getBlock(r[0], r[1], r[2]) == Block.stone.getBlockID()) {
				world.setBlock(r[0], r[1], r[2], oreBlock.getBlockID(), false);
				finalVeinSize--;
				if(finalVeinSize == 0) break;
			}
			int[] n = new int[] {r[0] + 1, r[1], r[2]};
			if(dist(x, y, z, n) <= maxDist) arr.add(n);
			n = new int[] {r[0] - 1, r[1], r[2]};
			if(dist(x, y, z, n) <= maxDist) arr.add(n);
			
			n = new int[] {r[0], r[1] + 1, r[2]};
			if(dist(x, y, z, n) <= maxDist) arr.add(n);
			n = new int[] {r[0], r[1] - 1, r[2]};
			if(dist(x, y, z, n) <= maxDist) arr.add(n);
			
			n = new int[] {r[0], r[1], r[2] + 1};
			if(dist(x, y, z, n) <= maxDist) arr.add(n);
			n = new int[] {r[0], r[1], r[2] - 1};
			if(dist(x, y, z, n) <= maxDist) arr.add(n);
			
			if(arr.isEmpty()) break;
		}
		
		return true;
	}
	
	private int abs(int x) {
		return x < 0 ? -x : x;
	}
	
	private int dist(int x, int y, int z, int[] b) {
		return abs((int)Math.sqrt( (x - b[0]) * (x - b[0]) + (y - b[1]) * (y - b[1]) + (z - b[2]) * (z - b[2])));
	}
	
}