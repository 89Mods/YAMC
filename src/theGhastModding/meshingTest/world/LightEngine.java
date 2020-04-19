package theGhastModding.meshingTest.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import theGhastModding.meshingTest.world.blocks.Block;

public class LightEngine {
	
	private World world;
	
	public LightEngine(World w) {
		this.world = w;
	}
	
	private class LightSource {
		
		public int x,y,z,level,chunkx,chunky,chunkz;
		public Chunk chunk;
		private boolean oob = false;
		
		public LightSource(int x, int y, int z, int level) {
			this.set(x, y, z, level);
		}
		
		public void set(int x, int y, int z, int level) {
			this.x = x; this.y = y; this.z = z; this.level = level;
			oob = false;
			this.chunk = world.getChunkAt(x, y, z);
			if(this.chunk != null && this.chunk.isGenerated && this.chunk.isDecorated) {
				this.chunkx = x - Chunk.CHUNK_WIDTH * chunk.getChunkx();
				this.chunky = y - Chunk.CHUNK_HEIGHT * chunk.getChunky();
				this.chunkz = z - Chunk.CHUNK_DEPTH * chunk.getChunkz();
			}else {
				this.chunkx = this.chunky = this.chunkz = -1;
				oob = true;
			}
		}
		
		public int getCurrLevel(boolean sunlight) {
			if(oob && sunlight) return 0;
			if(oob && !sunlight) return 0;
			return (sunlight ? chunk.getAbsoluteSunlight(chunkx, chunky, chunkz) : chunk.getTorchlight(chunkx, chunky, chunkz));
		}
		
		public boolean shouldPlace() {
			return !Block.allBlocks[chunk.getBlock(chunkx, chunky, chunkz)].isOpaque();
		}
		
		public void checkOpacity() {
			this.level -= Block.allBlocks[chunk.getBlock(chunkx, chunky, chunkz)].getOpacity();
		}
		
		public void place(boolean sunlight) {
			if(oob) return;
			if(sunlight) chunk.setSunlight(chunkx, chunky, chunkz, level); else chunk.setTorchlight(chunkx, chunky, chunkz, level);
		}
		
		public boolean equals(Object o) {
			if(!(o instanceof LightSource)) return false;
			LightSource other = (LightSource)o;
			if(other.x == this.x && other.y == this.y && other.z == this.z && other.level == this.level) return true;
			return false;
		}
		
	}
	
	private List<LightSource> reusableSources = new ArrayList<LightSource>(15 * 15);
	
	//private List<LightSource> lightQueue = new ArrayList<LightSource>(15 * 15);
	private Stack<LightSource> lightQueue = new Stack<LightSource>();
	
	private void checkNeighborLight(int x, int y, int z, int level, boolean sunlight) {
		if(level <= 1) return;
		LightSource ls;
		if(reusableSources.isEmpty()) {
			ls = new LightSource(x, y, z, level - 1);
		}else {
			ls = reusableSources.remove(reusableSources.size() - 1);
			ls.set(x, y, z, level - 1);
		}
		if(ls.chunk == null) {
			reusableSources.add(ls);
			return;
		}
		if(!ls.shouldPlace()) {
			reusableSources.add(ls);
			return;
		}
		if(ls.getCurrLevel(sunlight) + 2 > level) {
			reusableSources.add(ls);
			return;
		}
		ls.checkOpacity();
		lightQueue.push(ls);
	}
	
	public synchronized void placeLight(int x, int y, int z, int level, boolean sunlight) {
		
		LightSource ls;
		if(reusableSources.isEmpty()) {
			ls = new LightSource(x, y, z, level);
		}else {
			ls = reusableSources.remove(reusableSources.size() - 1);
			ls.set(x, y, z, level);
		}
		lightQueue.push(ls);
		
		parseLightQueue(sunlight);
	}
	
	private synchronized void parseLightQueue(boolean sunlight) {
		int level,x,y,z;
		while(!lightQueue.isEmpty()) {
			LightSource ls = lightQueue.pop();
			level = ls.level;
			x = ls.x;
			y = ls.y;
			z = ls.z;
			ls.place(sunlight);
			checkNeighborLight(x - 1, y, z, level, sunlight);
			checkNeighborLight(x + 1, y, z, level, sunlight);
			checkNeighborLight(x, y - 1, z, sunlight ? (level == 15 ? 16 : level) : level, sunlight);
			checkNeighborLight(x, y + 1, z, level, sunlight);
			checkNeighborLight(x, y, z - 1, level, sunlight);
			checkNeighborLight(x, y, z + 1, level, sunlight);
			if(reusableSources.size() < 512) reusableSources.add(ls);
		}
	}
	
