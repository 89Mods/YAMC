package theGhastModding.meshingTest.resources;

public class BasicObjects {
	
	public final BaseModel cube;
	public final BaseModel advancedCube;
	
	public BasicObjects() throws Exception {
		float[] vertices = {
				//Left
				-0.5f,0f,-0.5f,	
				-0.5f,-1f,-0.5f,	
				0.5f,-1f,-0.5f,	
				0.5f,0f,-0.5f,		
				
				//Right
				-0.5f,0f,0.5f,	
				-0.5f,-1f,0.5f,	
				0.5f,-1f,0.5f,	
				0.5f,0f,0.5f,
				
				//Front
				0.5f,0f,-0.5f,	
				0.5f,-1f,-0.5f,	
				0.5f,-1f,0.5f,	
				0.5f,0f,0.5f,
				
				//Back
				-0.5f,0f,-0.5f,	
				-0.5f,-1f,-0.5f,	
				-0.5f,-1f,0.5f,	
				-0.5f,0f,0.5f,
				
				//Top
				-0.5f,0f,0.5f,
				-0.5f,0f,-0.5f,
				0.5f,0f,-0.5f,
				0.5f,0f,0.5f,
				
				//Bottom
				-0.5f,-1f,0.5f,
				-0.5f,-1f,-0.5f,
				0.5f,-1f,-0.5f,
				0.5f,-1f,0.5f
				
		};
		
		float[] textureCoords = {
				
				0,0,
				0,1,
				1,1,
				1,0,			
				0,0,
				0,1,
				1,1,
				1,0,			
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0

				
		};
		
		/*int[] indices = {
				0,1,3,	
				3,1,2,
				4,5,7,
				7,5,6,
				11,9,8,
				10,9,11,
				15,13,12,
				14,13,15,
				16,17,19,
				19,17,18,
				20,21,23,
				23,21,22
		};*/
		int[] indices = {
				3,1,0,	
				2,1,3,	
				4,5,7,
				7,5,6,
				11,9,8,
				10,9,11,
				12,13,15,
				15,13,14,
				19,17,16,
				18,17,19,
				20,21,23,
				23,21,22
		};
		
		//Back face: 0,1,2,3
		//Front face: 4,5,6,7
		//Right face: 8,9,10,11
		//Left face: 12,13,14,15
		//Top face: 16,17,18,19
		//Bottom face: 20,21,22,23
		
		float[] normals = {
				0,-0.5f,-1,
				0,-0.5f,-1,
				0,-0.5f,-1,
				0,-0.5f,-1,
				0,-0.5f,1,
				0,-0.5f,1,
				0,-0.5f,1,
				0,-0.5f,1,
				1,-0.5f,0,
				1,-0.5f,0,
				1,-0.5f,0,
				1,-0.5f,0,
				-1,-0.5f,0,
				-1,-0.5f,0,
				-1,-0.5f,0,
				-1,-0.5f,0,
				0,0.5f,0,
				0,0.5f,0,
				0,0.5f,0,
				0,0.5f,0,
				0,-1.5f,0,
				0,-1.5f,0,
				0,-1.5f,0,
				0,-1.5f,0,
		};
		
		final float oneThird = 1f / 3f;
		
		float[] advanvedCubetextureCoords = {
				
				oneThird,0,
				oneThird,0.5f,
				0,0.5f,
				0,0,
				
				oneThird,0,
				oneThird,0.5f,
				oneThird * 2,0.5f,
				oneThird * 2,0,
				
				1,0,
				1,0.5f,
				oneThird * 2,0.5f,
				oneThird * 2,0,
				
				0,0.5f,
				0,1,
				oneThird,1,
				oneThird,0.5f,
				
				oneThird,1,
				oneThird,0.5f,
				oneThird * 2,0.5f,
				oneThird * 2,1,
				
				oneThird * 2,0.5f,
				oneThird * 2,1,
				1,1,
				1,0.5f
				
		};
		
		cube = Loader.loadToVAO(vertices, indices, textureCoords, normals);
		advancedCube = Loader.loadToVAO(vertices, indices, advanvedCubetextureCoords, normals);
	}
	
}