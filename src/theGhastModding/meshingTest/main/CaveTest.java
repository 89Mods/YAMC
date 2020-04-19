package theGhastModding.meshingTest.main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import edu.cornell.lassp.houle.RngPack.RanMT;

public class CaveTest {
	
	private static boolean[][][] map;
	private static final int width = 128;
	private static final int height = 32;
	private static final int depth = 128;
	private static final int deathLimit = 9;
	private static final int birthLimit = 12;
	
	public static void main(String[] args) {
		try {
			
			double aliveChance = 0.4;
			Random rng = new RanMT();
			
			map = new boolean[width][height][depth];
			for(int i = 0; i < width; i++) {
				for(int j = 0; j < height; j++) {
					for(int k = 0; k < depth; k++) {
						map[i][j][k] = rng.nextDouble() < aliveChance;
					}
				}
			}
			
			for(int i = 0; i < 10; i++) {
				simulationStep();
			}
			
			for(int k = 0; k < height; k++) {
				BufferedImage img = new BufferedImage(width * 2, depth * 2, BufferedImage.TYPE_INT_RGB);
				for(int i = 0; i < width; i++) {
					for(int j = 0; j < depth; j++) {
						int col;
						
						if(!map[i][k][j]) col = Color.BLUE.getRGB();
						else col = 0x00909090;
						
						for(int a = 0; a < 2; a++) {
							for(int b = 0; b < 2; b++) {
								img.setRGB(i * 2 + a, j * 2 + b, col);
							}
						}
					}
				}
				
				JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(img)), "Noise", JOptionPane.INFORMATION_MESSAGE);
			}
		}catch(Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void simulationStep() {
		boolean[][][] newMap = new boolean[width][height][depth];
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				for(int k = 0; k < depth; k++) {
					int nbs = countAliveNeighbors(i, j, k);
					
					//Cell is alive, but has too few neighbors = it dies
					if(map[i][j][k]) {
						newMap[i][j][k] = nbs >= deathLimit;
					}else { //Cell is dead and has enough neighbors to be 'born'
						newMap[i][j][k] = nbs > birthLimit;
					}
				}
			}
		}
		map = newMap;
	}
	
	private static int countAliveNeighbors(int x, int y, int z) {
		int count = 0;
		for(int i = -1; i < 2; i++) {
			for(int j = -1; j < 2; j++) {
				for(int k = -1; k < 2; k++) {
					if(i == 0 && j == 0 && k == 0) continue;
					int n_x = x + i;
					int n_y = y + j;
					int n_z = z + k;
					if(n_x < 0 || n_y < 0 || n_z < 0 || n_x >= width || n_y >= height || n_z >= depth) {
						count++;
					}else if(map[n_x][n_y][n_z]){
						count++;
					}
				}
			}
		}
		return count;
	}
	
}