	private class LightRemoval {
		
		public int x,y,z,level,chunkx,chunky,chunkz;
		public Chunk chunk;
		private boolean oob = false;
		
		public LightRemoval(int x, int y, int z, int level) {
			this.x = x; this.y = y; this.z = z; this.level = level;
			this.chunk = world.getChunkAt(x, y, z);
			if(this.chunk != null) {
				this.chunkx = x - Chunk.CHUNK_WIDTH * chunk.getChunkx();
				this.chunky = y - Chunk.CHUNK_HEIGHT * chunk.getChunky();
				this.chunkz = z - Chunk.CHUNK_DEPTH * chunk.getChunkz();
			}else {
				this.chunkx = this.chunky = this.chunkz = -1;
				oob = true;
			}
		}
		
		public int getCurrLevel(boolean sunlight) {
			if(oob && sunlight) return 15;
			if(oob && !sunlight) return 0;
			return (sunlight ? chunk.getAbsoluteSunlight(chunkx, chunky, chunkz) : chunk.getTorchlight(chunkx, chunky, chunkz));
		}
		
		public boolean shouldPlace() {
			return !Block.allBlocks[chunk.getBlock(chunkx, chunky, chunkz)].isOpaque();
		}
		
		public void place(boolean sunlight) {
			if(oob) return;
			if(sunlight) chunk.setSunlight(chunkx, chunky, chunkz, 0); else chunk.setTorchlight(chunkx, chunky, chunkz, 0);
		}
		
	}
	
	private List<LightRemoval> lightRemovalQueue = new ArrayList<LightRemoval>(15 * 15);
	private List<LightSource> tempLightQueue = new ArrayList<LightSource>();
	
	private void checkNeighborLightRemoval(int x, int y, int z, int originalLevel, boolean sunlight) {
		LightRemoval lr = new LightRemoval(x, y, z, originalLevel - 1);
		if(lr.chunk == null) return;
		if(!lr.shouldPlace()) return;
		int currLevel = lr.getCurrLevel(sunlight);
		if(currLevel != 0 && currLevel < originalLevel) {
			lightRemovalQueue.add(new LightRemoval(x, y, z, currLevel));
			return;
		}else if(currLevel >= originalLevel){
			tempLightQueue.add(new LightSource(x, y, z, currLevel));
		}
	}
	
	public synchronized void removeLight(int x, int y, int z, int originalLevel, boolean sunlight) {
		
		lightRemovalQueue.add(new LightRemoval(x, y, z, originalLevel));
		
		while(!lightRemovalQueue.isEmpty()) {
			LightRemoval lr = lightRemovalQueue.remove(lightRemovalQueue.size() - 1);
			originalLevel = lr.level;
			x = lr.x;
			y = lr.y;
			z = lr.z;
			boolean b = false;
			if(sunlight && lr.getCurrLevel(true) == 15) {
				lightRemovalQueue.add(new LightRemoval(x, y - 1, z, 14));
				b = true;
			}
			lr.place(sunlight);
			checkNeighborLightRemoval(x - 1, y, z, originalLevel, sunlight);
			checkNeighborLightRemoval(x + 1, y, z, originalLevel, sunlight);
			if(!b) checkNeighborLightRemoval(x, y - 1, z, originalLevel, sunlight);
			checkNeighborLightRemoval(x, y + 1, z, originalLevel, sunlight);
			checkNeighborLightRemoval(x, y, z - 1, originalLevel, sunlight);
			checkNeighborLightRemoval(x, y, z + 1, originalLevel, sunlight);
		}
		
		for(int i = 0; i < tempLightQueue.size(); i++) {
			if(tempLightQueue.get(i).getCurrLevel(sunlight) >= tempLightQueue.get(i).level) {
				lightQueue.push(tempLightQueue.get(i));
			}
		}
		tempLightQueue.clear();
		parseLightQueue(sunlight);
	}
	
}