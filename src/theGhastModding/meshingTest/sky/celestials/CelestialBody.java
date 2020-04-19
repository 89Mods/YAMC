package theGhastModding.meshingTest.sky.celestials;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.joml.Vector3f;

import theGhastModding.meshingTest.resources.BaseModel;
import theGhastModding.meshingTest.resources.Loader;
import theGhastModding.meshingTest.resources.ModelTexture;
import theGhastModding.meshingTest.resources.TexturedModel;

public class CelestialBody {
	
	private float radius;
	private TexturedModel model;
	private Vector3f pos,rot;
	
	public CelestialBody(float radius, TexturedModel model, Vector3f pos, Vector3f rot) {
		super();
		this.radius = radius;
		this.model = model;
		this.pos = pos;
		this.rot = rot;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public TexturedModel getModel() {
		return model;
	}
	
	public Vector3f getPos() {
		return pos;
	}
	
	public Vector3f getRot() {
		return rot;
	}
	
	public static BaseModel genPlanetModel(float radius, float[][] heightmap, float heightStretch) throws Exception {
		final float normalCalcOffset = heightmap == null ? 0 : 1.0f / ((float)heightmap.length * 2.0f);
		
		//float circ = (float)Math.PI * 2.0f * radius;
		//int ringPolygonCnt = (int)(circ / 50.0f);
		int ringPolygonCnt = 2048;
		
		float[] vertices = new float[ringPolygonCnt * ringPolygonCnt * 3 + 3];
		float[] textureCoordinates = new float[ringPolygonCnt * ringPolygonCnt * 2 + 2];
		float[] normals = new float[ringPolygonCnt * ringPolygonCnt * 3 + 3];
		for(int i = 0; i < ringPolygonCnt; i++) {
			float latitude = (float)i / (float)ringPolygonCnt;
			for(int j = 0; j < ringPolygonCnt; j++) {
				float longitude = (float)j / (float)ringPolygonCnt;
				
				textureCoordinates[(i * ringPolygonCnt + j) * 2 + 0] = (float)j / (float)ringPolygonCnt;
				textureCoordinates[(i * ringPolygonCnt + j) * 2 + 1] = (float)i / (float)ringPolygonCnt;
				
				float wx = 0.0f;
				float wy = 1.0f;
				float wz = 0.0f;
				
				float cosX = (float)Math.cos((latitude * 180.0f - 0.0f) * 0.017453292519943295f);
				float sinX = (float)Math.sin((latitude * 180.0f - 0.0f) * 0.017453292519943295f);
				float cosY = (float)Math.cos((longitude * 360.0f - 180.0f) * 0.017453292519943295f);
				float sinY = (float)Math.sin((longitude * 360.0f - 180.0f) * 0.017453292519943295f);
				
				float wyn = wy * cosX - wz * sinX;
				wz = wy * sinX + wz * cosX;
				wy = wyn;
				
				float wxn = wx * cosY + wz * sinY;
				wz = -wx * sinY + wz * cosY;
				wx = wxn;
				
				float plusHeight = heightmap == null ? 0 : heightmap[(int)(longitude * heightmap.length)][(int)(latitude * heightmap[0].length)] * heightStretch;
				
				wx *= radius + plusHeight;
				wy *= radius + plusHeight;
				wz *= radius + plusHeight;
				
				vertices[(i * ringPolygonCnt + j) * 3 + 0] = wx;
				vertices[(i * ringPolygonCnt + j) * 3 + 1] = wy;
				vertices[(i * ringPolygonCnt + j) * 3 + 2] = wz;
				
				// Normal calculation
				if(heightmap != null) {
					float h1 = heightmap[(int)((longitude + normalCalcOffset) * heightmap.length)][(int)(latitude * heightmap[0].length)];
					float h2 = heightmap[(int)(longitude * heightmap.length)][(int)((latitude + normalCalcOffset / 2.0f) * heightmap[0].length)];
					float h3 = heightmap[(int)((longitude - normalCalcOffset) * heightmap.length)][(int)(latitude * heightmap[0].length)];
					float h4 = heightmap[(int)(longitude * heightmap.length)][(int)((latitude - normalCalcOffset / 2.0f) * heightmap[0].length)];
					
					wx = -(h1 - h3);
					wy = 1.0f / (heightStretch * 2.0f);
					wz = h2 - h4;
				}else {
					wx = 0;
					wy = 1.0f;
					wz = 0;
				}
				
				wyn = wy * cosX - wz * sinX;
				wz = wy * sinX + wz * cosX;
				wy = wyn;
				
				wxn = wx * cosY + wz * sinY;
				wz = -wx * sinY + wz * cosY;
				wx = wxn;
				
				normals[(i * ringPolygonCnt + j) * 3 + 0] = wx;
				normals[(i * ringPolygonCnt + j) * 3 + 1] = wy;
				normals[(i * ringPolygonCnt + j) * 3 + 2] = wz;
			}
		}
		vertices[vertices.length - 3] = 0.0f;
		vertices[vertices.length - 2] = -radius;
		vertices[vertices.length - 1] = 0.0f;
		normals[normals.length - 3] = 0.0f;
		normals[normals.length - 2] = -1.0f;
		normals[normals.length - 1] = 0.0f;
		
		/*int[] indices = new int[ringPolygonCnt * 3];
		for(int j = 0; j < ringPolygonCnt; j++) {
			indices[j * 3 + 0] = 100 * ringPolygonCnt + j;
			indices[j * 3 + 1] = 100 * ringPolygonCnt + ((j + 1) % ringPolygonCnt);
			indices[j * 3 + 2] = 99 * ringPolygonCnt + j;
		}*/
		
		int[] indices = new int[ringPolygonCnt * ringPolygonCnt * 3 * 2 - 3];
		for(int i = 0; i < ringPolygonCnt; i++) {
			for(int j = 0; j < ringPolygonCnt; j++) {
				indices[(i * ringPolygonCnt + j) * 3 * 2 + 0] = i * ringPolygonCnt + j;
				indices[(i * ringPolygonCnt + j) * 3 * 2 + 1] = i * ringPolygonCnt + (j + 1) % ringPolygonCnt;
				indices[(i * ringPolygonCnt + j) * 3 * 2 + 2] = (i - 1) * ringPolygonCnt + j;
				if(i == 0) {
					indices[(i * ringPolygonCnt + j) * 3 * 2 + 2] = ringPolygonCnt * ringPolygonCnt;
				}
				
				if(i != 0) {
					indices[(i * ringPolygonCnt + j) * 3 * 2 - 3] = i * ringPolygonCnt + (j + 1) % ringPolygonCnt;
					indices[(i * ringPolygonCnt + j) * 3 * 2 - 2] = (i - 1) * ringPolygonCnt + (j + 1) % ringPolygonCnt;
					indices[(i * ringPolygonCnt + j) * 3 * 2 - 1] = (i - 1) * ringPolygonCnt + j;
				}
			}
		}
		
		return Loader.loadToVAO(vertices, indices, textureCoordinates, normals);
	}
	
	public static CelestialBody loadPlanet(float radius, Vector3f pos, Vector3f rot, String textureFile, String heightmapFile, float heightStretch) throws Exception {
		int texID = Loader.loadTextureFromFile(textureFile);
		BufferedImage img = ImageIO.read(new File(heightmapFile));
		float[][] heightmap = new float[img.getWidth()][img.getHeight()];
		for(int i = 0; i < img.getWidth(); i++) {
			for(int j = 0; j < img.getHeight(); j++) {
				heightmap[i][j] = (float)(img.getRGB(i, j) & 0xFF) / 255.0f;
			}
		}
		BaseModel planetModel = genPlanetModel(radius, heightmap, heightStretch);
		TexturedModel texturedModel = new TexturedModel(planetModel, new ModelTexture(texID));
		CelestialBody planet = new CelestialBody(radius, texturedModel, pos, rot);
		return planet;
	}
	
}