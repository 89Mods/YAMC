package theGhastModding.meshingTest.world;

public class Chunk {
	
	private int[] blocks;
	private int chunkx;
	private int chunky;
	private int chunkz;
	
	private boolean dirty = false;
	
	public static final int CHUNK_WIDTH = 16;
	public static final int CHUNK_HEIGHT = 16;
	public static final int CHUNK_DEPTH = 16;
	
	public Chunk(int chunkx, int chunky, int chunkz) {
		this.chunkx = chunkx;
		this.chunky = chunky;
		this.chunkz = chunkz;
		blocks = new int[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_DEPTH];
	}
	
	public boolean setBlock(int x, int y, int z, int block) {
		if(x < 0 || x >= CHUNK_WIDTH || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_DEPTH) return false;
		blocks[x * (CHUNK_HEIGHT * CHUNK_DEPTH) + y * CHUNK_DEPTH + z] = block;
		markDirty();
		return true;
	}
	
	public int getBlock(int x, int y, int z) {
		if(x < 0 || x >= CHUNK_WIDTH || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_DEPTH) return 0;
		return blocks[x * (CHUNK_HEIGHT * CHUNK_DEPTH) + y * CHUNK_DEPTH + z];
	}
	
	public void markDirty() {
		this.dirty = true;
	}
	
	public void markNotDirty() {
		this.dirty = false;
	}
	
	public boolean isDirty() {
		return this.dirty;
	}
	
	public int getChunkx() {
		return chunkx;
	}
	
	public int getChunky() {
		return chunky;
	}
	
	public int getChunkz() {
		return chunkz;
	}
	
}