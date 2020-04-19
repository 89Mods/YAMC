package theGhastModding.meshingTest.world.blocks;

import java.util.Random;

import theGhastModding.meshingTest.phys.AABB;
import theGhastModding.meshingTest.world.World;

public class Block {
	
	public static final int BLOCK_FACE_LEFT = 0;
	public static final int BLOCK_FACE_RIGHT = 1;
	public static final int BLOCK_FACE_FRONT = 2;
	public static final int BLOCK_FACE_BACK = 3;
	public static final int BLOCK_FACE_TOP = 4;
	public static final int BLOCK_FACE_BOTTOM = 5;
	
	public static final Block[] allBlocks = new Block[256];
	
	public static final Block air;
	public static final Block stone;
	public static final BlockGrass grass;
	public static final BlockDirt dirt;
	public static final Block glass;
	public static final Block iron;
	public static final Block gold;
	public static final Block leaves;
	public static final Block log;
	public static final Block bedrock;
	public static final Block oreGold;
	public static final Block oreIron;
	public static final Block oreCoal;
	public static final Block oreDiamond;
	public static final Block oreRedstone;
	public static final Block sand;
	public static final Block water;
	public static final Block planks;
	public static final Block bricks;
	public static final Block tnt;
	public static final Block wool;
	public static final Block lamp;
	public static final Block c1,c2,c3;
	
	static {
		air = new Block(0,0) {
			@Override
			public boolean shouldRender(int face) { return false; }
			@Override
			public boolean isOpaque() { return false; }
			@Override
			protected AABB getBoundingBox() { return null; }
		};
		stone = new Block(1,1);
		grass = new BlockGrass();
		dirt = new BlockDirt(3,8);
		glass = new Block(4,9) {
			@Override
			public boolean isOpaque() { return false; }
		};
		iron = new Block(5,10).setLuminoscity(10);
		gold = new Block(6,11);
		leaves = new BlockLeaves().setOpacity(2);
		log = new Block(8,17) {
			@Override
			public int getTexture(int face) {
				if(face == Block.BLOCK_FACE_BOTTOM || face == Block.BLOCK_FACE_TOP) {
					return 18;
				}
				return this.defaultTextureLoc;
			}
		};
		bedrock = new Block(9,3);
		oreGold = new Block(10,24);
		oreIron = new Block(11,25);
		oreCoal = new Block(12,26);
		oreDiamond = new Block(12 + 1,27);
		oreRedstone = new Block(14, 28).setLuminoscity(9);
		sand = new BlockSand();
		water = new BlockWater().setOpacity(2);
		planks = new Block(17, 29);
		bricks = new Block(18, 32);
		tnt = new BlockTNT();
		wool = new Block(20, 40/*44*/);
		lamp = new Block(21, 41).setLuminoscity(15);
		c1 = new Block(22, 42);
		c2 = new Block(23, 43);
		c3 = new Block(24, 45);
	}
	
	private int id;
	protected int defaultTextureLoc;
	protected int luminoscity = 0;
	protected int opacity = 0;
	
	private static AABB defaultHitbox = new AABB(0, 0, 0, 1, 1, 1);
	
	public Block(int id, int textureLocation) {
		this.id = id;
		this.defaultTextureLoc = textureLocation;
		allBlocks[id] = this;
	}
	
	public void updateTick(World world, int x, int y, int z, Random rng) {
		
	}
	
	public void onNeighborChanged(World world, int x, int y, int z, int x_n, int y_n, int z_n, Random rng) {
		
	}
	
	public void onBlockPlaced(World world, int x, int y, int z, Random rng) {
		
	}
	
	public int getTexture(int face) {
		return this.defaultTextureLoc;
	}
	
	public boolean shouldRender(int face) {
		return true;
	}
	
	public boolean isOpaque() {
		return true;
	}
	
	public int getBlockID() {
		return this.id;
	}
	
	public static Block getBlockFromID(int id) {
		return allBlocks[id];
	}
	
	protected Block setLuminoscity(int lum) {
		this.luminoscity = lum;
		return this;
	}
	
	public int getLuminoscity() {
		return this.luminoscity;
	}
	
	public boolean canRenderThrough() {
		return false;
	}
	
	protected Block setOpacity(int op) {
		this.opacity = op;
		return this;
	}
	
	public int getOpacity() {
		return this.opacity;
	}
	
	protected AABB getBoundingBox() {
		return defaultHitbox;
	}
	
	public AABB getBoundingBox(int x, int y, int z) {
		AABB box = getBoundingBox();
		if(box == null) return null;
		return box.moveClone(x, y, z);
	}
	
}