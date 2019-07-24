package theGhastModding.meshingTest.main;

import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import edu.cornell.lassp.houle.RngPack.RanMT;
import theGhastModding.meshingTest.maths.OctaveNoise;
import theGhastModding.meshingTest.util.BetterRandom;

public class PerlinNoiseTest {
	
	public static void main(String[] args) {
		try {
			BufferedImage img = new BufferedImage(1024, 512, BufferedImage.TYPE_INT_RGB);
			
			long seed = (System.currentTimeMillis() + System.nanoTime()) * Runtime.getRuntime().totalMemory();
			Random rng = new BetterRandom(new RanMT(new int[] {(int)seed, (int)(seed >> 8), (int)(seed >> 32)}));
			OctaveNoise noise = new OctaveNoise(rng, 350, 350, 16, 2, 0.5);
			//PerlinNoise noise = new PerlinNoise(rng, 32, 256);
			
			for(int i = 0; i < img.getWidth(); i++) {
				for(int j = 0; j < img.getHeight(); j++) {
					
					double n = noise.sampleNorm((i + 7864000.0) / 120.0, (j + 7864100.0) / 120.0, 512.0) - 64;
					int col = (int)n;
					if(col < 0) col = 0;
					if(col > 255) col = 255;
					img.setRGB(i, j, col | (col << 8) | (col << 16));
				}
			}
			
			JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(img)), "Noise", JOptionPane.INFORMATION_MESSAGE);
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}