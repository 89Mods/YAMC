package theGhastModding.meshingTest.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import edu.cornell.lassp.houle.RngPack.RanMT;
import theGhastModding.meshingTest.maths.Vector3i;
import theGhastModding.meshingTest.world.Chunk;
import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.blocks.Block;

public class WorldGeneratorHypermaze extends WorldGenerator {
	
	protected int[][][] cells;
	private int mCx = -1;
	private int mCy = -1;
	
	public int maxrun = 3;
	public boolean fixedRun = false;
	private int run = 5;
	private int currStep = run;
	private int lastDir = 0;
	public int roomcnt = 15000;
	
	public WorldGeneratorHypermaze(World world) {
		super(world);
	}
	
	@Override
	public void prepare() {
		
	}
	
	protected void genMazeChunk(int mCx, int mCy) {
		if(mCx == this.mCx && mCy == this.mCy) return;
		
		this.cells = new int[1028 + 2][90 + 2][1028 + 2];
		for(int i = -1; i < 1028 + 1; i++) {
			for(int j = -1; j < 90 + 1; j++) {
				for(int k = -1; k < 1028 + 1; k++) {
					this.cells[i + 1][j + 1][k + 1] = 0xFFF;
					if(i == -1 || j == -1 || k == -1 || i == 1028 || j == 90 || k == 1028) this.cells[i + 1][j + 1][k + 1] = 0b111111;
				}
			}
		}
		
		for(int i = -1; i < 1028 + 1; i++) {
			for(int k = -1; k < 1028 + 1; k++) {
				this.cells[i + 1][0][k + 1] = 0b111111;
				this.cells[i + 1][90 + 1][k + 1] = 0b111111;
			}
		}
		
		this.mCx = mCx;
		this.mCy = mCy;
		long chunkSeed = mCx * 15485867 + mCy * 105883;
		Random rng = new RanMT(new int[] {(int)chunkSeed, (int)(chunkSeed >> 32), (int)this.seed, (int)(this.seed >> 32)});
		
		List<Room> rooms = new ArrayList<Room>();
		for(int i = 0; i < roomcnt; i++) {
			int roomWidth = rng.nextInt(5) + 1;
			int roomHeight = rng.nextInt(5) + 1;
			int roomDepth = rng.nextInt(5) + 1;
			int roomX = rng.nextInt(1020 - roomWidth - 2) + roomWidth + 2;
			int roomY = rng.nextInt(72 - roomHeight - 2) + roomHeight + 2;
			int roomZ = rng.nextInt(1020 - roomDepth - 2) + roomHeight + 2;
			boolean canplace = true;
			for(int x = -1; x < roomWidth + 1; x++) {
				for(int y = -1; y < roomHeight + 1; y++) {
					for(int z = -1; z < roomDepth + 1; z++) {
						if(x + roomX >= cells.length || y + roomY >= cells[x + roomX].length || z + roomZ >= cells[z + roomZ].length || (cells[x + roomX][y + roomY][z + roomZ] & 0b1000000) == 0) {
							canplace = false;
							x = roomWidth + 10;
							break;
						}
					}
				}
			}
			if(canplace) {
				for(int x = -1; x < roomWidth + 1; x++) {
					for(int y = -1; y < roomHeight + 1; y++) {
						for(int z = -1; z < roomDepth + 1; z++) {
							cells[x + roomX][y + roomY][z + roomZ] = 0;
							if(x == -1) cells[x + roomX][y + roomY][z + roomZ] |= 0b0100;
							if(x == roomWidth) cells[x + roomX][y + roomY][z + roomZ] |= 0b1000;
							if(y == -1) cells[x + roomX][y + roomY][z + roomZ] |= 0b0001;
							if(y == roomHeight) cells[x + roomX][y + roomY][z + roomZ] |= 0b0010;
							if(z == -1) cells[x + roomX][y + roomY][z + roomZ] |= 0b010000;
							if(z == roomDepth) cells[x + roomX][y + roomY][z + roomZ] |= 0b100000;
						}
					}
				}
				rooms.add(new Room(roomX - 1, roomY - 1, roomZ - 1, roomWidth + 2, roomHeight + 2, roomDepth + 2));
			}else {
				i--;
			}
		}
		
		currStep = run;
		Stack<Vector3i> s = new Stack<Vector3i>();
		s.push(new Vector3i(rng.nextInt(1027) + 1, rng.nextInt(88) + 2, rng.nextInt(1027) + 1));
		//final int leny_t = 1028 + 2;
		//final int leny_w = (1028 + 2) * (772 + 2);
		
		long startTime = System.currentTimeMillis();
		while(!s.isEmpty()) {
			Vector3i indx = s.peek();
			int x = indx.x;
			int y = indx.y;
			int z = indx.z;
			
			if((cells[x][y][z] & 0b1000000) != 0) cells[x][y][z] &= ~0b1000000;
			if((cells[x + 1][y][z] & 0b1000000) == 0 && (cells[x][y + 1][z] & 0b1000000) == 0 && (cells[x][y][z + 1] & 0b1000000) == 0 && (cells[x - 1][y][z] & 0b1000000) == 0 && (cells[x][y - 1][z] & 0b1000000) == 0 && (cells[x][y][z - 1] & 0b1000000) == 0) {
				s.pop();
				continue;
			}
			int x_new;
			int y_new;
			int z_new;
			while(true) {
				int indx_r;
				if(currStep >= run) {
					indx_r = lastDir = rng.nextInt(6);
					if(fixedRun) run = maxrun;
					else run = rng.nextInt(maxrun) + 1;
					currStep = 0;
				}else {
					indx_r = lastDir;
				}
				currStep++;
				x_new = x;
				y_new = y;
				z_new = z;
				if(indx_r == 0) x_new++;
				if(indx_r == 1) y_new++;
				if(indx_r == 2) z_new++;
				if(indx_r == 3) x_new--;
				if(indx_r == 4) y_new--;
				if(indx_r == 5) z_new--;
				if((cells[x_new][y_new][z_new] & 0b1000000) != 0) break;
			}
			s.push(new Vector3i(x_new, y_new, z_new));
			if(x_new > x) { //Break right wall
				cells[x][y][z] &= ~0b1000;
				cells[x_new][y_new][z_new] &= ~0b0100;
			}
			if(x_new < x) { //Break left wall
				cells[x][y][z] &= ~0b0100;
				cells[x_new][y_new][z_new] &= ~0b1000;
			}
			if(y_new > y) { //Break top wall
				cells[x][y][z] &= ~0b100000;
				cells[x_new][y_new][z_new] &= ~0b010000;
			}
			if(y_new < y) { //Break bottom wall
				cells[x][y][z] &= ~0b010000;
				cells[x_new][y_new][z_new] &= ~0b100000;
			}
			if(z_new > z) { //Break front wall
				cells[x][y][z] &= ~0b0010;
				cells[x_new][y_new][z_new] &= ~0b0001;
			}
			if(z_new < z) { //Break back wall
				cells[x][y][z] &= ~0b0001;
				cells[x_new][y_new][z_new] &= ~0b0010;
			}
		}
		System.out.println(String.format("Maze generated in %#.4f seconds.", (double)(System.currentTimeMillis() - startTime) / 1000.0));
		
		//Post-process
		for(int i = 1; i < 1023; i++) {
			for(int j = 1; j < 85; j++) {
				for(int k = 1; k < 1023; k++) {
					int cell = cells[i][j][k];
					if((cell & 0b111111) == 0b111111) {
						if((cells[i + 1][j][k] & 0b1000000) == 0 && (cells[i][j + 1][k] & 0b1000000) == 0 && (cells[i][j][k + 1] & 0b1000000) == 0 && (cells[i - 1][j][k] & 0b1000000) == 0 && (cells[i][j - 1][k] & 0b1000000) == 0 && (cells[i][j][k - 1] & 0b1000000) == 0) continue;
						int i_new;
						int j_new;
						int k_new;
						while(true) {
							int indx_r = rng.nextInt(6);
							i_new = i;
							j_new = j;
							k_new = k;
							if(indx_r == 0) i_new++;
							if(indx_r == 1) j_new++;
							if(indx_r == 2) k_new++;
							if(indx_r == 3) i_new--;
							if(indx_r == 4) j_new--;
							if(indx_r == 5) k_new--;
							if((cells[i_new][j_new][k_new] & 0b111111) != 0b111111) break;
						}
						if(i_new > i) { //Break right wall
							cells[i][j][k] &= ~0b1000;
							cells[i_new][j_new][k_new] &= ~0b0100;
						}
						if(i_new < i) { //Break left wall
							cells[i][j][k] &= ~0b0100;
							cells[i_new][j_new][k_new] &= ~0b1000;
						}
						if(j_new > j) { //Break top wall
							cells[i][j][k] &= ~0b100000;
							cells[i_new][j_new][k_new] &= ~0b010000;
						}
						if(j_new < j) { //Break bottom wall
							cells[i][j][k] &= ~0b010000;
							cells[i_new][j_new][k_new] &= ~0b100000;
						}
						if(k_new > k) { //Break front wall
							cells[i][j][k] &= ~0b0010;
							cells[i_new][j_new][k_new] &= ~0b0001;
						}
						if(k_new < k) { //Break back wall
							cells[i][j][k] &= ~0b0001;
							cells[i_new][j_new][k_new] &= ~0b0010;
						}
						cells[i][j][k] &= ~0b1000000;
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
		}
		
		/*for(int o = 0; o < 10; o++) {
			BufferedImage img = new BufferedImage(128 * 3 + 3, 128 * 3 + 3, BufferedImage.TYPE_INT_RGB);
			for(int i = 0; i < 128 * 3 + 3; i++) {
				for(int j = 0; j < 128 * 3 + 3; j++) {
					img.setRGB(i, j, Color.WHITE.getRGB());
				}
			}
			
			for(int i = 0; i < 128; i++) {
				for(int j = 0; j < 128; j++) {
					int cell = cells[i][o][j];
					if((cell & 0b0001) != 0) {
						for(int i2 = 0; i2 < 4; i2++) img.setRGB(i * 3 + i2, j * 3, Color.BLACK.getRGB());
					}
					if((cell & 0b0100) != 0) {
						for(int j2 = 0; j2 < 4; j2++) img.setRGB(i * 3, j * 3 + j2, Color.BLACK.getRGB());
					}
				}
			}
			
			try {
				ImageIO.write(img, "png", new File("testimg" + Integer.toString(o) + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		
		for(int i = 0; i < rooms.size(); i++) {
			Room room = rooms.get(i);
			//Up to three doorways
			for(int j = 0; j < 2; j++) {
				int doorwaySide = rng.nextInt(4);
				int doorwayLoc = rng.nextInt((doorwaySide < 2 ? room.width : room.depth));
				int doorwayHeight = rng.nextInt(room.height - 1) + room.y + 1;
				if(doorwaySide == 0) { //Right
					if((cells[room.x + room.width][doorwayHeight][room.y + doorwayLoc] & 0b10000) == 0) {
						cells[room.x + room.width - 1][doorwayHeight][room.y + 1 + doorwayLoc] &= ~0b1000;
						cells[room.x + room.width][doorwayHeight][room.y + 1 + doorwayLoc] &= ~0b0100;
					}
				}else
				if(doorwaySide == 1) { //Left
					if((cells[room.x - 1][doorwayHeight][room.y + doorwayLoc] & 0b10000) == 0) {
						cells[room.x][doorwayHeight][room.y + doorwayLoc] &= ~0b0100;
						cells[room.x - 1][doorwayHeight][room.y + doorwayLoc] &= ~0b1000;
					}
				}else
				if(doorwaySide == 2) { //Top
					if((cells[room.x + doorwayLoc][doorwayHeight][room.y - 1] & 0b10000) == 0) {
						cells[room.x + doorwayLoc][doorwayHeight][room.y] &= ~0b0010;
						cells[room.x + doorwayLoc][doorwayHeight][room.y - 1] &= ~0b0001;
					}
				}else
				if(doorwaySide == 3) { //Bottom
					if((cells[room.x + doorwayLoc][doorwayHeight][room.y + room.height] & 0b10000) == 0) {
						cells[room.x + doorwayLoc][doorwayHeight][room.y + room.height] &= ~0b0001;
						cells[room.x + doorwayLoc][doorwayHeight][room.y + room.height - 1] &= ~0b0010;
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
		for(int y = 0; y < world.getHeight() - 3; y+=3) {
			//int offsetY = y % 3;
			for(int i = 0; i < (offsetX == 2 ? 6 : 5); i++) {
					for(int k = 0; k < (offsetZ == 2 ? 6 : 5); k++) {
						
						int cell = cells[x / 3 + i + 1 - mazeChunkX * 1024][y / 3 + 1][z / 3 + k + 1 - mazeChunkZ * 1024];
						
						for(int o = 0; o < 3; o++) {
							int id = Block.stone.getBlockID();
							if((cell & 0b1000000) != 0) {
								for(int j2 = 0; j2 < 4; j2++) for(int i2 = 0; i2 < 4; i2++) world.setBlock(x + i * 3 + i2 - offsetX, 3 + o + y, z + k * 3 + j2 - offsetZ, Block.bedrock.getBlockID());
							}else {
								if((cell & 0b0010) != 0) {
									for(int i2 = 0; i2 < 4; i2++) world.setBlock(x + i * 3 + i2 - offsetX, 3 + o + y, z + k * 3 + 3 - offsetZ, id);
								}
								if((cell & 0b1000) != 0) {
									for(int j2 = 0; j2 < 4; j2++) world.setBlock(x + i * 3 + 3 - offsetX, 3 + o + y, z + k * 3 + j2 - offsetZ, id);
								}
								if((cell & 0b100000) != 0) {
									for(int j2 = 0; j2 < 4; j2++) {
										for(int k2 = 0; k2 < 4; k2++) {
											world.setBlock(x + i * 3 + k2 - offsetX, 3 + y + 3, z + k * 3 + j2 - offsetZ, Block.lamp.getBlockID());
										}
									}
								}
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
					
					/*if(world.getBlock(i + chunkx * 16, k, j + chunkz * 16) == Block.air.getBlockID()) {
						world.setBlock(i + chunkx * 16, k, j + chunkz * 16, Block.iron.getBlockID());
					}else {
						world.setBlock(i + chunkx * 16, k, j + chunkz * 16, Block.air.getBlockID());
					}*/
					
					currChunk = world.getChunk(chunkx, k / Chunk.CHUNK_HEIGHT, chunkz);
					Chunk currChunk2 = world.getChunk(chunkx, (k + 1) / Chunk.CHUNK_HEIGHT, chunkz);
					if(currChunk.getBlock(i, k % Chunk.CHUNK_HEIGHT, j) == Block.grass.getBlockID() && currChunk2.getAbsoluteSunlight(i, (k + 1) % Chunk.CHUNK_HEIGHT, j) < 4 && currChunk2.getTorchlight(i, (k + 1) % Chunk.CHUNK_HEIGHT, j) < 4) {
						currChunk.setBlock(i, k % Chunk.CHUNK_HEIGHT, j, Block.dirt.getBlockID());
					}
				}
			}
		}
	}
	
	private class Room {
		
		public int x,y,width,height,depth;

		public Room(int x, int y, int z, int width, int height, int depth) {
			super();
			this.x = x;
			this.y = y;
			//this.z = z;
			this.width = width;
			this.height = height;
			this.depth = depth;
		}
		
	}
	
}