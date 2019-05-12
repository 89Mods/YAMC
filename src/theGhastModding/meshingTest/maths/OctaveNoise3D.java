package theGhastModding.meshingTest.maths;

import java.util.Random;

public class OctaveNoise3D {
	
	private PerlinNoise3D[] noises;
	private double att;
	private double cr;
	
	public OctaveNoise3D(Random rng, int width, int height, int depth, int octaves, double att, double cr) {
		this.noises = new PerlinNoise3D[octaves];
		for(int i = 0; i < octaves; i++) {
			double freq = Math.pow(att, i);
			this.noises[i] = new PerlinNoise3D(rng, (int)(width * 1 / freq) + 1, (int)(height * 1 / freq) + 1, (int)(depth * 1 / freq) + 1);
		}
		this.att = att;
		this.cr = cr;
	}
	
	public double sample(double x, double y, double z, double scale) {
		if(x < 0) x = (double)Integer.MAX_VALUE + x;
		if(y < 0) y = (double)Integer.MAX_VALUE + y;
		if(z < 0) z = (double)Integer.MAX_VALUE + z;
		double finalRes = 0;
		double max = 0;
		
		double currFreq = 1.0;
		double currSc = 1.0;
		for(int i = 0; i < noises.length; i++) {
			finalRes += noises[i].sample(x * currFreq, y * currFreq, z * currFreq, currSc);
			max += currSc;
			currFreq *= att;
			currSc *= cr;
		}
		
		return finalRes / max * scale;
	}
	
	public double sampleNorm(double x, double y, double z, double scale) {
		return (sample(x, y, z, 1.0) + 1.0) / 2.0 * scale;
	}
	
}