package theGhastModding.meshingTest.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.joml.Vector2f;

import theGhastModding.meshingTest.maths.Vector3i;
import theGhastModding.meshingTest.resources.BaseModel;
import theGhastModding.meshingTest.resources.Loader;
import theGhastModding.meshingTest.resources.textures.BlockTexturemap;
import theGhastModding.meshingTest.world.blocks.Block;

public class WorldMesher implements Runnable {
	
	private World world;
	private Loader loader;
	private Thread thread;
	public Exception e = null;
	
	public WorldMesher(World world, Loader loader, BlockTexturemap texturemap) {
		this.world = world;
		this.loader = loader;
		this.allMeshes = new HashMap<Vector3i, ChunkMesh>();
		meshers = new ChunkMesher[512];
		for(int i = 0; i < meshers.length; i++) meshers[i] = new ChunkMesher(loader, texturemap, world);
		/*for(int i = 0; i < world.getChunkWidth(); i++) {
			for(int j = 0; j < world.getChunkHeight(); j++) {
				for(int k = 0; k < world.getChunkDepth(); k++) {
					meshers[getChunkIndex(i, j, k)] = new ChunkMesher(loader, texturemap, world);
				}
			}
		}*/
	}
	
	private static final float[] leftFace = {
			-0.5f,0f,-0.5f,	
			-0.5f,-1f,-0.5f,	
			0.5f,-1f,-0.5f,	
			0.5f,0f,-0.5f,
	};
	
	private static final int[] leftIndices = {
			3,1,0,	
			2,1,3,
	};
	
	private static final float[] leftNormals = {
			0,-0.5f,-1,
			0,-0.5f,-1,
			0,-0.5f,-1,
			0,-0.5f,-1,
	};
	
	private static final float[] leftTextureCoords = {
			0,0,
			0,1,
			1,1,
			1,0,
	};
	
	private static final float[] rightFace = {
			-0.5f,0f,0.5f,	
			-0.5f,-1f,0.5f,	
			0.5f,-1f,0.5f,	
			0.5f,0f,0.5f,
	};
	
	private static final int[] rightIndices = {
			0,1,3,
			3,1,2,
	};
	
	private static final float[] rightNormals = {
			0,-0.5f,1,
			0,-0.5f,1,
			0,-0.5f,1,
			0,-0.5f,1,
	};
	
	private static final float[] rightTextureCoords = {
			0,0,
			0,1,
			1,1,
			1,0,
	};
	
	private static final float[] frontFace = {
			0.5f,0f,-0.5f,	
			0.5f,-1f,-0.5f,	
			0.5f,-1f,0.5f,	
			0.5f,0f,0.5f,
	};
	
	private static final int[] frontIndices = {
			3,1,0,
			2,1,3,	
	};
	
	private static final float[] frontNormals = {
			1,-0.5f,0,
			1,-0.5f,0,
			1,-0.5f,0,
			1,-0.5f,0,
	};
	
	private static final float[] frontTextureCoords = {
			0,0,
			0,1,
			1,1,
			1,0,
	};
	
	private static final float[] backFace = {
			-0.5f,0f,-0.5f,	
			-0.5f,-1f,-0.5f,	
			-0.5f,-1f,0.5f,	
			-0.5f,0f,0.5f,
	};
	
	private static final int[] backIndices = {
			0,1,3,
			3,1,2,
	};
	
	private static final float[] backNormals = {
			-1,-0.5f,0,
			-1,-0.5f,0,
			-1,-0.5f,0,
			-1,-0.5f,0,
	};
	
	private static final float[] backTextureCoords = {
			0,0,
			0,1,
			1,1,
			1,0,
	};
	
	private static final float[] topFace = {
			-0.5f,0f,0.5f,
			-0.5f,0f,-0.5f,
			0.5f,0f,-0.5f,
			0.5f,0f,0.5f,
	};
	
	private static final int[] topIndices = {
			3,1,0,
			2,1,3,
	};
	
	private static final float[] topNormals = {
			0,0.5f,0,
			0,0.5f,0,
			0,0.5f,0,
			0,0.5f,0,
	};
	
