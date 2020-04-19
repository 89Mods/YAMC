package theGhastModding.meshingTest.maths;

import java.util.Random;

public class OctaveNoise {
	
	private PerlinNoise noise;
	private double persistence;
	private double lacunarity;
	private int octaves;
	
	public OctaveNoise(Random rng, int width, int height, int octaves, double lacunarity, double persistence) {
		this.noise = new PerlinNoise(rng, (int)(width * octaves), (int)(height * octaves));
		this.persistence = persistence;
		this.lacunarity = lacunarity;
		this.octaves = octaves;
	}
	
	public double sample(double x, double y) {
		if(x < 0) x = (double)Integer.MAX_VALUE + x;
		if(y < 0) y = (double)Integer.MAX_VALUE + y;
		double finalRes = 0;
		double max = 0;
		
		double currFreq = 1.0;
		double currSc = 1.0;
		for(int i = 0; i < octaves; i++) {
			finalRes += noise.sample(x * currFreq, y * currFreq) * currSc;
			max += currSc;
			currFreq *= lacunarity;
			currSc *= persistence;
		}
		
		return finalRes / max;
	}
	
}