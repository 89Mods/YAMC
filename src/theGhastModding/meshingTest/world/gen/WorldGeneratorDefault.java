package theGhastModding.meshingTest.world.gen;

import java.io.File;

import edu.cornell.lassp.houle.RngPack.RanMT;
import theGhastModding.meshingTest.maths.OctaveNoise;
import theGhastModding.meshingTest.maths.OctaveNoise3D;
import theGhastModding.meshingTest.util.BetterRandom;
import theGhastModding.meshingTest.world.Chunk;
import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.blocks.Block;

public class WorldGeneratorDefault extends WorldGenerator {
	
	private OctaveNoise stoneNoise = null;
	private OctaveNoise3D noise1;
	private OctaveNoise treeNoise;
	private OctaveNoise3D caveNoise;
	private BetterRandom rng;
	
	private static final WorldGenSettings settings = new WorldGenSettings();
	
	static {
		settings.loadFromFile(new File("genSettings.txt"));
	}
	
	private int mapWidth;
	private int mapHeight;
	
	public int totalBlocksGenerated = 0;
	
	public WorldGeneratorDefault(World world) {
		super(world);
	}
	
	@Override
	public void prepare() {
		totalBlocksGenerated = 0;
		rng = new BetterRandom(new RanMT(new int[] {(int)seed, (int)(seed >> 8), (int)(seed >> 32)}));
		if(stoneNoise == null) {
			mapWidth = mapHeight = settings.noiseMapSize;
			stoneNoise = new OctaveNoise(rng, mapWidth, mapHeight, settings.stoneOctaves, settings.stoneLac, settings.stonePer);
			treeNoise = new OctaveNoise(rng, mapWidth, mapHeight, 4, 2.0, 0.5);
			caveNoise = new OctaveNoise3D(rng, mapWidth, 12, mapHeight, settings.caveOctaves, settings.caveLac, settings.cavePer);
			
			noise1 = new OctaveNoise3D(rng, mapWidth, 12, mapHeight, settings.mountainOctaves, settings.mountainLac, settings.mountainPer);
		}
	}
	
