package theGhastModding.meshingTest.world;

import java.util.Random;

import theGhastModding.meshingTest.world.blocks.Block;

public class World {
	
	private Chunk[] chunks;
	private int width;
	private int height;
	private int depth;
	private int chunkWidth;
	private int chunkHeight;
	private int chunkDepth;
	
	public static final int DEFAULT_WIDTH = 128;
	public static final int DEFAULT_HEIGHT = 128;
	public static final int DEFAULT_DEPTH = 128;
	
	public World(int width, int height, int depth) {
		this.width = width - (width % Chunk.CHUNK_WIDTH);
		this.height = height - (height % Chunk.CHUNK_HEIGHT);
		this.depth = depth - (depth % Chunk.CHUNK_DEPTH);
		this.chunkWidth = this.width / Chunk.CHUNK_WIDTH;
		this.chunkHeight = this.height / Chunk.CHUNK_HEIGHT;
		this.chunkDepth = this.depth / Chunk.CHUNK_DEPTH;
		this.chunks = new Chunk[chunkWidth * chunkHeight * chunkDepth];
		for(int i = 0; i < chunkWidth; i++) {
			for(int j = 0; j < chunkHeight; j++) {
				for(int k = 0; k < chunkDepth; k++) {
					this.chunks[i * (chunkHeight * chunkDepth) + j * chunkDepth + k] = new Chunk(i, j, k);
				}
			}
		}
	}
	
	//Sets a block and returns true if block has been set successfully
	public boolean setBlock(int x, int y, int z, int block) {
		if(x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) return false;
		Chunk c = getChunkAt(x, y, z);
		boolean b = c.setBlock(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz(), block);
		System.err.println(block);
		if(b) updateAllLightsOnBlockChange(x, y, z, Block.allBlocks[block].isOpaque());
		return b;
	}
	
	public int getBlock(int x, int y, int z) {
		if(x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) return 0;
		Chunk c = getChunkAt(x, y, z);
		return c.getBlock(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz());
	}
	
