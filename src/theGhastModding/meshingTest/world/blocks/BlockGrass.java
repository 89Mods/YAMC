package theGhastModding.meshingTest.world.blocks;

import java.util.Random;

import theGhastModding.meshingTest.world.World;

public class BlockGrass extends Block {
	
	public BlockGrass() {
		super(2,2);
	}
	
	public int getTexture(int face) {
		if(face == Block.BLOCK_FACE_LEFT || face == Block.BLOCK_FACE_RIGHT || face == Block.BLOCK_FACE_FRONT || face == Block.BLOCK_FACE_BACK) {
			return 16;
		}
		if(face == Block.BLOCK_FACE_BOTTOM) {
			return 8;
		}
		return this.defaultTextureLoc;
	}
	
	public boolean isOpaque(int face) {
		return true;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rng) {
		if((world.getAbsoluteSunLightLevel(x, y + 1, z) < 4 && world.getTorchLightLevel(x, y + 1, z) < 4) || Block.allBlocks[world.getBlock(x, y + 1, z)].isOpaque()) {
			world.setBlock(x, y, z, Block.dirt.getBlockID());
			return;
		}
		if(world.getAbsoluteSunLightLevel(x, y + 1, z) >= 9 || world.getTorchLightLevel(x, y + 1, z) >= 9) {
			int side = rng.nextInt(8);
			int x2,y2,z2;
			switch(side) {
				default:
					x2 = x;
					y2 = y;
					z2 = z;
					break;
				case 0:
					x2 = x + 1;
					y2 = y;
					z2 = z;
					break;
				case 1:
					x2 = x - 1;
					y2 = y;
					z2 = z;
					break;
				case 2:
					x2 = x;
					y2 = y;
					z2 = z + 1;
					break;
				case 3:
					x2 = x;
					y2 = y;
					z2 = z - 1;
					break;
				case 4:
					x2 = x + 1;
					y2 = y;
					z2 = z + 1;
					break;
				case 5:
					x2 = x - 1;
					y2 = y;
					z2 = z + 1;
					break;
				case 6:
					x2 = x + 1;
					y2 = y;
					z2 = z - 1;
					break;
				case 7:
					x2 = x - 1;
					y2 = y;
					z2 = z - 1;
					break;
			}
			y2 += rng.nextInt(3) - 1;
			if(world.getBlock(x2, y2, z2) == Block.dirt.getBlockID() && world.getBlock(x2, y2 + 1, z2) == Block.air.getBlockID() && world.getBlock(x2, y2 + 2, z2) == Block.air.getBlockID() && (world.getAbsoluteSunLightLevel(x2, y2 + 1, z2) >= 6 || world.getTorchLightLevel(x2, y2 + 1, z2) >= 6)) {
				world.setBlock(x2, y2, z2, Block.grass.getBlockID());
			}
		}
	}
	
}