	@Override
	public void generateChunks(Chunk[] c, int chunkx, int chunkz) {
		int x = chunkx * Chunk.CHUNK_WIDTH;
		int z = chunkz * Chunk.CHUNK_DEPTH;
		long newseed = seed * ((chunkx + 1) * Short.MAX_VALUE + chunkz * Integer.MAX_VALUE);
		((RanMT)rng.getRNG()).setSeed(new int[] {(int)newseed, (int)(newseed >> 32), chunkx * Short.MAX_VALUE, chunkz * Short.MIN_VALUE, x, z});
		
		Chunk currChunk;
		for(int i = 0; i < Chunk.CHUNK_WIDTH; i++) {
			for(int j = 0; j < Chunk.CHUNK_DEPTH; j++) {
				int dirtDiff = rng.nextInt(3);
				int height = 57 + (int)Math.abs(stoneNoise.sample((x + i) / settings.scaleX, (z + j) / settings.scaleZ, settings.heightStretch));
				if(height < 0) height = Integer.MAX_VALUE - 2;
				int dirtHeight = height - 1 - dirtDiff;
				int grassHeight = height + 1;
				for(int k = 0; k <= grassHeight; k++) {
					if(k >= world.getHeight()) break;
					currChunk = c[k / Chunk.CHUNK_HEIGHT];
					currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.stone.getBlockID());
					if(k >= dirtHeight) {
						currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.dirt.getBlockID());
					}
					if(k == grassHeight) {
						currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.grass.getBlockID());
					}
					totalBlocksGenerated++;
				}
				int origHeight = height - 1;
				height = 58 + (int)Math.abs(stoneNoise.sample((x + i) / (settings.scaleX * 2.2), (z + j) / (settings.scaleZ * 2.2), settings.mountainStretch));
				if(height < 0) height = Integer.MAX_VALUE - 1;
				dirtHeight = height - 1 - dirtDiff;
				grassHeight = height + 1;
				int topblockY = 0;
				boolean isStone = false;
				if(grassHeight >= world.getHeight()) grassHeight = world.getHeight() - 1;
				if(origHeight < -1) origHeight = -1;
				for(int k = origHeight + 1; k <= grassHeight; k++) {
					if(k >= world.getHeight()) break;
					currChunk = c[k / Chunk.CHUNK_HEIGHT];
					if(currChunk.getBlock(i, k % Chunk.CHUNK_HEIGHT, j) == Block.air.getBlockID()) {
						double gradient = (k - origHeight + 1) / (height - origHeight + 1);
						if(gradient > 1) break;
						double diff = noise1.sampleNorm((x + i) / (settings.scaleX * 2.2), k / (settings.scaleY * 2.2), (z + j) / (settings.scaleZ * 2.2), 1.0) - gradient;
						if(diff >= 0.5) {
							currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.stone.getBlockID());
							topblockY = k;
							isStone = true;
							if(k >= dirtHeight) {
								currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.dirt.getBlockID());
								isStone = false;
							}
							if(k == grassHeight) {
								currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.grass.getBlockID());
								isStone = false;
							}
							totalBlocksGenerated++;
						}
					}
				}
				
				if(isStone) {
					int dirtAmountDown = 2 + rng.nextInt(2);
					int start = topblockY - dirtAmountDown;
					if(start < 0) start = 0;
					int end = topblockY;
					if(end > world.getHeight()) end = world.getHeight();
					for(int k = start; k < topblockY; k++) {
						c[k / Chunk.CHUNK_HEIGHT].setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.dirt.getBlockID());
					}
					if(topblockY >= world.getHeight()) topblockY = world.getHeight();
					c[topblockY / Chunk.CHUNK_HEIGHT].setBlock(i, topblockY % Chunk.CHUNK_HEIGHT, j, Block.grass.getBlockID());
				}else {
					if(topblockY >= world.getHeight()) topblockY = world.getHeight();
					c[topblockY / Chunk.CHUNK_HEIGHT].setBlock(i, topblockY % Chunk.CHUNK_HEIGHT, j, Block.grass.getBlockID());
				}
			}
		}
		
		//Fix some glitches with grass blocks
		for(int i = 0; i < Chunk.CHUNK_WIDTH; i++) {
			for(int j = 0; j < Chunk.CHUNK_DEPTH; j++) {
				for(int k = 0; k < world.getHeight() - 1; k++) {
					currChunk = c[k / Chunk.CHUNK_HEIGHT];
					Chunk currChunk2 = c[(k + 1) / Chunk.CHUNK_HEIGHT];
					if(currChunk.getBlock(i, k % Chunk.CHUNK_HEIGHT, j) == Block.grass.getBlockID() && Block.allBlocks[currChunk2.getBlock(i, (k + 1) % Chunk.CHUNK_HEIGHT, j)].isOpaque()) {
						currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.dirt.getBlockID());
					}
				}
			}
		}
		
		//Generate oceans and beaches
		for(int i = 0; i < Chunk.CHUNK_WIDTH; i++) {
			for(int j = 0; j < Chunk.CHUNK_DEPTH; j++) {
				for(int k = 0; k < world.getHeight() - 1; k++) {
					currChunk = c[k / Chunk.CHUNK_HEIGHT];
					if(k <= settings.seaLevel && currChunk.getBlock(i, k % Chunk.CHUNK_HEIGHT, j) == Block.air.getBlockID()) {
						currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.water.getBlockID());
					}
					if(k >= settings.seaLevel && k <= settings.seaLevel + 1 && currChunk.getBlock(i, k % Chunk.CHUNK_HEIGHT, j) == Block.grass.getBlockID()) {
						currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.sand.getBlockID());
						for(int k2 = k - 1; k2 >= 0; k2--) {
							Chunk currChunk3 = c[k2 / Chunk.CHUNK_HEIGHT];
							if(currChunk3.getBlock(i, k2 % Chunk.CHUNK_HEIGHT, j) != Block.dirt.getBlockID()) break;
							currChunk3.setBlock(i, k2 % Chunk.CHUNK_HEIGHT, j, Block.sand.getBlockID());
						}
					}
					if(k < settings.seaLevel && currChunk.getBlock(i, k % Chunk.CHUNK_HEIGHT, j) == Block.grass.getBlockID()) {
						currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.dirt.getBlockID());
					}
				}
			}
		}
		
		//Generate caves
		for(int i = 0; i < Chunk.CHUNK_WIDTH; i++) {
			for(int j = 0; j < Chunk.CHUNK_DEPTH; j++) {
				for(int k = 0; k < world.getHeight() - 1; k++) {
					currChunk = c[k / Chunk.CHUNK_HEIGHT];
					Chunk c2 = c[(k + 1) / Chunk.CHUNK_HEIGHT];
					if(caveNoise.sampleNorm((x + i) / settings.caveStretchX, k / settings.caveStretchY, (z + j) / settings.caveStretchZ, 1.0) > settings.caveThreshold + (double)k / (double)settings.seaLevel * 0.05D + (k <= 5 ? 0.05D : 0)) {
						if(currChunk.getBlock(i, k % Chunk.CHUNK_HEIGHT, j) != Block.air.getBlockID() && currChunk.getBlock(i, k % Chunk.CHUNK_HEIGHT, j) != Block.water.getBlockID() && c2.getBlock(i, (k + 1) % Chunk.CHUNK_HEIGHT, j) != Block.water.getBlockID() && c2.getBlock(i, (k + 1) % Chunk.CHUNK_HEIGHT, j) != Block.sand.getBlockID()) {
							currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.air.getBlockID());
							totalBlocksGenerated--;
						}
						//currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.stone.getBlockID());
					}else {
						//currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.air.getBlockID());
					}
				}
			}
		}
		
		//Put bedrock
		for(int i = 0; i < Chunk.CHUNK_WIDTH; i++) {
			for(int j = 0; j < Chunk.CHUNK_DEPTH; j++) {
				for(int k = 0; k < rng.nextInt(3) + 1; k++) {
					currChunk = c[0];
					currChunk.setBlock(i, k, j, Block.bedrock.getBlockID());
				}
			}
		}
	}
	
	@Override
	public void decorate(int chunkx, int chunkz) {
		WorldGenTree tree = new WorldGenTree();
		long newseed = seed * ((chunkx + 1) * Short.MAX_VALUE + chunkz * Integer.MAX_VALUE);
		((RanMT)rng.getRNG()).setSeed(new int[] {(int)newseed, (int)(newseed >> 32), chunkx * Short.MAX_VALUE, chunkz * Short.MIN_VALUE, chunkx * Chunk.CHUNK_WIDTH, chunkz * Chunk.CHUNK_DEPTH});
		
		for(int i = 0; i < settings.treeTries; i++) {
			int x = chunkx * Chunk.CHUNK_WIDTH + rng.nextInt(16);
			int y = 50 + rng.nextInt(47);
			int z = chunkz * Chunk.CHUNK_WIDTH + rng.nextInt(16);
			double spawnChance = treeNoise.sample(20000000 - x, 20000000 - z, 2.0) + 0.1;
			if(rng.nextDouble() < spawnChance) continue;
			
			tree.generate(world, x, y, z, rng);
		}
		
		generateOre(chunkx, chunkz, settings.dirtTries, 33, 0, 256, Block.dirt);
		generateOre(chunkx, chunkz, settings.coalTries, 17, 0, 128, Block.oreCoal);
		generateOre(chunkx, chunkz, settings.ironTries, 9, 0, 64, Block.oreIron);
		generateOre(chunkx, chunkz, settings.goldTries, 9, 0, 32, Block.oreGold);
		//Redstone - tries: 8, vein size: 8, min height: 0, max height: 16
		generateOre(chunkx, chunkz, settings.redstoneTries, 8, 0, 16, Block.oreRedstone);
		generateOre(chunkx, chunkz, settings.diamondTries, 8, 0, 16, Block.oreDiamond);
		
		//Cause all sand to fall
		Chunk currChunk;
		Chunk[] cs = world.getMasterChunk(chunkx, chunkz);
		for(int i = 0; i < Chunk.CHUNK_WIDTH; i++) {
			 for(int j = 0; j < Chunk.CHUNK_DEPTH; j++) {
				 for(int k = 0; k < world.getHeight(); k++) {
					 currChunk = cs[k / Chunk.CHUNK_HEIGHT];
					 if(currChunk.getBlock(i, k %  Chunk.CHUNK_HEIGHT, j) == Block.sand.getBlockID()) {
						 Block.sand.onBlockPlaced(world, i + chunkx * 16, k, j + chunkz * 16, rng);
					 }
				 }
			 }
		}
	}
	
	private void generateOre(int chunkx, int chunkz, int spawnTries, int veinSize, int minHeight, int maxHeight, Block oreBlock) {
		for(int i = 0; i < spawnTries; i++) {
			int x = chunkx * Chunk.CHUNK_WIDTH + rng.nextInt(16);
			int y = minHeight + rng.nextInt(maxHeight - minHeight + 1);
			int z = chunkz * Chunk.CHUNK_WIDTH + rng.nextInt(16);
			new WorldGenOre(oreBlock, veinSize).generate(world, x, y, z, rng);
		}
	}
	
	@Override
	public void postLightingFixes(int chunkx, int chunkz) {
		//Remove grass in low light level areas
		Chunk currChunk;
		for(int i = 0; i < Chunk.CHUNK_WIDTH; i++) {
			for(int j = 0; j < Chunk.CHUNK_DEPTH; j++) {
				for(int k = 0; k < world.getHeight() - 1; k++) {
					currChunk = world.getChunk(chunkx, k / Chunk.CHUNK_HEIGHT, chunkz);
					Chunk currChunk2 = world.getChunk(chunkx, (k + 1) / Chunk.CHUNK_HEIGHT, chunkz);
					if(currChunk.getBlock(i, k % Chunk.CHUNK_HEIGHT, j) == Block.grass.getBlockID() && currChunk2.getAbsoluteSunlight(i, (k + 1) % Chunk.CHUNK_HEIGHT, j) < 4 && currChunk2.getTorchlight(i, (k + 1) % Chunk.CHUNK_HEIGHT, j) < 4) {
						currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.dirt.getBlockID());
					}
				}
			}
		}
	}
	
}