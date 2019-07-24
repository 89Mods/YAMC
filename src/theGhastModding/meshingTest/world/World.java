package theGhastModding.meshingTest.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import edu.cornell.lassp.houle.RngPack.RanMT;
import theGhastModding.meshingTest.maths.Vector2i;
import theGhastModding.meshingTest.util.BetterRandom;
import theGhastModding.meshingTest.world.blocks.Block;
import theGhastModding.meshingTest.world.gen.WorldGenerator;
import theGhastModding.meshingTest.world.gen.WorldGeneratorDefault;

public class World implements Runnable {
	
	private volatile Map<Vector2i, Chunk[]> allChunks;
	private int width;
	private int height;
	private int depth;
	private int chunkWidth;
	private int chunkHeight;
	private int chunkDepth;
	
	private long worldSeed = /*2359736052030574893L*/(System.currentTimeMillis() + System.nanoTime()) * Runtime.getRuntime().totalMemory();
	private Random interactionRNG;
	
	private WorldGenerator generator = null;
	
	private LightEngine lightEngine;
	
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
		this.allChunks = new HashMap<Vector2i, Chunk[]>();
		/*for(int i = 0; i < chunkWidth; i++) {
			for(int k = 0; k < chunkDepth; k++) {
				Chunk[] masterChunk = new Chunk[chunkHeight];
				for(int j = 0; j < chunkHeight; j++) {
					masterChunk[j] = new Chunk(i, j, k);
				}
				this.allChunks.put(new Vector2i(i, k), masterChunk);
			}
		}*/
		interactionRNG = new BetterRandom(new RanMT(worldSeed));
		this.lightEngine = new LightEngine(this);
	}
	
	public boolean setBlock(int x, int y, int z, int block) {
		return this.setBlock(x, y, z, block, true);
	}
	
	//Sets a block and returns true if block has been set successfully
	public boolean setBlock(int x, int y, int z, int block, boolean parseLight) {
		if(x < 0 || y < 0 || z < 0) return false;
		Chunk c = getChunkAt(x, y, z);
		if(c == null) return false;
		int x_c = x - Chunk.CHUNK_WIDTH * c.getChunkx();
		int y_c = y - Chunk.CHUNK_HEIGHT * c.getChunky();
		int z_c = z - Chunk.CHUNK_DEPTH * c.getChunkz();
		int prevblock = c.getBlock(x_c, y_c, z_c);
		boolean b = c.setBlock(x_c, y_c, z_c, block);
		if(!b) return b;
		if(c.hasSunlight == false || c.isDecorated == false || c.isGenerated == false) return true; //No point in parsing sunlight or doing block interations
		Block.allBlocks[block].onBlockPlaced(this, x, y, z, interactionRNG);
		if(!parseLight) return true;
		/*if(Block.allBlocks[block].isOpaque() && !Block.allBlocks[prevblock].isOpaque()) {
			int sunl = c.getAbsoluteSunlight(x_c, y_c, z_c);
			if(sunl != 0) removeLight(x, y, z, sunl, true);
		}else if(!Block.allBlocks[block].isOpaque() && Block.allBlocks[prevblock].isOpaque()) {
			
			int sunl = getAbsoluteSunLightLevel(x, y + 1, z);
			if(sunl == 15) {
				placeLight(x, y, z, 15, true);
				parseLightQueue(true);
			}else {
				placeLight(x, y, z, sunl - 1, true);
				
				sunl = getAbsoluteSunLightLevel(x + 1, y, z);
				placeLight(x, y, z, sunl - 1, true);
				
				sunl = getAbsoluteSunLightLevel(x - 1, y, z);
				placeLight(x, y, z, sunl - 1, true);
				
				
				sunl = getAbsoluteSunLightLevel(x, y - 1, z);
				placeLight(x, y, z, sunl - 1, true);
				
				sunl = getAbsoluteSunLightLevel(x, y, z + 1);
				placeLight(x, y, z, sunl - 1, true);
				
				sunl = getAbsoluteSunLightLevel(x, y, z - 1);
				placeLight(x, y, z, sunl - 1, true);
				
				parseLightQueue(true);
			}
		}*/
		lightEngine.removeLight(x, y, z, getAbsoluteSunLightLevel(x, y, z), true);
		
		if(Block.allBlocks[prevblock].getLuminoscity() != 0) {
			removeLightSource(x, y, z, Block.allBlocks[prevblock].getLuminoscity());
		}
		if(Block.allBlocks[block].getLuminoscity() != 0) {
			placeLightSource(x, y, z, Block.allBlocks[block].getLuminoscity());
		}
		return b;
	}
	
	public int getBlock(int x, int y, int z) {
		if(x < 0 || y < 0 || z < 0) return 0;
		Chunk c = getChunkAt(x, y, z);
		if(c == null) return Block.air.getBlockID();
		return c.getBlock(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz());
	}
	
	//Gets the current light level of a block
	public int getLightLevel(int x, int y, int z) {
		if(x < 0 || y < 0 || z < 0) return 0;
		Chunk c = getChunkAt(x, y, z);
		if(c == null) return 0;
		int sunlight = c.getAbsoluteSunlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz()) - (15 - sunlightLevel);
		if(sunlight < 0) sunlight = 0;
		int torchlight = c.getTorchlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz());
		return Math.max(sunlight, torchlight);
	}
	
	public int getTorchLightLevel(int x, int y, int z) {
		if(x < 0 || y < 0 || z < 0) return 0;
		Chunk c = getChunkAt(x, y, z);
		if(c == null) return 0;
		return c.getTorchlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz());
	}
	
	//Private function for use in the placeLight function
	public void setTorchLightLevel(int x, int y, int z, int level) {
		if(x < 0 || y < 0 || z < 0) return;
		Chunk c = getChunkAt(x, y, z);
		if(c == null) return;
		c.setTorchlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz(), level);
	}
	
	public int getAbsoluteSunLightLevel(int x, int y, int z) {
		if(x < 0 || y < 0 || z < 0) return 0;
		Chunk c = getChunkAt(x, y, z);
		if(c == null) return 15;
		return c.getAbsoluteSunlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz());
	}
	
	public int getSunLightLevel(int x, int y, int z) {
		if(x < 0 || y < 0 || z < 0) return 0;
		Chunk c = getChunkAt(x, y, z);
		if(c == null) return sunlightLevel;
		return c.getAbsoluteSunlight(x - Chunk.CHUNK_WIDTH * c.getChunkx(), y - Chunk.CHUNK_HEIGHT * c.getChunky(), z - Chunk.CHUNK_DEPTH * c.getChunkz()) - (15 - sunlightLevel);
	}
	
	public void placeLightSource(int x, int y, int z, int level) {
		lightEngine.placeLight(x, y, z, level, false);
	}
	
	public void removeLightSource(int x, int y, int z, int originalLevel) {
		lightEngine.removeLight(x, y, z, originalLevel, false);
	}
	
	private int sunlightLevel = 15;
	
	public synchronized void setSunlight(int level) {
		this.sunlightLevel = level;
		for(Chunk[] cs:allChunks.values()) for(Chunk c:cs) c.markDirty();
	}
	
	public Chunk getChunk(int chunkx, int chunky, int chunkz) {
		Chunk[] cs = getMasterChunk(chunkx, chunkz);
		if(cs == null) return null;
		return cs[chunky];
	}
	
	public Chunk getChunkAt(int x, int y, int z) {
		if(x < 0 || y < 0 || z < 0 || y >= height) return null;
		Chunk[] cs = getMasterChunk(x / Chunk.CHUNK_WIDTH, z / Chunk.CHUNK_DEPTH);
		if(cs == null) return null;
		return cs[y / Chunk.CHUNK_HEIGHT];
	}
	
	private Vector2i accessVect = new Vector2i(0, 0);
	
	public synchronized Chunk[] getMasterChunk(int x, int z) {
		accessVect.x = x; //Looks a bit stupid, but it's the only way to prevent vector objects from filling up RAM
		accessVect.y = z;
		return allChunks.get(accessVect);
	}
	
	public synchronized void addChunk(Chunk[] cs, Vector2i v) {
		if(allChunks.get(v) == null) {
			allChunks.put(v, cs);
		}
	}
	
	public synchronized List<Chunk[]> getAllLoadedChunks(){
		List<Chunk[]> toReturn = new ArrayList<Chunk[]>();
		for(Chunk[] cs:allChunks.values()) toReturn.add(cs);
		return toReturn;
	}
	
	public boolean isChunkLoaded(int x, int z) {
		return getMasterChunk(x, z) != null;
	}
	
	public int getWorldHeightAt(int x, int z) {
		for(int i = 0; i < height; i++) {
			if(getBlock(x,i,z) == Block.air.getBlockID()) return i;
		}
		return height;
	}
	
	private synchronized Chunk[] deleteChunk(Vector2i v) {
		return allChunks.remove(v);
	}
	
	private void unloadChunk(Vector2i v) {
		Chunk[] cs = deleteChunk(v);
		try {
			for(int i = 0; i < cs.length; i++) cs[i].save();
		}catch(Exception e) {
			System.err.println("Error saving chunks: ");
			e.printStackTrace();
			return;
		}
	}
	
	private Chunk[] loadChunk(Vector2i v) {
		Chunk[] cs = getMasterChunk(v.x, v.y);
		if(cs != null) return cs;
		cs = new Chunk[chunkHeight];
		boolean loaded = true;
		for(int i = 0; i < chunkHeight; i++) {
			cs[i] = new Chunk(v.x, i, v.y);
			if(loaded) {
				try {
					loaded = cs[i].load(true);
				} catch(Exception e) {
					System.err.println("Error loading chunk: ");
					e.printStackTrace();
					loaded = false;
				}
			}
		}
		addChunk(cs, v);
		return cs;
	}
	
	private volatile List<Vector2i> toGenerate = new ArrayList<Vector2i>();
	
	private class GeneratorThread implements Runnable {
		
		public double progress;
		private int total;
		
		public GeneratorThread() {
			
		}
		
		@Override
		public void run() {
			if(toGenerate.isEmpty()) {
				progress = 100;
				return;
			}
			List<Vector2i> toGenerateC = new ArrayList<Vector2i>();
			for(int i = 0; i < toGenerate.size(); i++) toGenerateC.add(toGenerate.get(i).clone());
			toGenerate.clear();
			
			generator.prepare();
			progress = 0;
			int origLength = toGenerateC.size() * 2;
			
			int smallestx = Integer.MAX_VALUE;
			int largestx = Integer.MIN_VALUE;
			
			int smallestz = Integer.MAX_VALUE;
			int largestz = Integer.MIN_VALUE;
			
			List<Vector2i> toDecorate = new ArrayList<Vector2i>(toGenerateC.size());
			while(!toGenerateC.isEmpty()) {
				Vector2i coords = toGenerateC.remove(toGenerateC.size() - 1);
				
				if(coords.x < smallestx) smallestx = coords.x;
				if(coords.x > largestx) largestx = coords.x;
				
				if(coords.y < smallestz) smallestz = coords.y;
				if(coords.y > largestz) largestz = coords.y;
				
				/*Chunk[] cs = getMasterChunk(coords.x, coords.y);
				if(cs == null) {
					cs = new Chunk[chunkHeight];
					for(int i = 0; i < chunkHeight; i++) cs[i] = new Chunk(coords.x, i, coords.y);
					addChunk(cs, coords);
				}*/
				Chunk[] cs = loadChunk(coords);
				toDecorate.add(coords);
				total++;
				progress = (double)total / (double)origLength * 100.0D;
				if(cs[0].isGenerated) continue;
				//If cs = null, add new chunks
				generator.generateChunks(cs, coords.x, coords.y);
				for(int i = 0; i < chunkHeight; i++) cs[i].isGenerated = true;
			}
			
			List<Vector2i> decoratedChunks = new ArrayList<Vector2i>();
			Vector2i coords2 = new Vector2i(0, 0);
			while(!toDecorate.isEmpty()) {
				Vector2i coords = toDecorate.remove(toDecorate.size() - 1);
				//If any adjacent chunk = null, add & generate that chunk (don't decorate it yet though)
				for(int i = -1; i <= 1; i++) {
					for(int j = -1; j <= 1; j++) {
						if(coords.x + i < 0 || coords.y + j < 0) continue;
						coords2.x = coords.x + i;
						coords2.y = coords.y + j;
						/*Chunk[] cs = getMasterChunk(coords2.x, coords2.y);
						if(cs == null) {
							cs = new Chunk[chunkHeight];
							for(int i2 = 0; i2 < chunkHeight; i2++) cs[i2] = new Chunk(coords2.x, i2, coords2.y);
							addChunk(cs, coords2);
						}*/
						Chunk[] cs = loadChunk(coords);
						if(!cs[0].isGenerated) {
							generator.generateChunks(cs, coords2.x, coords2.y);
							for(int i2 = 0; i2 < chunkHeight; i2++) cs[i2].isGenerated = true;
						}
					}
				}
				//TOD_O: make "isDecorated" and "isGenerated" Chunk attributes to control this
				Chunk[] cs = getMasterChunk(coords.x, coords.y);
				total++;
				progress = (double)total / (double)origLength * 100.0D;
				if(cs[0].isDecorated) continue;
				generator.decorate(coords.x, coords.y);
				for(int i = 0; i < chunkHeight; i++) cs[i].isDecorated = true;
				decoratedChunks.add(coords);
			}
			
			if(generator instanceof WorldGeneratorDefault) System.out.println(Integer.toString(((WorldGeneratorDefault)generator).totalBlocksGenerated) + " blocks added!");
			doInitialSunlight(smallestx, smallestz, largestx, largestz);
			progress = 100.0D;
			
			if(!toGenerate.isEmpty()) { //More chunks were added in the meantime
				run(); //Recursively call this function
			}
		}
		
		private void doInitialSunlight(int fromx, int fromz, int tox, int toz) {
			try {
				if(lightingThread != null && lightingThread.isAlive()) lightingThread.join();
			}catch(InterruptedException e) {
				e.printStackTrace();
				return;
			}
			lightingT = new LightingThread(fromx, fromz, tox, toz);
			lightingThread = new Thread(lightingT);
			lightingThread.start();
		}
		
	}
	
	private class LightingThread implements Runnable {
		
		public double progress;
		private int total;
		
		private int fromx,fromz,tox,toz;
		
		public LightingThread(int fromx, int fromz, int tox, int toz) {
			this.fromx = fromx;
			this.fromz = fromz;
			this.tox = tox;
			this.toz = toz;
		}
		
		@Override
		public void run() {
			if(fromx < 0) fromx = 0;
			if(fromz < 0) fromz = 0;
			for(int cx = fromx; cx < tox; cx++) {
				for(int cz = fromz; cz < toz; cz++) {
					Chunk[] currc = getMasterChunk(cx, cz);
					if(currc == null || currc[0].isDecorated == false || currc[0].isGenerated == false) continue;
					int currlvl = 15;
					if(currc[0].hasSunlight) continue; //Sunlight has obviously already been calculated for this chunk
					
					int startx = cx * 16;
					int startz = cz * 16;
					for(int x = 0; x < 16; x++) {
						for(int z = 0; z < 16; z++) {
							currlvl = 15;
							for(int y = height - 1; y >= 0; y--) {
								int blockid = getBlock(x + startx, y, z + startz);
								if(Block.allBlocks[blockid].isOpaque()) break;
								currlvl -= Block.allBlocks[blockid].getOpacity();
								if(currlvl <= 0) break;
								currc[y / Chunk.CHUNK_HEIGHT].setSunlight(x, y % Chunk.CHUNK_HEIGHT, z, currlvl);
							}
						}
					}
				}
			}
			
			//Place sunlight but also parse light from possibly existing chunks to include the new chunks
			total = 0;
			List<Vector2i> parsedChunks = new ArrayList<Vector2i>();
			for(int cx = fromx - 1; cx < tox + 1; cx++) {
				for(int cz = fromz - 1; cz < toz + 1; cz++) {
					total++;
					int currlvl = 15;
					Chunk[] mc = getMasterChunk(cx, cz);
					if(mc == null || mc[0].hasSunlight || mc[0].isDecorated == false || mc[0].isGenerated == false) continue;
					if(cx < fromx || cx >= tox || cz < fromz || cz >= toz) {
						if(!mc[0].hasSunlight) continue;
						for(Chunk c:mc) c.markDirty();
					}
					
					int endx = cx * 16 + 16;
					int endz = cz * 16 + 16;
					for(int x = cx * 16; x < endx; x++) {
						for(int z = cz * 16; z < endz; z++) {
							for(int y = height - 1; y >= 0; y--) {
								int blockid = mc[y / Chunk.CHUNK_HEIGHT].getBlock(x % Chunk.CHUNK_WIDTH, y % Chunk.CHUNK_HEIGHT, z % Chunk.CHUNK_DEPTH);
								if(Block.allBlocks[blockid].isOpaque()) continue;
								currlvl = getAbsoluteSunLightLevel(x, y, z);
								if(currlvl == 0) continue;
								//currlvl -= Block.allBlocks[blockid].getOpacity();
								if(getAbsoluteSunLightLevel(x + 1, y, z) < currlvl || getAbsoluteSunLightLevel(x - 1 , y, z) < currlvl || getAbsoluteSunLightLevel(x, y + 1, z) < currlvl || getAbsoluteSunLightLevel(x, y - 1, z) < currlvl
										|| getAbsoluteSunLightLevel(x, y, z + 1) < currlvl || getAbsoluteSunLightLevel(x, y, z - 1) < currlvl) {
									lightEngine.placeLight(x, y, z, currlvl, true);
								}
							}
						}
					}
					if(mc[0].hasSunlight == false) {
						for(Chunk c:mc) c.hasSunlight = true;
						parsedChunks.add(new Vector2i(cx, cz));
					}
					progress = (double)total / (double)((tox - fromx + 2) * (toz - fromz + 2)) * 100.0D;
				}
			}
			for(Vector2i vi:parsedChunks) {
				generator.postLightingFixes(vi.x, vi.y);
			}
			for(Vector2i vi:parsedChunks) {
				generator.parseTorchLight(vi.x, vi.y);
			}
			sunlightLevel = 15;
			generatingSpawn = false;
		}
		
	}
	
	private GeneratorThread generatorT;
	private Thread generatorThread = null;
	public boolean generatingSpawn = false;
	
	private LightingThread lightingT;
	private Thread lightingThread = null;
	
	public void startGenerateSpawnChunks(int centerx, int centerz) {
		if(generatorThread != null) return;
		generatingSpawn = true;
		for(int i = centerx - chunkWidth / 2; i < centerx + chunkWidth / 2; i++) {
			for(int j = centerz - chunkDepth / 2; j < centerz + chunkDepth / 2; j++) {
				toGenerate.add(new Vector2i(i, j));
			}
		}
		generatorT = new GeneratorThread();
		generatorThread = new Thread(generatorT);
		generatorThread.start();
	}
	
	public void startGenerate() {
		if(generatorThread.isAlive()) return;
		generatorT = new GeneratorThread();
		generatorThread = new Thread(generatorT);
		generatorThread.start();
	}
	
	public volatile boolean running = false;
	private volatile int playerx = -1;
	private volatile int playery = -1;
	private volatile int playerz = -1;
	
	public volatile Exception e = null;
	
	private List<Vector2i> toBeRemoved = new ArrayList<Vector2i>();
	
	//World ticker/updater thread
	@Override
	public void run() {
		try {
			running = true;
			playerx = playery = playerz = -1;
			int prevplayerx = -1;
			int prevplayerz = -1;
			e = null;
			long startTime;
			startTime = System.currentTimeMillis();
			long debugTime = System.currentTimeMillis();
			final long tickTime = (long)(1000.0D / 20.0D);
			int tickCntr = 0;
			while(running) {
				
				if(System.currentTimeMillis() - debugTime >= 1000 && playerx != -1 && playery != -1 && playerz != -1 && playerx != prevplayerx && playerz != prevplayerz) {
					int minx = (playerx - (width / 2)) / 16;
					int maxx = (playerx + (width / 2)) / 16;
					int minz = (playerz - (depth / 2)) / 16;
					int maxz = (playerz + (depth / 2)) / 16;
					toBeRemoved.clear();
					for(Chunk[] c:getAllLoadedChunks()) {
						
						if(c[0].getChunkx() > (maxx + 5) || c[0].getChunkz() > (maxz + 5) || c[0].getChunkx() < (minx - 5) || c[0].getChunkz() < (minz - 5)) {
							if(c[0].isDecorated) toBeRemoved.add(new Vector2i(c[0].getChunkx(), c[0].getChunkz()));
						}
					}
					for(Vector2i vi:toBeRemoved) unloadChunk(vi);
					
					int cnt = 0;
					for(int i = minx - 1; i <= maxx + 1; i++) {
						for(int j = minz - 1; j <= maxz + 1; j++) {
							if(i > 0 && j > 0 && (getMasterChunk(i, j) == null || getMasterChunk(i, j)[0].isDecorated == false)) {
								toGenerate.add(new Vector2i(i, j));
								cnt++;
							}
						}
					}
					if(cnt != 0) startGenerate();
					prevplayerx = playerx;
					prevplayerz = playerz;
				}else if(System.currentTimeMillis() - debugTime >= 1000) {
					debugTime = System.currentTimeMillis();
					System.out.println("TPS: " + Integer.toString(tickCntr));
					tickCntr = 0;
				}
				if(System.currentTimeMillis() - startTime >= tickTime) {
					startTime = System.currentTimeMillis();
					doTicks();
					tickCntr++;
				}
			}
		} catch(Exception e2) {
			e = e2;
		}
	}
	
	private void doTicks() {
		List<Chunk[]> cs = getAllLoadedChunks();
		int x = 0;
		int y = 0;
		int z = 0;
		int blockid;
		for(Chunk[] css:cs) {
			for(Chunk c:css) {
				if(c.hasSunlight == false || c.isDecorated == false || c.isGenerated == false) continue;
				for(int i = 0; i < 3; i++) {
					x = interactionRNG.nextInt(Chunk.CHUNK_WIDTH);
					y = interactionRNG.nextInt(Chunk.CHUNK_HEIGHT);
					z = interactionRNG.nextInt(Chunk.CHUNK_DEPTH);
					blockid = c.getBlock(x, y, z);
					if(blockid != 0) {
						Block.allBlocks[blockid].updateTick(this, x + c.getChunkx() * 16, y + c.getChunky() * 16, z + c.getChunkz() * 16, interactionRNG);
					}
				}
			}
		}
	}
	
	public void join() throws Exception {
		if(generatorThread != null && generatorThread.isAlive()) generatorThread.join();
		if(lightingThread != null && lightingThread.isAlive()) lightingThread.join();
	}
	
	public void updateLoadedChunks(int playerx, int playery, int playerz) {
		this.playerx = playerx;
		this.playery = playery;
		this.playerz = playerz;
	}
	
	public boolean isGenerating() {
		return generatorThread.isAlive();
	}
	
	public double getGenerationProgress() {
		if(lightingThread != null && lightingThread.isAlive()) return lightingT.progress / 2.0 + 50.0D;
		return generatorT.progress / 2.0;
	}
	
	public void saveWorld() throws Exception {
		if(!new File("World/").exists()) new File("World/").mkdirs();
		File outfile = new File("World/level.dat");
		FileOutputStream fos = new FileOutputStream(outfile);
		DataOutputStream dos = new DataOutputStream(fos);
		dos.writeLong(worldSeed);
		dos.close();
		
		List<Chunk[]> cs = getAllLoadedChunks();
		for(Chunk[] css:cs) {
			for(Chunk c:css) {
				c.save();
			}
		}
	}
	
	public void loadWorldData() throws Exception {
		File infile = new File("World/level.dat");
		if(!infile.exists()) return;
		FileInputStream fis = new FileInputStream(infile);
		DataInputStream dis = new DataInputStream(fis);
		this.worldSeed = dis.readLong();
		if(this.generator != null) this.generator.setSeed(this.worldSeed);
		dis.close();
	}
	
	public void setWorldGen(WorldGenerator gen) {
		this.generator = gen;
		this.generator.setSeed(worldSeed);
	}
	
	public WorldGenerator getWorldGen() {
		return this.generator;
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