package theGhastModding.meshingTest.world.gen;

import theGhastModding.meshingTest.world.Chunk;
import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.blocks.Block;

public abstract class WorldGenerator {
	
	protected World world;
	protected long seed = 0;
	
	public WorldGenerator(World world) {
		this.world = world;
	}
	
	public abstract void prepare();
	
	public abstract void generateChunks(Chunk[] c, int chunkx, int chunkz);
	
	public abstract void decorate(int chunkx, int chunkz);
	
	public abstract void postLightingFixes(int chunkx, int chunkz);
	
	public void parseTorchLight(int chunkx, int chunkz) {
		Chunk currChunk;
		int blockid = 0;
		Chunk[] cs = world.getMasterChunk(chunkx, chunkz);
		if(cs == null || !cs[0].hasSunlight) return;
		for(int i = 0; i < Chunk.CHUNK_WIDTH; i++) {
			 for(int j = 0; j < Chunk.CHUNK_DEPTH; j++) {
				 for(int k = 0; k < world.getHeight(); k++) {
					 currChunk = cs[k / Chunk.CHUNK_HEIGHT];
					 blockid = currChunk.getBlock(i, k %  Chunk.CHUNK_HEIGHT, j);
					 if(Block.allBlocks[blockid].getLuminoscity() != 0) {
						 world.placeLightSource(i + chunkx * 16, k, j + chunkz * 16, Block.allBlocks[blockid].getLuminoscity());
					 }
				 }
			 }
		 }
		
		//TODO: Parse light from adjacent chunks
	}
	
	public void setSeed(long newSeed) {
		this.seed = newSeed;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
}