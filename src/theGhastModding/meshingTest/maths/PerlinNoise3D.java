package theGhastModding.meshingTest.maths;

import java.util.Random;

public class PerlinNoise3D {
	
	private double[][][][] noiseMap;
	private int width,height,depth;
	
	public PerlinNoise3D(Random rng, int width, int height, int depth) {
		this.noiseMap = new double[width + 1][height + 1][depth + 1][3];
		for(int i = 0; i < width + 1; i++) {
			for(int j = 0; j < height + 1; j++) {
				for(int k = 0; k < depth + 1; k++) {
					noiseMap[i][j][k][0] = rng.nextDouble() * 2.0 - 1.0;
					noiseMap[i][j][k][1] = rng.nextDouble() * 2.0 - 1.0;
					noiseMap[i][j][k][2] = rng.nextDouble() * 2.0 - 1.0;
				}
			}
		}
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	private static double lerp(double a0, double a1, double w) {
		return a0 + w * (a1 - a0);
	}
	
	private static double weight(double x) {
		return 3 * (x * x) - 2 * (x * x * x);
	}
	
	private double noise(double x, double y, double z) {
		
		int nodex = (int)x;
		int nodey = (int)y;
		int nodez = (int)z;
		double sx = x - (double)nodex;
		double sy = y - (double)nodey;
		double sz = z - (double)nodez;
		
		double wx = weight(sx);
		double wy = weight(sy);
		double wz = weight(sz);
		
		nodex %= width;
		nodey %= height;
		nodez %= depth;
		
		double dot0 = sx * noiseMap[nodex][nodey][nodez][0] + sy * noiseMap[nodex][nodey][nodez][1] + sz * noiseMap[nodex][nodey][nodez][2];
		double dot1 = (sx - 1) * noiseMap[nodex + 1][nodey][nodez][0] + sy * noiseMap[nodex + 1][nodey][nodez][1] + sz * noiseMap[nodex + 1][nodey][nodez][2];
		double dot2 = sx * noiseMap[nodex][nodey + 1][nodez][0] + (sy - 1) * noiseMap[nodex][nodey + 1][nodez][1] + sz * noiseMap[nodex][nodey + 1][nodez][2];
		double dot3 = (sx - 1) * noiseMap[nodex + 1][nodey + 1][nodez][0] + (sy - 1) * noiseMap[nodex + 1][nodey + 1][nodez][1] + sz * noiseMap[nodex + 1][nodey + 1][nodez][2];
		double dot4 = sx * noiseMap[nodex][nodey][nodez + 1][0] + sy * noiseMap[nodex][nodey][nodez + 1][1] + (sz - 1) * noiseMap[nodex][nodey][nodez + 1][2];
		double dot5 = (sx - 1) * noiseMap[nodex + 1][nodey][nodez + 1][0] + sy * noiseMap[nodex + 1][nodey][nodez + 1][1] + (sz - 1) * noiseMap[nodex + 1][nodey][nodez + 1][2];
		double dot6 = sx * noiseMap[nodex][nodey + 1][nodez + 1][0] + (sy - 1) * noiseMap[nodex][nodey + 1][nodez + 1][1] + (sz - 1) * noiseMap[nodex][nodey + 1][nodez + 1][2];
		double dot7 = (sx - 1) * noiseMap[nodex + 1][nodey + 1][nodez + 1][0] + (sy - 1) * noiseMap[nodex + 1][nodey + 1][nodez + 1][1] + (sz - 1) * noiseMap[nodex + 1][nodey + 1][nodez + 1][2];
		
		double ix0 = lerp(dot0, dot1, wx);
		double ix1 = lerp(dot2, dot3, wx);
		
		double iy0 = lerp(ix0, ix1, wy);
		
		double ix2 = lerp(dot4, dot5, wx);
		double ix3 = lerp(dot6, dot7, wx);
		
		double iy1 = lerp(ix2, ix3, wy);
		
		return lerp(iy0, iy1, wz);
	}
	
	public double sample(double x, double y, double z, double scale) {
		return noise(x, y, z) * scale;
	}
	
	public double sampleNorm(double x, double y, double z, double scale) {
		return (noise(x, y, z) + 1.0) / 2.0 * scale;
	}
	
}