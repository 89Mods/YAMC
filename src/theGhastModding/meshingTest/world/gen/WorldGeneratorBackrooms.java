package theGhastModding.meshingTest.world.gen;

import java.util.Random;

import org.joml.Vector3f;

import edu.cornell.lassp.houle.RngPack.RanMT;
import theGhastModding.meshingTest.sound.SoundEngine;
import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.blocks.Block;

public class WorldGeneratorBackrooms extends WorldGeneratorMaze {
	
	public WorldGeneratorBackrooms(World world) {
		super(world);
	}
	
	@Override
	public void decorate(int chunkx, int chunkz) {
		if(chunkx < 0 || chunkz < 0) return;
		int mazeChunkX = chunkx / 192;
		int mazeChunkZ = chunkz / 192;
		genMazeChunk(mazeChunkX, mazeChunkZ);
		int x = chunkx * 16;
		int z = chunkz * 16;
		for(int i = 0; i < 16; i++) {
			for(int j = 0; j < 16; j++) {
				world.setBlock(x + i, 0, z + j, Block.bedrock.getBlockID());
				world.setBlock(x + i, 1, z + j, Block.dirt.getBlockID());
				world.setBlock(x + i, 2, z + j, Block.wool.getBlockID());
				world.setBlock(x + i, 7, z + j, Block.c1.getBlockID());
			}
		}
		long newseed = seed * ((chunkx + 1) * Short.MAX_VALUE + chunkz * Integer.MAX_VALUE);
		Random rng = new RanMT(new int[] {(int)newseed, (int)(newseed >> 32), (int)this.seed, (int)(this.seed >> 32)});
		for(int i = 0; i < 6; i++) {
			int lampx = rng.nextInt(16);
			int lampz = rng.nextInt(16);
			if(world.getBlock(lampx + x, 7, lampz + z) == Block.lamp.getBlockID()) continue;
			if(world.getBlock(lampx + x, 7, lampz + z + 1) == Block.lamp.getBlockID()) continue;
			if(world.getBlock(lampx + x, 7, lampz + z + 2) == Block.lamp.getBlockID()) continue;
			world.setBlock(lampx + x, 7, lampz + z, Block.lamp.getBlockID());
			world.setBlock(lampx + x, 7, lampz + z + 1, Block.lamp.getBlockID());
			world.setBlock(lampx + x, 7, lampz + z + 2, Block.lamp.getBlockID());
			if(i == 0) {
				int id = SoundEngine.current.playSound("buzz", new Vector3f(lampx + x, 7, lampz + z + 1), true);
				SoundEngine.current.stopSound(id);
			}
		}
		
		int offsetX = x % 3;
		int offsetZ = z % 3;
		for(int i = 0; i < (offsetX == 2 ? 6 : 5); i++) {
			for(int j = 0; j < (offsetZ == 2 ? 6 : 5); j++) {
				
				int cell = cells[x / 3 + i + 1 - mazeChunkX * 1024][z / 3 + j + 1 - mazeChunkZ * 1024];
				
				for(int o = 0; o < 5; o++) {
					int id = Block.c2.getBlockID();
					if(o == 0) id = Block.c3.getBlockID();
					if((cell & 0b10000) != 0) {
						for(int j2 = 0; j2 < 4; j2++) for(int i2 = 0; i2 < 4; i2++) world.setBlock(x + i * 3 + i2 - offsetX, 3 + o, z + j * 3 + j2 - offsetZ, Block.bedrock.getBlockID());
					}else {
						if((cell & 0b0001) != 0) {
							//for(int i2 = 0; i2 < 4; i2++) world.setBlock(x + i * 3 + i2 - offsetX, 3 + o, z + j * 3 - offsetZ, id);
						}
						if((cell & 0b0010) != 0) {
							for(int i2 = 0; i2 < 4; i2++) world.setBlock(x + i * 3 + i2 - offsetX, 3 + o, z + j * 3 + 3 - offsetZ, id);
						}
						if((cell & 0b0100) != 0) {
							//for(int j2 = 0; j2 < 4; j2++) world.setBlock(x + i * 3 - offsetX, 3 + o, z + j * 3 + j2 - offsetZ, id);
						}
						if((cell & 0b1000) != 0) {
							for(int j2 = 0; j2 < 4; j2++) world.setBlock(x + i * 3 + 3 - offsetX, 3 + o, z + j * 3 + j2 - offsetZ, id);
						}
					}
				}
			}
		}
	}
	
}