	private static final float[] topTextureCoords = {
			0,0,
			0,1,
			1,1,
			1,0,
	};
	
	private static final float[] bottomFace = {
			-0.5f,-1f,0.5f,
			-0.5f,-1f,-0.5f,
			0.5f,-1f,-0.5f,
			0.5f,-1f,0.5f
	};
	
	private static final int[] bottomIndices = {
			0,1,3,
			3,1,2
	};
	
	private static final float[] bottomNormals = {
			0,-1.5f,0,
			0,-1.5f,0,
			0,-1.5f,0,
			0,-1.5f,0,
	};
	
	private static final float[] bottomTextureCoords = {
			0,0,
			0,1,
			1,1,
			1,0
	};
	
	public BaseModel getBlockPreview(int blockid, BlockTexturemap texturemap) throws Exception {
		
		float[] vertices = new float[72];
		int[] indices = new int[36];
		float[] textureCoords = new float[48];
		float[] normals = new float[72];
		float[] lightLevels = new float[72];
		
		int[] actualface = new int[] {2,3,1,0,4,5};
		for(int f = 0; f < 6; f++) {
			int i = actualface[f];
			Vector2f offsetTextureCoords = texturemap.textureLocationToCoordinates(Block.allBlocks[blockid].getTexture(i));
			for(int j = 0; j < faceVertices[0].length; j++) vertices[f * faceVertices[0].length + j] = faceVertices[i][j];
			for(int j = 0; j < faceIndices[0].length; j++) indices[f * faceIndices[0].length + j] = faceIndices[i][j] + (f * faceVertices[0].length) / 3;
			for(int j = 0; j < faceTextureCoords[0].length; j++) {
				float offset = 0;
				if(j % 2 == 0) offset = offsetTextureCoords.x;
				if(j % 2 == 1) offset = offsetTextureCoords.y;
				float offsetCoord = (float)offset / (float)texturemap.getTextureWidth();
				offsetCoord += (float)texturemap.getTextureBlockDim() / (float)texturemap.getTextureWidth() * faceTextureCoords[i][j];
				textureCoords[f * faceTextureCoords[0].length + j] = offsetCoord;
			}
			for(int j = 0; j < faceNormals[0].length; j++) normals[f * faceNormals[0].length + j] = faceNormals[i][j];
			for(int j = 0; j < faceVertices[0].length; j++) lightLevels[f * faceVertices[0].length + j] = 1.0f;
		}
		
		return loader.loadChunkMesh(vertices, vertices.length, indices, indices.length, textureCoords, textureCoords.length, normals, normals.length, lightLevels, lightLevels.length).getModel();
	}
	
	private static final float[][] faceVertices;
	private static final int[][] faceIndices;
	private static final float[][] faceNormals;
	private static final float[][] faceTextureCoords;
	
	static {
		faceVertices = new float[6][];
		faceVertices[Block.BLOCK_FACE_LEFT] = leftFace;
		faceVertices[Block.BLOCK_FACE_RIGHT] = rightFace;
		faceVertices[Block.BLOCK_FACE_FRONT] = frontFace;
		faceVertices[Block.BLOCK_FACE_BACK] = backFace;
		faceVertices[Block.BLOCK_FACE_TOP] = topFace;
		faceVertices[Block.BLOCK_FACE_BOTTOM] = bottomFace;
		
		faceIndices = new int[6][];
		faceIndices[Block.BLOCK_FACE_LEFT] = leftIndices;
		faceIndices[Block.BLOCK_FACE_RIGHT] = rightIndices;
		faceIndices[Block.BLOCK_FACE_FRONT] = frontIndices;
		faceIndices[Block.BLOCK_FACE_BACK] = backIndices;
		faceIndices[Block.BLOCK_FACE_TOP] = topIndices;
		faceIndices[Block.BLOCK_FACE_BOTTOM] = bottomIndices;
		
		faceNormals = new float[6][];
		faceNormals[Block.BLOCK_FACE_LEFT] = leftNormals;
		faceNormals[Block.BLOCK_FACE_RIGHT] = rightNormals;
		faceNormals[Block.BLOCK_FACE_FRONT] = frontNormals;
		faceNormals[Block.BLOCK_FACE_BACK] = backNormals;
		faceNormals[Block.BLOCK_FACE_TOP] = topNormals;
		faceNormals[Block.BLOCK_FACE_BOTTOM] = bottomNormals;
		
		faceTextureCoords = new float[6][];
		faceTextureCoords[Block.BLOCK_FACE_LEFT] = leftTextureCoords;
		faceTextureCoords[Block.BLOCK_FACE_RIGHT] = rightTextureCoords;
		faceTextureCoords[Block.BLOCK_FACE_FRONT] = frontTextureCoords;
		faceTextureCoords[Block.BLOCK_FACE_BACK] = backTextureCoords;
		faceTextureCoords[Block.BLOCK_FACE_TOP] = topTextureCoords;
		faceTextureCoords[Block.BLOCK_FACE_BOTTOM] = bottomTextureCoords;
	}
	
