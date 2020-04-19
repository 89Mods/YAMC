package theGhastModding.meshingTest.sky;

import org.joml.Vector3f;

import theGhastModding.meshingTest.resources.BaseModel;
import theGhastModding.meshingTest.resources.Loader;

public class Atmosphere {
	
	public float planetRadius = 6360e3f;
	public float atmoRadius = 6420e3f;
	public float Hr = 7994f;
	public float Hm = 1200f;
	
	public Vector3f betaR = new Vector3f(3.8e-6f, 13.5e-6f, 33.1e-6f);
	public Vector3f betaM = new Vector3f(4e-6f, 4e-6f, 4e-6f);
	
	public float mieG = 0.78f;
	
	public Atmosphere() {
		super();
	}
	
	public static BaseModel generateSkydome(float radius) throws Exception {
		int ringPolygonCnt = 4096;
		float[] vertices = new float[ringPolygonCnt * ringPolygonCnt * 3 + 3];
		float[] normals = new float[ringPolygonCnt * ringPolygonCnt * 3 + 3];
		for(int i = 0; i < ringPolygonCnt; i++) {
			float latitude = (float)i / (float)ringPolygonCnt;
			for(int j = 0; j < ringPolygonCnt; j++) {
				float longitude = (float)j / (float)ringPolygonCnt;
				
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
				
				normals[(i * ringPolygonCnt + j) * 3 + 0] = -wx;
				normals[(i * ringPolygonCnt + j) * 3 + 1] = -wy;
				normals[(i * ringPolygonCnt + j) * 3 + 2] = -wz;
				
				wx *= radius;
				wy *= radius;
				wz *= radius;
				
				vertices[(i * ringPolygonCnt + j) * 3 + 0] = wx;
				vertices[(i * ringPolygonCnt + j) * 3 + 1] = wy;
				vertices[(i * ringPolygonCnt + j) * 3 + 2] = wz;
			}
		}
		vertices[vertices.length - 3] = 0.0f;
		vertices[vertices.length - 2] = -radius;
		vertices[vertices.length - 1] = 0.0f;
		normals[normals.length - 3] = -0.0f;
		normals[normals.length - 2] = 1.0f;
		normals[normals.length - 1] = -0.0f;
		
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
		
		return Loader.loadToVAO(vertices, indices, normals);
	}
	
	public static final Atmosphere EVE;
	
	public static final Atmosphere DUNA;
	
	public static final Atmosphere JOOL;
	
	public static final Atmosphere LAYTHE;
	
	public static final Atmosphere PROMETHEUS;
	
	public static final Atmosphere HAVEN;
	
	public static final Atmosphere WARDEN;
	
	public static final Atmosphere TEMPEST;
	
	public static final Atmosphere CERBERUS;
	
	public static final Atmosphere DEFAULT = new Atmosphere();
	
	static {
		EVE = new Atmosphere();
		EVE.planetRadius = 700000;
		EVE.atmoRadius = 711380.5f;
		EVE.Hr = 10000;
		EVE.Hm = 1200f;
		EVE.betaM = new Vector3f(5e-6f, 5e-6f, 5e-6f);
		EVE.betaR = new Vector3f(0.0000178f, 0.00001354f, 0.00008344f);
		EVE.mieG = 0.85f;
		
		DUNA = new Atmosphere();
		DUNA.planetRadius = 320000;
		DUNA.atmoRadius = 324603.813f;
		DUNA.Hr = 10000;
		DUNA.Hm = 1200f;
		DUNA.betaR = new Vector3f(0.000015f, 0.0000065f, 0.0000027f);
		DUNA.mieG = 0.85f;
		
		JOOL = new Atmosphere();
		JOOL.planetRadius = 6000000;
		JOOL.atmoRadius = 6100000;
		JOOL.Hr = 10000f;
		JOOL.Hm = 1200f;
		JOOL.betaR = new Vector3f(0.0000058f, 0.0000335f, 0.0000131f);
		JOOL.mieG = 0.85f;
		
		LAYTHE = new Atmosphere();
		LAYTHE.planetRadius = 600000;
		LAYTHE.atmoRadius = 608632.125f;
		LAYTHE.Hr = 10000;
		LAYTHE.Hm = 1200f;
		LAYTHE.betaR = new Vector3f(0.0000058f, 0.0000135f, 0.0000331f);
		LAYTHE.mieG = 0.78f;
		
		PROMETHEUS = new Atmosphere();
		PROMETHEUS.planetRadius = 6470000;
		PROMETHEUS.atmoRadius = 6526603;
		PROMETHEUS.Hr = 7000;
		PROMETHEUS.Hm = 1000;
		PROMETHEUS.betaM = new Vector3f(4e-6f, 4.2e-6f, 4.6e-6f);
		PROMETHEUS.betaR = new Vector3f(0.0000257f, 0.0000184f, 0.0000052f);
		PROMETHEUS.mieG = 0.7f;
		
		HAVEN = new Atmosphere();
		HAVEN.planetRadius = 608000;
		HAVEN.atmoRadius = 617401;
		HAVEN.Hr = 6500f;
		HAVEN.Hm = 1300f;
		HAVEN.betaR = new Vector3f(0.0000089f, 0.0000137f, 0.0000216f);
		HAVEN.betaM = new Vector3f(2e-5f, 2e-5f, 2e-5f);
		HAVEN.mieG = 0.81f;
		
		WARDEN = new Atmosphere();
		WARDEN.planetRadius = 2150000;
		WARDEN.atmoRadius = 2220000;
		WARDEN.Hr = 11000;
		WARDEN.Hm = 1200f;
		WARDEN.betaM = new Vector3f(2e-6f, 1.8e-6f, 1.5e-6f);
		WARDEN.betaR = new Vector3f(0.000002609f, 0.000002132f, 0.000001407f);
		WARDEN.mieG = 0.8f;
		
		TEMPEST = new Atmosphere();
		TEMPEST.planetRadius = 6000000;
		TEMPEST.atmoRadius = 6086321;
		//TEMPEST.planetRadius = 1758000;
		//TEMPEST.atmoRadius = 1758000+672000;
		TEMPEST.Hr = 10000;
		TEMPEST.Hm = 1200f;
		TEMPEST.betaR = new Vector3f(0.00000027f, 0.00000416f, 0.00001923f);
		TEMPEST.mieG = 0.85f;
		
		CERBERUS = new Atmosphere();
		CERBERUS.planetRadius = 13170000;
		CERBERUS.atmoRadius = 13800000;
		CERBERUS.Hr = 16000;
		CERBERUS.Hm = 1300f;
		CERBERUS.betaM = new Vector3f(6e-6f, 6e-6f, 6e-6f);
		CERBERUS.betaR = new Vector3f(0.000087f, 0.0000196f, 0.00000352f);
		CERBERUS.mieG = 0.08f;
	}
	
}