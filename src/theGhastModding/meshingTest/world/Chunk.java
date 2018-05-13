package theGhastModding.meshingTest.world;

public class Chunk {
	
	private int[] blocks;
	//Sunlight is stored in the first 4 bits of the lights array, torchlight in the next 4. The remaining 24 bits are reserved.
	private int[] lights;
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
		lights = new int[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_DEPTH];
	}
	
	//Sets a block and returns true if block has been set successfully
	public boolean setBlock(int x, int y, int z, int block) {
		if(x < 0 || x >= CHUNK_WIDTH || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_DEPTH) return false;
		blocks[toIndex(x, y, z)] = block;
		markDirty();
		return true;
	}
	
	public int getBlock(int x, int y, int z) {
		if(x < 0 || x >= CHUNK_WIDTH || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_DEPTH) return 0;
		return blocks[toIndex(x, y, z)];
	}
	
	public int getSunlight(int x, int y, int z) {
		if(x < 0 || x >= CHUNK_WIDTH || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_DEPTH) return 0;
		return (lights[toIndex(x, y, z)] >> 4) & 0xF; //AND the sunlight value out
	}
	
	public void setSunlight(int x, int y, int z, int val) {
		if(x < 0 || x >= CHUNK_WIDTH || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_DEPTH) return;
		val = val > 0xF ? 0xF : val; //Make sure that the light level is never larger then 15
		val = val < 0 ? 0 : val; //Also that it isn't negative
		lights[toIndex(x, y, z)] = (lights[toIndex(x, y, z)] & 0xF) | (val << 4); //OR the sunlight value in
		markDirty();
	}
	
	public int getTorchlight(int x, int y, int z) {
		if(x < 0 || x >= CHUNK_WIDTH || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_DEPTH) return 0;
		return lights[toIndex(x, y, z)] & 0xF; //AND the torchlight value out
	}
	
	public void setTorchlight(int x, int y, int z, int val) {
		if(x < 0 || x >= CHUNK_WIDTH || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_DEPTH) return;
		val = val > 0xF ? 0xF : val; //Make sure that the light level is never larger then 15
		val = val < 0 ? 0 : val; //Also that it isn't negative
		lights[toIndex(x, y, z)] = (lights[toIndex(x, y, z)] & 0xF0) | val; //OR the torchlight value in
		markDirty();
	}
	
	//Convert coordinates to array index
	private int toIndex(int x, int y, int z) {
		return x * (CHUNK_HEIGHT * CHUNK_DEPTH) + y * CHUNK_DEPTH + z;
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