	private int faceCount;
	private int blockCount;
	
	private static boolean debug = false;
	private volatile boolean hasUpdated = true;
	public volatile boolean newMeshesAvailable = true;
	
	private Map<Vector3i, ChunkMesh> allMeshes;
	
	private ThreadPoolExecutor pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 1, 2));
	private ChunkMesher[] meshers;
	private List<Future<?>> futures = new ArrayList<Future<?>>();
	
	private volatile List<ChunkMesh> getMeshes = new ArrayList<ChunkMesh>();
	private volatile List<Vector3i> keySetCache = new ArrayList<Vector3i>();
	
	public synchronized List<ChunkMesh> getMeshes() {
		getMeshes.clear();
		for(ChunkMesh cm:this.allMeshes.values()) getMeshes.add(cm);
		newMeshesAvailable = false;
		return this.getMeshes;
	}
	
	private synchronized void deleteMesh(Vector3i vi) {
		loader.deleteMesh(allMeshes.remove(vi));
	}
	
	private synchronized void prepareKeySet() {
		keySetCache.clear();
		for(Vector3i vi:allMeshes.keySet()) keySetCache.add(vi);
	}
	
	/*private int getChunkIndex(int chunkx, int chunky, int chunkz) {
		if(chunkx < 0 || chunky >= world.getChunkWidth() || chunky < 0 || chunky >= world.getChunkHeight() || chunkz < 0 || chunkz >= world.getChunkDepth()) return -1;
		return (chunkx * world.getChunkHeight() * world.getChunkDepth()) + chunky * world.getChunkDepth() + chunkz;
	}*/
	
	public void startMeshingThread() {
		if(thread != null && thread.isAlive()) return;
		thread = new Thread(this);
		thread.start();
	}
	
	List<Vector3i> toRemove = new ArrayList<Vector3i>();
	public volatile boolean running = false;
	
	@Override
	public void run() {
		try {
			e = null;
			running = true;
			while(running) {
				updateMeshesNow();
				Thread.sleep(6);
			}
		}catch(Exception e2) {
			e = e2;
		}
	}
	
	public void join() throws Exception {
		if(thread != null && thread.isAlive()) thread.join(2048);
	}
	
	private long startTime = System.currentTimeMillis();
	
	public void updateMeshesNow() throws Exception {
		if(!hasUpdated) return;
		faceCount = 0;
		blockCount = 0;
		futures.clear();
		long startTime = System.currentTimeMillis();
		List<Chunk[]> loadedChunks = world.getAllLoadedChunks();
		int indx = 0;
		for(Chunk[] cs:loadedChunks) {
			Chunk[] css1 = world.getMasterChunk(cs[0].getChunkx() + 1, cs[0].getChunkz());
			Chunk[] css2 = world.getMasterChunk(cs[0].getChunkx() - 1, cs[0].getChunkz());
			Chunk[] css3 = world.getMasterChunk(cs[0].getChunkx(), cs[0].getChunkz() + 1);
			Chunk[] css4 = world.getMasterChunk(cs[0].getChunkx(), cs[0].getChunkz() - 1);
			if(css1 == null || css2 == null || css3 == null || css4 == null) continue;
			if(cs[0].getChunkx() != 0 && cs[0].getChunkz() != 0) {
				if(css1 == null || css2 == null || css3 == null || css4 == null) continue;
			}else {
				if(!(cs[0].getChunkx() == 0 && cs[0].getChunkz() == 0)) {
					if(css1 == null || css3 == null) continue;
				}
			}
			for(int i = 0; i < cs.length; i++) {
				Chunk c = cs[i];
				if(c.isDirty() == true && c.isDecorated && c.isGenerated && c.hasSunlight) {
					
					//Check adjacent chunks
					Chunk c1 = css1[i];
					Chunk c3 = css3[i];
					if(c.getChunkx() != 0 && c.getChunkz() != 0) {
						Chunk c2 = css2[i];
						Chunk c4 = css4[i];
						if(c1 == null || c2 == null || c3 == null || c4 == null || !c1.hasSunlight || !c2.hasSunlight || !c3.hasSunlight || !c4.hasSunlight) continue;
					}else {
						if(!(c.getChunkx() == 0 && c.getChunkz() == 0)) {
							if(c1 == null || c3 == null || !c1.hasSunlight || !c3.hasSunlight) continue;
						}
					}
					
					meshers[indx].setChunk(c.getChunkx(), c.getChunky(), c.getChunkz());
					futures.add(pool.submit(meshers[indx]));
					indx++;
					c.markNotDirty();
				}
				if(indx >= meshers.length) break;
			}
			if(indx >= meshers.length) break;
		}
		for(Future<?> f:futures) f.get();
		
		prepareKeySet();
		for(Vector3i vi:keySetCache) {
			if(!world.isChunkLoaded(vi.x, vi.z)) {
				toRemove.add(vi);
			}
		}
		
		startTime = System.currentTimeMillis();
		hasUpdated = false;
		
		if(debug && faceCount != 0) System.out.println(Integer.toString(faceCount) + " faces in mesh (" + Integer.toString(blockCount) + " blocks)" + ", took " + Long.toString(System.currentTimeMillis() - startTime) + " ms");
	}
	
	public void updateRendererMeshes() throws Exception {
		if(hasUpdated) return;
		while(!toRemove.isEmpty()) {
			deleteMesh(toRemove.remove(toRemove.size() - 1));
		}
		if(pool.getActiveCount() != 0) {
			if(System.currentTimeMillis() - startTime >= 5000) System.err.println("Thread stuck!");
			return;
		}
		
		for(int i = 0; i < meshers.length; i++) {
			if(meshers[i].hasGotten == true) continue;
			if(meshers[i].ex != null) throw meshers[i].ex;
			meshers[i].hasGotten = true;
			ChunkMesh oldMesh = allMeshes.put(new Vector3i(meshers[i].chunkx, meshers[i].chunky, meshers[i].chunkz), meshers[i].getMesh());
			if(oldMesh != null) loader.deleteMesh(oldMesh);
			faceCount += meshers[i].faceCount;
			blockCount += meshers[i].blockCount;
		}
		
		hasUpdated = true;
		newMeshesAvailable = true;
	}
	
	private static class ChunkMesher implements Runnable {
		
		private int chunkx,chunky,chunkz;
		private Loader loader;
		private BlockTexturemap texturemap;
		private World world;
		private Exception ex;
		
		public volatile boolean hasGotten = true;
		
		private float[] vertices = new float[Chunk.CHUNK_WIDTH * Chunk.CHUNK_HEIGHT * Chunk.CHUNK_DEPTH * 6 * topFace.length];
		private int verticesIndx = 0;
		private int verticesLength;
		private int[] indices = new int[Chunk.CHUNK_WIDTH * Chunk.CHUNK_HEIGHT * Chunk.CHUNK_DEPTH * 6 * topIndices.length];
		private int indicesIndx = 0;
		private int indicesLength;
		private float[] normals = new float[Chunk.CHUNK_WIDTH * Chunk.CHUNK_HEIGHT * Chunk.CHUNK_DEPTH * 6 * topNormals.length];
		private int normalsIndx = 0;
		private int normalsLength;
		private float[] textureCoords = new float[Chunk.CHUNK_WIDTH * Chunk.CHUNK_HEIGHT * Chunk.CHUNK_DEPTH * 6 * topTextureCoords.length];
		private int textureCoordsIndx = 0;
		private int textureCoordsLength;
		private float[] lightLevels = new float[Chunk.CHUNK_WIDTH * Chunk.CHUNK_HEIGHT * Chunk.CHUNK_DEPTH * 6 * 4];
		private int lightLevelsIndx = 0;
		private int lightLevelsLength;
		
		private int faceCount,blockCount;
		
		public ChunkMesher(Loader loader, BlockTexturemap texturemap, World world) {
			this.loader = loader;
			this.texturemap = texturemap;
			this.world = world;;
		}
		
		public void setChunk(int chunkx, int chunky, int chunkz) {
			this.chunkx = chunkx;
			this.chunky = chunky;
			this.chunkz = chunkz;
		}
		
		@Override
		public void run() {
			this.ex = null;
			try {
				meshChunk();
				hasGotten = false;
			}catch(Exception e) {
				this.ex = e;
				return;
			}
		}
		
		public synchronized ChunkMesh getMesh() throws Exception {
			if(verticesLength == 0) {
				return new ChunkMesh(null, true);
			}
			return loader.loadChunkMesh(vertices, verticesLength, indices, indicesLength, textureCoords, textureCoordsLength, normals, normalsLength, lightLevels, lightLevelsLength);
		}
		
		private void meshChunk() throws Exception {
			faceCount = 0;
			blockCount = 0;
			int airID = Block.air.getBlockID();
			Block adjBlock;
			for(int x = Chunk.CHUNK_WIDTH * chunkx; x < Chunk.CHUNK_WIDTH * (chunkx + 1); x++) {
				for(int y = Chunk.CHUNK_HEIGHT * chunky; y < Chunk.CHUNK_HEIGHT * (chunky + 1); y++) {
					for(int z = Chunk.CHUNK_DEPTH * chunkz; z < Chunk.CHUNK_DEPTH * (chunkz + 1); z++) {
						int blockid = world.getBlock(x, y, z);
						if(blockid != airID && Block.getBlockFromID(blockid) != null) {
							Block block = Block.getBlockFromID(blockid);
							int oldFaceCount = faceCount;
							adjBlock = Block.allBlocks[world.getBlock(x+1,y,z)];
							if(!adjBlock.isOpaque()) tryAddFace(Block.BLOCK_FACE_FRONT, block, adjBlock, texturemap, x, y, z);
							adjBlock = Block.allBlocks[world.getBlock(x-1,y,z)];
							if(!adjBlock.isOpaque()) tryAddFace(Block.BLOCK_FACE_BACK, block, adjBlock, texturemap, x, y, z);
							adjBlock = Block.allBlocks[world.getBlock(x,y,z+1)];
							if(!adjBlock.isOpaque()) tryAddFace(Block.BLOCK_FACE_RIGHT, block, adjBlock, texturemap, x, y, z);
							adjBlock = Block.allBlocks[world.getBlock(x,y,z-1)];
							if(!adjBlock.isOpaque()) tryAddFace(Block.BLOCK_FACE_LEFT, block, adjBlock, texturemap, x, y, z);
							adjBlock = Block.allBlocks[world.getBlock(x,y+1,z)];
							if(!adjBlock.isOpaque()) tryAddFace(Block.BLOCK_FACE_TOP, block, adjBlock, texturemap, x, y, z);
							adjBlock = Block.allBlocks[world.getBlock(x,y-1,z)];
							if(!adjBlock.isOpaque()) tryAddFace(Block.BLOCK_FACE_BOTTOM, block, adjBlock, texturemap, x, y, z);
							if(oldFaceCount != faceCount) blockCount++;
						}
					}
				}
			}
			
			verticesLength = verticesIndx;
			verticesIndx = 0;
			
			indicesLength = indicesIndx;
			indicesIndx = 0;
			
			normalsLength = normalsIndx;
			normalsIndx = 0;
			
			textureCoordsLength = textureCoordsIndx;
			textureCoordsIndx = 0;
			
			lightLevelsLength = lightLevelsIndx;
			lightLevelsIndx = 0;
		}
		
		private void tryAddFace(int face, Block block, Block adjBlock, BlockTexturemap texturemap, int x, int y, int z) {
			if(!block.shouldRender(face)) return;
			if(block.canRenderThrough() == false && adjBlock.getBlockID() == block.getBlockID()) return;
			
			int origSize = verticesIndx / 3;
			for(int i = 0; i < faceVertices[face].length; i++) {
				int offset = 0;
				if(i % 3 == 0) offset = x;
				if(i % 3 == 1) offset = y;
				if(i % 3 == 2) offset = z;
				float offsetCoord = faceVertices[face][i] + (float)offset;
				vertices[verticesIndx] = offsetCoord;
				verticesIndx++;
			}
			for(int i = 0; i < faceIndices[face].length; i++) {
				indices[indicesIndx] = faceIndices[face][i] + origSize;
				indicesIndx++;
			}
			for(int i = 0; i < faceNormals[face].length; i++) {
				int offset = 0;
				if(i % 3 == 0) offset = x;
				if(i % 3 == 1) offset = y;
				if(i % 3 == 2) offset = z;
				float offsetCoord = faceNormals[face][i] + (float)offset;
				normals[normalsIndx] = offsetCoord;
				normalsIndx++;
			}
			//1280 by 720
			Vector2f offsetTextureCoords = texturemap.textureLocationToCoordinates(block.getTexture(face));
			for(int i = 0; i < faceTextureCoords[face].length; i++) {
				float offset = 0;
				if(i % 2 == 0) offset = offsetTextureCoords.x;
				if(i % 2 == 1) offset = offsetTextureCoords.y;
				//float offsetCoord = (faceTextureCoords[face][i] / (float)texturemap.getBlocksPerRow()) + (float)offset / (float)texturemap.getBlocksPerRow();
				float offsetCoord = (float)offset / (float)texturemap.getTextureWidth();
				offsetCoord += (float)texturemap.getTextureBlockDim() / (float)texturemap.getTextureWidth() * faceTextureCoords[face][i];
				textureCoords[textureCoordsIndx] = offsetCoord;
				textureCoordsIndx++;
			}
			float lightLevel = getLightLevelForFace(x, y, z, face);
			lightLevels[lightLevelsIndx] = lightLevel;
			lightLevelsIndx++;
			lightLevels[lightLevelsIndx] = lightLevel;
			lightLevelsIndx++;
			lightLevels[lightLevelsIndx] = lightLevel;
			lightLevelsIndx++;
			lightLevels[lightLevelsIndx] = lightLevel;
			lightLevelsIndx++;
			faceCount++;
		}
		
		private float getLightLevelForFace(int x, int y, int z, int face) {
			/*int blockid = world.getBlock(x, y, z);
			if(!Block.allBlocks[blockid].isOpaque()) {
				return (float)world.getLightLevel(x, y, z) / 15.0f;
			}*/
			int lightLevel = 0;
			switch(face) {
				default:
					lightLevel = 0;
					break;
				case Block.BLOCK_FACE_FRONT:
					lightLevel = world.getLightLevel(x + 1, y, z) - 1;
					break;
				case Block.BLOCK_FACE_BACK:
					lightLevel = world.getLightLevel(x - 1, y, z) - 1;
					break;
				case Block.BLOCK_FACE_RIGHT:
					lightLevel = world.getLightLevel(x, y, z + 1) - 1;
					break;
				case Block.BLOCK_FACE_LEFT:
					lightLevel = world.getLightLevel(x, y, z - 1) - 1;
					break;
				case Block.BLOCK_FACE_TOP:
					lightLevel = world.getLightLevel(x, y + 1, z);
					break;
				case Block.BLOCK_FACE_BOTTOM:
					lightLevel = world.getLightLevel(x, y - 1, z) - 2;
					break;
			}
			if(lightLevel < 0) lightLevel = 0;
			return (float)lightLevel / 15.0f;
		}
		
	}
	
}