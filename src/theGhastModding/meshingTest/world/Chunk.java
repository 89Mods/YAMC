package theGhastModding.meshingTest.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import theGhastModding.meshingTest.util.FileChannelInputStream;
import theGhastModding.meshingTest.util.FileChannelOutputStream;

public class Chunk {
	
	private volatile short[][][] blocks;
	//Sunlight is stored in the first 4 bits of the lights array, torchlight in the next 4. The remaining 24 bits are reserved.
	private volatile short[][][] lights;
	private int chunkx;
	private int chunky;
	private int chunkz;
	
	private boolean dirty = false;
	
	public volatile boolean isGenerated = false;
	public volatile boolean isDecorated = false;
	public volatile boolean hasSunlight = false;
	
	public static final int CHUNK_WIDTH = 16;
	public static final int CHUNK_HEIGHT = 16;
	public static final int CHUNK_DEPTH = 16;
	
	public Chunk(int chunkx, int chunky, int chunkz) {
		this.chunkx = chunkx;
		this.chunky = chunky;
		this.chunkz = chunkz;
		blocks = new short[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_DEPTH];
		lights = new short[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_DEPTH];
	}
	
	//Sets a block and returns true if block has been set successfully
	public synchronized boolean setBlock(int x, int y, int z, int block) {
		if(blocks[x][y][z] != block) markDirty();
		blocks[x][y][z] = (short)block;
		return true;
	}
	
	public int getBlock(int x, int y, int z) {
		return blocks[x][y][z];
	}
	
	public int getAbsoluteSunlight(int x, int y, int z) {
		return (lights[x][y][z] >> 4) & 0xF; //AND the sunlight value out
	}
	
	private short prevlight;
	
	public synchronized void setSunlight(int x, int y, int z, int val) {
		val = val > 0xF ? 0xF : val; //Make sure that the light level is never larger then 15
		val = val < 0 ? 0 : val; //Also that it isn't smaller then 1
		val &= 0xF;
		prevlight = lights[x][y][z];
		lights[x][y][z] = (short)((lights[x][y][z] & 0xF) | (val << 4)); //OR the sunlight value in
		if(prevlight != lights[x][y][z]) markDirty();
	}
	
	public int getTorchlight(int x, int y, int z) {
		return lights[x][y][z] & 0xF; //AND the torchlight value out
	}
	
	public synchronized void setTorchlight(int x, int y, int z, int val) {
		val = val > 0xF ? 0xF : val; //Make sure that the light level is never larger then 15
		val = val < 0 ? 0 : val; //Also that it isn't negative
		prevlight = lights[x][y][z];
		lights[x][y][z] = (short)((lights[x][y][z] & 0xF0) | val); //OR the torchlight value in
		if(prevlight != lights[x][y][z]) markDirty();
	}
	
	//Convert coordinates to array index
	/*private int toIndex(int x, int y, int z) {
		return x * (CHUNK_HEIGHT * CHUNK_DEPTH) + y * CHUNK_DEPTH + z;
	}*/
	
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
	
	public void save() throws Exception {
		File worldFolder = new File(String.format("World/Chunks/%dx%d/", chunkx, chunkz));
		if(!worldFolder.exists()) worldFolder.mkdirs();
		File outFile = new File(String.format("%s/%d.dat", worldFolder.getPath(), chunky));
		FileOutputStream fos = new FileOutputStream(outFile);
		FileChannelOutputStream out = new FileChannelOutputStream(fos.getChannel());
		DataOutputStream dout = new DataOutputStream(out);
		
		dout.writeInt(CHUNK_WIDTH);
		dout.writeInt(CHUNK_HEIGHT);
		dout.writeInt(CHUNK_DEPTH);
		dout.writeInt(chunkx);
		dout.writeInt(chunky);
		dout.writeInt(chunkz);
		dout.writeBoolean(dirty);
		dout.writeBoolean(isGenerated);
		dout.writeBoolean(isDecorated);
		dout.writeBoolean(hasSunlight);
		
		for(int i = 0; i < CHUNK_WIDTH; i++) {
			for(int j = 0; j < CHUNK_HEIGHT; j++) {
				for(int k = 0; k < CHUNK_DEPTH; k++) {
					dout.writeShort(blocks[i][j][k]);
					dout.writeShort(lights[i][j][k]);
				}
			}
		}
		
		dout.close();
		fos.close();
	}
	
	public boolean load(boolean forceDirty) throws Exception {
		File worldFolder = new File(String.format("World/Chunks/%dx%d/", chunkx, chunkz));
		File inFile = new File(String.format("%s/%d.dat", worldFolder.getPath(), chunky));
		if(!inFile.exists()) return false;
		FileInputStream fis = new FileInputStream(inFile);
		FileChannelInputStream in = new FileChannelInputStream(fis.getChannel());
		DataInputStream din = new DataInputStream(in);
		
		int a = din.readInt();
		int b = din.readInt();
		int c = din.readInt();
		
		int d = din.readInt();
		int e = din.readInt();
		int f = din.readInt();
		if(a != CHUNK_WIDTH || b != CHUNK_HEIGHT || c != CHUNK_DEPTH || d != chunkx || e != chunky || f != chunkz) {
			din.close();
			fis.close();
			throw new Exception("Invalid savefile format");
		}
		dirty = din.readBoolean();
		if(forceDirty) dirty = true;
		isGenerated = din.readBoolean();
		isDecorated = din.readBoolean();
		hasSunlight = din.readBoolean();
		
		for(int i = 0; i < CHUNK_WIDTH; i++) {
			for(int j = 0; j < CHUNK_HEIGHT; j++) {
				for(int k = 0; k < CHUNK_DEPTH; k++) {
					blocks[i][j][k] = din.readShort();
					lights[i][j][k] = din.readShort();
				}
			}
		}
		
		din.close();
		fis.close();
		return true;
	}
	
}