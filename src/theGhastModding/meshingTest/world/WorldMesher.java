package theGhastModding.meshingTest.world;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import theGhastModding.meshingTest.resources.Loader;
import theGhastModding.meshingTest.resources.textures.BlockTexturemap;
import theGhastModding.meshingTest.world.blocks.Block;

public class WorldMesher {
	
	private World world;
	
	public WorldMesher(World world) {
		this.world = world;
		this.meshes = new ChunkMesh[world.getChunkWidth() * world.getChunkHeight() * world.getChunkDepth()];
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
	
	private List<Float> vertices;
	private List<Integer> indices;
	private List<Float> normals;
	private List<Float> textureCoords;
	private int faceCount;
	private int blockCount;
	
	private ChunkMesh[] meshes;
	
	public synchronized ChunkMesh[] getMeshes() {
		return this.meshes;
	}
	
	private int getChunkIndex(int chunkx, int chunky, int chunkz) {
		if(chunkx < 0 || chunky >= world.getChunkWidth() || chunky < 0 || chunky >= world.getChunkHeight() || chunkz < 0 || chunkz >= world.getChunkDepth()) return -1;
		return (chunkx * world.getChunkHeight() * world.getChunkDepth()) + chunky * world.getChunkDepth() + chunkz;
	}
	
	public void updateMeshes(Loader loader, BlockTexturemap texturemap) throws Exception {
		faceCount = 0;
		blockCount = 0;
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < world.getChunkWidth(); i++) {
			for(int j = 0; j < world.getChunkHeight(); j++) {
				for(int k = 0; k < world.getChunkDepth(); k++) {
					int indx = getChunkIndex(i, j, k);
					if(indx != -1 && (world.getChunk(i, j, k).isDirty() == true || meshes[indx] == null)) {
						meshes[indx] = meshChunk(i, j, k, loader, texturemap);
						world.getChunk(i, j, k).markNotDirty();
					}
				}
			}
		}
		if(faceCount != 0) System.out.println(Integer.toString(faceCount) + " faces in mesh (" + Integer.toString(blockCount) + " blocks)" + ", took " + Long.toString(System.currentTimeMillis() - startTime) + " ms");
	}
	
	private ChunkMesh meshChunk(int chunkx, int chunky, int chunkz, Loader loader, BlockTexturemap texturemap) throws Exception {
		vertices = new ArrayList<Float>();
		indices = new ArrayList<Integer>();
		normals = new ArrayList<Float>();
		textureCoords = new ArrayList<Float>();
		int airID = Block.air.getBlockID();
		for(int x = Chunk.CHUNK_WIDTH * chunkx; x < Chunk.CHUNK_WIDTH * (chunkx + 1); x++) {
			for(int y = Chunk.CHUNK_HEIGHT * chunky; y < Chunk.CHUNK_HEIGHT * (chunky + 1); y++) {
				for(int z = Chunk.CHUNK_DEPTH * chunkz; z < Chunk.CHUNK_DEPTH * (chunkz + 1); z++) {
					int blockid = world.getBlock(x, y, z);
					if(blockid != airID && Block.getBlockFromID(blockid) != null) {
						Block block = Block.getBlockFromID(blockid);
						int oldFaceCount = faceCount;
						if(!Block.getBlockFromID(world.getBlock(x+1,y,z)).isOpaque(Block.BLOCK_FACE_BACK)) tryAddFace(Block.BLOCK_FACE_FRONT, block, texturemap, x, y, z);
						if(!Block.getBlockFromID(world.getBlock(x-1,y,z)).isOpaque(Block.BLOCK_FACE_FRONT)) tryAddFace(Block.BLOCK_FACE_BACK, block, texturemap, x, y, z);
						if(!Block.getBlockFromID(world.getBlock(x,y,z+1)).isOpaque(Block.BLOCK_FACE_LEFT)) tryAddFace(Block.BLOCK_FACE_RIGHT, block, texturemap, x, y, z);
						if(!Block.getBlockFromID(world.getBlock(x,y,z-1)).isOpaque(Block.BLOCK_FACE_RIGHT)) tryAddFace(Block.BLOCK_FACE_LEFT, block, texturemap, x, y, z);
						if(!Block.getBlockFromID(world.getBlock(x,y+1,z)).isOpaque(Block.BLOCK_FACE_BOTTOM)) tryAddFace(Block.BLOCK_FACE_TOP, block, texturemap, x, y, z);
						if(!Block.getBlockFromID(world.getBlock(x,y-1,z)).isOpaque(Block.BLOCK_FACE_TOP)) tryAddFace(Block.BLOCK_FACE_BOTTOM, block, texturemap, x, y, z);
						if(oldFaceCount != faceCount) blockCount++;
					}
				}
			}
		}
		if(vertices.isEmpty()) {
			return new ChunkMesh(null, true);
		}
		float[] verticesArray = new float[vertices.size()];
		for(int i = 0; i < vertices.size(); i++) {
			verticesArray[i] = vertices.get(i);
		}
		vertices.clear();
		int[] indicesArray = new int[indices.size()];
		for(int i = 0; i < indices.size(); i++) {
			indicesArray[i] = indices.get(i);
		}
		indices.clear();
		float[] normalsArray = new float[normals.size()];
		for(int i = 0; i < normals.size(); i++) {
			normalsArray[i] = normals.get(i);
		}
		normals.clear();
		float[] textureCoordsArray = new float[textureCoords.size()];
		for(int i = 0; i < textureCoords.size(); i++) {
			textureCoordsArray[i] = textureCoords.get(i);
		}
		textureCoords.clear();
		return loader.loadChunkMesh(verticesArray, indicesArray, textureCoordsArray, normalsArray, new float[] {});
	}
	
	private void tryAddFace(int face, Block block, BlockTexturemap texturemap, int x, int y, int z) {
		if(!block.shouldRender(face)) return;
		int origSize = vertices.size() / 3;
		for(int i = 0; i < faceVertices[face].length; i++) {
			int offset = 0;
			if(i % 3 == 0) offset = x;
			if(i % 3 == 1) offset = y;
			if(i % 3 == 2) offset = z;
			float offsetCoord = faceVertices[face][i] + (float)offset;
			vertices.add(offsetCoord);
		}
		for(int i = 0; i < faceIndices[face].length; i++) {
			indices.add(faceIndices[face][i] + origSize);
		}
		for(int i = 0; i < faceNormals[face].length; i++) {
			int offset = 0;
			if(i % 3 == 0) offset = x;
			if(i % 3 == 1) offset = y;
			if(i % 3 == 2) offset = z;
			float offsetCoord = faceNormals[face][i] + (float)offset;
			normals.add(offsetCoord);
		}
		//1280 by 720
		Vector2f offsetTextureCoords = texturemap.textureLocationToCoordinates(block.getTexture(face));
		for(int i = 0; i < faceTextureCoords[face].length; i++) {
			float offset = 0;
			if(i % 2 == 0) offset = offsetTextureCoords.x;
			if(i % 2 == 1) offset = offsetTextureCoords.y;
			float offsetCoord = (faceTextureCoords[face][i] / (float)texturemap.getBlocksPerRow()) + (float)offset / (float)texturemap.getBlocksPerRow();
			textureCoords.add(offsetCoord);
		}
		faceCount++;
	}
	
}