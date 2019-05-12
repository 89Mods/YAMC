package theGhastModding.meshingTest.world.gen;

import java.util.Random;

import theGhastModding.meshingTest.world.World;

public abstract class WorldGenFeature {
	
	public WorldGenFeature() {
		
	}
	
	public abstract boolean generate(World world, int x, int y, int z, Random rng);
	
}