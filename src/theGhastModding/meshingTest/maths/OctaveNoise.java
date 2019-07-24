package theGhastModding.meshingTest.maths;

import java.util.Random;

public class OctaveNoise {
	
	private PerlinNoise[] noises;
	private double att;
	private double cr;
	
	public OctaveNoise(Random rng, int width, int height, int octaves, double att, double cr) {
		this.noises = new PerlinNoise[octaves];
		for(int i = 0; i < octaves; i++) {
			this.noises[i] = new PerlinNoise(rng, width, height);
		}
		this.att = att;
		this.cr = cr;
	}
	
	public double sample(double x, double y, double scale) {
		if(x < 0) x = (double)Integer.MAX_VALUE + x;
		if(y < 0) y = (double)Integer.MAX_VALUE + y;
		double finalRes = 0;
		double max = 0;
		
		double currFreq = 1.0;
		double currSc = 1.0;
		for(int i = 0; i < noises.length; i++) {
			finalRes += noises[i].sample(x * currFreq, y * currFreq, currSc);
			//if(finalRes > 1000) return 1000000;
			//if(finalRes < -1000) return -1000000;
			max += currSc;
			currFreq *= att;
			currSc *= cr;
		}
		
		return finalRes / max * scale;
	}
	
	public double sampleNorm(double x, double y, double scale) {
		return (sample(x, y, 1.0) + 1.0) / 2.0 * scale;
	}
	
}