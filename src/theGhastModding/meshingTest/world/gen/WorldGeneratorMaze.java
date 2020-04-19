package theGhastModding.meshingTest.world.gen;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import edu.cornell.lassp.houle.RngPack.RanMT;
import theGhastModding.meshingTest.world.Chunk;
import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.blocks.Block;

public class WorldGeneratorMaze extends WorldGenerator {
	
	protected int[][] cells;
	private int mCx = -1;
	private int mCy = -1;
	
	public int maxrun = 3;
	public boolean fixedRun = false;
	private int run = 5;
	private int currStep = run;
	private int lastDir = 0;
	public int roomcnt = 15000;
	
	public WorldGeneratorMaze(World world) {
		super(world);
	}
	
	@Override
	public void prepare() {
		
	}
	
	protected void genMazeChunk(int mCx, int mCy) {
		if(mCx == this.mCx && mCy == this.mCy) return;
		
		this.cells = new int[1028 + 2][1028 + 2];
		for(int i = -1; i < 1028 + 1; i++) {
			for(int j = -1; j < 1028 + 1; j++) {
				this.cells[i + 1][j + 1] = 0xFF;
				if(i == -1 || j == -1 || i == 1028 || j == 1028) this.cells[i + 1][j + 1] = 0b1111;
			}
		}
		
		this.mCx = mCx;
		this.mCy = mCy;
		long chunkSeed = mCx * 15485867 + mCy * 105883;
		Random rng = new RanMT(new int[] {(int)chunkSeed, (int)(chunkSeed >> 32), (int)this.seed, (int)(this.seed >> 32)});
		
		List<Rectangle> rooms = new ArrayList<Rectangle>();
		for(int i = 0; i < roomcnt; i++) {
			int roomWidth = rng.nextInt(5) + 1;
			int roomHeight = rng.nextInt(5) + 1;
			int roomX = rng.nextInt(1020 - roomWidth - 2) + roomWidth + 2;
			int roomY = rng.nextInt(1020 - roomHeight - 2) + roomHeight + 2;
			boolean canplace = true;
			for(int x = -1; x < roomWidth + 1; x++) {
				for(int y = -1; y < roomHeight + 1; y++) {
					if(x + roomX >= cells.length || y + roomY >= cells[x + roomX].length || (cells[x + roomX][y + roomY] & 0b10000) == 0) {
						canplace = false;
						x = roomWidth + 10;
						break;
					}
				}
			}
			if(canplace) {
				for(int x = -1; x < roomWidth + 1; x++) {
					for(int y = -1; y < roomHeight + 1; y++) {
						cells[x + roomX][y + roomY] = 0;
						if(x == -1) cells[x + roomX][y + roomY] |= 0b0100;
						if(x == roomWidth) cells[x + roomX][y + roomY] |= 0b1000;
						if(y == -1) cells[x + roomX][y + roomY] |= 0b0001;
						if(y == roomHeight) cells[x + roomX][y + roomY] |= 0b0010;
					}
				}
				rooms.add(new Rectangle(roomX - 1, roomY - 1, roomWidth + 2, roomHeight + 2));
			}else {
				i--;
			}
		}
		
		currStep = run;
		Stack<Integer> s = new Stack<Integer>();
		s.push((rng.nextInt(1027) + 1) + (rng.nextInt(1027) + 1) * (1028 + 2));
		final int leny_t = 1028 + 2;
		
		long startTime = System.currentTimeMillis();
		while(!s.isEmpty()) {
			int indx = s.peek();
			int x = indx / leny_t;
			int y = indx % leny_t;
			if((cells[x][y] & 0b10000) != 0) cells[x][y] &= ~0b10000;
			if((cells[x + 1][y] & 0b10000) == 0 && (cells[x][y + 1] & 0b10000) == 0 && (cells[x - 1][y] & 0b10000) == 0 && (cells[x][y - 1] & 0b10000) == 0) {
				s.pop();
				continue;
			}
			int x_new;
			int y_new;
			while(true) {
				int indx_r;
				if(currStep >= run) {
					indx_r = lastDir = rng.nextInt(4);
					if(fixedRun) run = maxrun;
					else run = rng.nextInt(maxrun) + 1;
					currStep = 0;
				}else {
					indx_r = lastDir;
				}
				currStep++;
				x_new = x;
				y_new = y;
				if(indx_r == 0) x_new++;
				if(indx_r == 1) y_new++;
				if(indx_r == 2) x_new--;
				if(indx_r == 3) y_new--;
				if((cells[x_new][y_new] & 0b10000) != 0) break;
			}
			s.push(x_new * leny_t + y_new);
			if(x_new > x) { //Break right wall
				cells[x][y] &= ~0b1000;
				cells[x_new][y_new] &= ~0b0100;
			}
			if(x_new < x) { //Break left wall
				cells[x][y] &= ~0b0100;
				cells[x_new][y_new] &= ~0b1000;
			}
			if(y_new > y) { //Break top wall
				cells[x][y] &= ~0b0010;
				cells[x_new][y_new] &= ~0b0001;
			}
			if(y_new < y) { //Break bottom wall
				cells[x][y] &= ~0b0001;
				cells[x_new][y_new] &= ~0b0010;
			}
		}
		System.out.println(String.format("Maze generated in %#.4f seconds.", (double)(System.currentTimeMillis() - startTime) / 1000.0));
		
		//Post-process
		for(int i = 1; i < 1023; i++) {
			for(int j = 1; j < 1023; j++) {
				int cell = cells[i][j];
				if((cell & 0b1111) == 0b1111) {
					if((cells[i + 1][j] & 0b10000) == 0 && (cells[i][j + 1] & 0b10000) == 0 && (cells[i - 1][j] & 0b10000) == 0 && (cells[i][j - 1] & 0b10000) == 0) continue;
					int i_new;
					int j_new;
					while(true) {
						int indx_r = rng.nextInt(4);
						i_new = i;
						j_new = j;
						if(indx_r == 0) i_new++;
						if(indx_r == 1) j_new++;
						if(indx_r == 2) i_new--;
						if(indx_r == 3) j_new--;
						if((cells[i_new][j_new] & 0b1111) != 0b1111) break;
					}
					if(i_new > i) { //Break right wall
						cells[i][j] &= ~0b1000;
						cells[i_new][j_new] &= ~0b0100;
					}
					if(i_new < i) { //Break left wall
						cells[i][j] &= ~0b0100;
						cells[i_new][j_new] &= ~0b1000;
					}
					if(j_new > j) { //Break top wall
						cells[i][j] &= ~0b0010;
						cells[i_new][j_new] &= ~0b0001;
					}
					if(j_new < j) { //Break bottom wall
						cells[i][j] &= ~0b0001;
						cells[i_new][j_new] &= ~0b0010;
					}
					cells[i][j] &= ~0b10000;
				}else {
					/*if(rng.nextInt(10) == 0) {
						int i_new = i;
						int j_new = j;
						int indx_r = rng.nextInt(4);
						if(indx_r == 0) i_new++;
						if(indx_r == 1) j_new++;
						if(indx_r == 2) i_new--;
						if(indx_r == 3) j_new--;
						if(i_new > i) { //Break right wall
							cells[i][j] &= ~0b1000;
							cells[i_new][j_new] &= ~0b0100;
						}
						if(i_new < i) { //Break left wall
							cells[i][j] &= ~0b0100;
							cells[i_new][j_new] &= ~0b1000;
						}
						if(j_new > j) { //Break top wall
							cells[i][j] &= ~0b0010;
							cells[i_new][j_new] &= ~0b0001;
						}
						if(j_new < j) { //Break bottom wall
							cells[i][j] &= ~0b0001;
							cells[i_new][j_new] &= ~0b0010;
						}
					}*/
				}
			}
		}
		
		for(int i = 0; i < rooms.size(); i++) {
			Rectangle room = rooms.get(i);
			//Up to three doorways
			for(int j = 0; j < 2; j++) {
				int doorwaySide = rng.nextInt(4);
				int doorwayLoc = rng.nextInt((doorwaySide < 2 ? room.width : room.height));
				if(doorwaySide == 0) { //Right
					if((cells[room.x + room.width][room.y + doorwayLoc] & 0b10000) == 0) {
						cells[room.x + room.width - 1][room.y + 1 + doorwayLoc] &= ~0b1000;
						cells[room.x + room.width][room.y + 1 + doorwayLoc] &= ~0b0100;
					}
				}else
				if(doorwaySide == 1) { //Left
					if((cells[room.x - 1][room.y + doorwayLoc] & 0b10000) == 0) {
						cells[room.x][room.y + doorwayLoc] &= ~0b0100;
						cells[room.x - 1][room.y + doorwayLoc] &= ~0b1000;
					}
				}else
				if(doorwaySide == 2) { //Top
					if((cells[room.x + doorwayLoc][room.y - 1] & 0b10000) == 0) {
						cells[room.x + doorwayLoc][room.y] &= ~0b0010;
						cells[room.x + doorwayLoc][room.y - 1] &= ~0b0001;
					}
				}else
				if(doorwaySide == 3) { //Bottom
					if((cells[room.x + doorwayLoc][room.y + room.height] & 0b10000) == 0) {
						cells[room.x + doorwayLoc][room.y + room.height] &= ~0b0001;
						cells[room.x + doorwayLoc][room.y + room.height - 1] &= ~0b0010;
					}
				}
			}
		}
		System.out.println(Integer.toString(rooms.size()) + " rooms generated.");
	}
	
	@Override
	public void generateChunks(Chunk[] c, int chunkx, int chunkz) {
		
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
				world.setBlock(x + i, 2, z + j, Block.iron.getBlockID());
			}
		}
		
		int offsetX = x % 3;
		int offsetZ = z % 3;
		for(int i = 0; i < (offsetX == 2 ? 6 : 5); i++) {
			for(int j = 0; j < (offsetZ == 2 ? 6 : 5); j++) {
				
				int cell = cells[x / 3 + i + 1 - mazeChunkX * 1024][z / 3 + j + 1 - mazeChunkZ * 1024];
				
				for(int o = 0; o < 5; o++) {
					int id = Block.stone.getBlockID();
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