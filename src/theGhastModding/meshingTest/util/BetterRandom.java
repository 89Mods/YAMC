package theGhastModding.meshingTest.util;

import java.util.Random;

import edu.cornell.lassp.houle.RngPack.RanMT;
import edu.cornell.lassp.houle.RngPack.RandomSeedable;

@SuppressWarnings("serial")
public class BetterRandom extends Random {
	
	private RandomSeedable rng;
	
	public BetterRandom(RandomSeedable rng) {
		this.rng = rng;
	}
	
	public BetterRandom() {
		this.rng = new RanMT();
	}
	
	public BetterRandom(long seed) {
		this.rng = new RanMT(seed);
	}
	
	public RandomSeedable getRNG() {
		return this.rng;
	}
	
	synchronized public void setSeed(long seed) {
		if(rng != null) rng.setSeed(seed);
	}
	
	public void nextBytes(byte[] bytes) {
		for(int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte)rng.choose(Byte.MIN_VALUE, Byte.MAX_VALUE);
		}
	}
	
	public int nextInt(int bound) {
		return rng.choose(bound + 1) - 1;
	}
	
	public int nextInt() {
		return (int)Math.round(rng.choose((double)Integer.MIN_VALUE, (double)Integer.MAX_VALUE));
	}
	
	public long nextLong() {
		return (long)Math.round(rng.choose((double)Long.MIN_VALUE, (double)Long.MAX_VALUE));
	}
	
	public boolean nextBoolean() {
		return rng.coin();
	}
	
	public float nextFloat() {
		return (float)rng.raw();
	}
	
	public double nextDouble() {
		return rng.raw();
	}
	
    synchronized public double nextGaussian() {
    	return rng.gaussian(1.0);
    }
	
}