	//Gets the current light level of a block
	public int getLightLevel(int x, int y, int z) {
		if(x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) return 0;
		Chunk c = getChunkAt(x, y, z);
		int sunlight = c.getSunlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz());
		int torchlight = c.getTorchlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz());
		return Math.max(sunlight, torchlight);
	}
	
	public int getTorchLightLevel(int x, int y, int z) {
		if(x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) return 0;
		Chunk c = getChunkAt(x, y, z);
		return c.getTorchlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz());
	}
	
	//Private function for use in the placeLight function
	private void setTorchLightLevel(int x, int y, int z, int level) {
		Chunk c = getChunkAt(x, y, z);
		c.setTorchlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz(), level);
	}
	
	public int getSunLightLevel(int x, int y, int z) {
		if(x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) return 0;
		Chunk c = getChunkAt(x, y, z);
		return c.getSunlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz());
	}
	
	//Private function for use in the placeLight function
	private void setSunLightLevel(int x, int y, int z, int level) {
		Chunk c = getChunkAt(x, y, z);
		c.setSunlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz(), level);
	}
	
	public void placeLightSource(int x, int y, int z, int level) {
		placeLight(x, y, z, level, false);
	}
	
	private void placeLight(int x, int y, int z, int level, boolean sunlight) {
		level = level > 0xF ? 0xF : level; //Make sure that the light level is never larger then 15
		level = level < 0 ? 0 : level; //Also that it isn't negative
		//Exit conditions: exit when light can no longer be propagated further because it is blocked or weaker then other surounding lights
		if(Block.allBlocks[getBlock(x, y, z)].isOpaque()) return;
		if((sunlight ? getSunLightLevel(x, y, z) : getTorchLightLevel(x, y, z)) >= level) return;
		if(x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) return;
		//Set light level of block
		if(sunlight) {
			setSunLightLevel(x, y, z, level);
		}else {
			setTorchLightLevel(x, y, z, level);
		}
		//Exit if light has become too weak to be propagated further
		if(level == 1) return;
		//:recursion:
		placeLight(x + 1, y, z, level - 1, sunlight);
		placeLight(x - 1, y, z, level - 1, sunlight);
		placeLight(x, y + 1, z, level - 1, sunlight);
		placeLight(x, y - 1, z, level - 1, sunlight);
		placeLight(x, y, z + 1, level - 1, sunlight);
		placeLight(x, y, z - 1, level - 1, sunlight);
	}
	
	public void removeLightSource(int x, int y, int z, int originalLevel) {
		removeLight(x, y, z, originalLevel, false);
	}
	
	//originalLevel is the light level of the light to be removed
	private void removeLight(int x, int y, int z, int originalLevel, boolean sunlight) {
		//This is mostly the same
		originalLevel = originalLevel > 0xF ? 0xF : originalLevel;
		originalLevel = originalLevel < 0 ? 0 : originalLevel;
		if(Block.allBlocks[getBlock(x, y, z)].isOpaque()) return;
		//Return when a light brighter then the one to be removed is found (it must belong to a different source and should therefor not be removed)
		if((sunlight ? getSunLightLevel(x, y, z) : getTorchLightLevel(x, y, z)) > originalLevel) return;
		if(x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) return;
		//Remove light
		if(sunlight) {
			setSunLightLevel(x, y, z, 0);
		}else {
			setTorchLightLevel(x, y, z, 0);
		}
		//Same as before
		if(originalLevel == 1) return;
		removeLight(x + 1, y, z, originalLevel - 1, sunlight);
		removeLight(x - 1, y, z, originalLevel - 1, sunlight);
		removeLight(x, y + 1, z, originalLevel - 1, sunlight);
		removeLight(x, y - 1, z, originalLevel - 1, sunlight);
		removeLight(x, y, z + 1, originalLevel - 1, sunlight);
		removeLight(x, y, z - 1, originalLevel - 1, sunlight);
	}
	
	//TODO: Increasing/Decreasing sunlight can be done by just adding/substracting a value from the entire sunlight map
	
	//Sets the initial sunlight (only needs to be done once)
	public void initialSunlight(int level) {
		for(int x = 0; x < width; x++) {
			for(int z = 0; z < depth; z++) {
				for(int y = height - 1; y >= 0; y--) {
					if(Block.allBlocks[getBlock(x, y, z)].isOpaque()) break;
					placeLight(x, y, z, level, true);
				}
			}
		}
	}
	
	//Placing/removing a block may interfer with existing lights, so all lights around the block need to be updated
	//This is so bad OMG
	public void updateAllLightsOnBlockChange(int x, int y, int z, boolean opaque) {
		if(getSunLightLevel(x, y + 1, z) == 7) {
			if(!opaque) {
				//Start adding sunlight below the block
				for(int y2 = y; y2 >= 0; y2--) {
					if(Block.allBlocks[getBlock(x, y2, z)].isOpaque()) break;
					placeLight(x, y2, z, 7, true);
				}
			}else {
				//Start removing sunlight below the block
				for(int y2 = y; y2 >= 0; y2--) {
					if(Block.allBlocks[getBlock(x, y2, z)].isOpaque()) break;
					removeLight(x, y2, z, 7, true);
				}
			}
		}
		
	}
	
	public Chunk getChunk(int chunkx, int chunky, int chunkz) {
		if(chunkx < 0 || chunky >= chunkWidth || chunky < 0 || chunky >= chunkHeight || chunkz < 0 || chunkz >= chunkDepth) return null;
		return chunks[(chunkx * chunkHeight * chunkDepth) + chunky * chunkDepth + chunkz];
	}
	
	public Chunk getChunkAt(int x, int y, int z) {
		if(x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) return null;
		return getChunk(x / Chunk.CHUNK_WIDTH, y / Chunk.CHUNK_HEIGHT, z / Chunk.CHUNK_DEPTH);
	}
	
	public int getWorldHeightAt(int x, int z) {
		for(int i = 0; i < height; i++) {
			if(getBlock(x,i,z) == Block.air.getBlockID()) return i;
		}
		return height;
	}
	
	public void generate() {
		int blockCount = 0;
		Random r = new Random();
		for(int i = 0; i < depth; i++) {
			for(int j = 0; j < width; j++) {
				for(int k = 0; k < 62; k++) {
					setBlock(j, k, i, Block.stone.getBlockID()); 
					if(k == 59 && r.nextInt(10) < 4) {
						setBlock(j, k, i, Block.dirt.getBlockID());
					}
					if(k == 60) setBlock(j, k, i, Block.dirt.getBlockID());
					if(k == 61) {
						if(r.nextInt(10) < 4) {
							setBlock(j, k, i, Block.dirt.getBlockID());
							setBlock(j, k + 1, i, Block.grass.getBlockID());
							blockCount++;
						}else {
							setBlock(j, k, i, Block.grass.getBlockID());
						}
					}
					blockCount++;
				}
			}
		}
		initialSunlight(7);
		System.out.println(Integer.toString(blockCount) + " blocks in world");
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public int getChunkWidth() {
		return chunkWidth;
	}
	
	public int getChunkHeight() {
		return chunkHeight;
	}
	
	public int getChunkDepth() {
		return chunkDepth;
	}
	
}