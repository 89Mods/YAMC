package theGhastModding.meshingTest.world.gen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class WorldGenSettings {
	
	public int seaLevel = 63;
	public double heightStretch = 160;
	public double mountainStretch = 200;
	public double scaleX = 120;
	public double scaleY = 80;
	public double scaleZ = 120;
	public int noiseMapSize = 350;
	public int stoneOctaves = 16;
	public double stoneLac = 2;
	public double stonePer = 0.5;
	public int caveOctaves = 8;
	public double caveLac = 2;
	public double cavePer = 0.5;
	public int mountainOctaves = 16;
	public double mountainLac = 2;
	public double mountainPer = 0.5;
	public double caveThreshold = 0.55;
	public int treeTries = 100;
	public int coalTries = 20;
	public int ironTries = 20;
	public int goldTries = 2;
	public int redstoneTries = 8;
	public int diamondTries = 1;
	public double caveStretchX = 25;
	public double caveStretchY = 25;
	public double caveStretchZ = 25;
	
	public WorldGenSettings() {
		
	}
	
	public void loadFromFile(File f) {
		try {
			if(!f.exists()) return;
			BufferedReader br = new BufferedReader(new FileReader(f));
			seaLevel = readInt(br.readLine());
			heightStretch = readDouble(br.readLine());
			mountainStretch = readDouble(br.readLine());
			scaleX = readDouble(br.readLine());
			scaleY = readDouble(br.readLine());
			scaleZ = readDouble(br.readLine());
			noiseMapSize = readInt(br.readLine());
			stoneOctaves = readInt(br.readLine());
			stoneLac = readDouble(br.readLine());
			stonePer = readDouble(br.readLine());
			caveOctaves = readInt(br.readLine());
			caveLac = readDouble(br.readLine());
			cavePer = readDouble(br.readLine());
			mountainOctaves = readInt(br.readLine());
			mountainLac = readDouble(br.readLine());
			mountainPer = readDouble(br.readLine());
			caveThreshold = readDouble(br.readLine());
			treeTries = readInt(br.readLine());
			coalTries = readInt(br.readLine());
			ironTries = readInt(br.readLine());
			goldTries = readInt(br.readLine());
			redstoneTries = readInt(br.readLine());
			diamondTries = readInt(br.readLine());
			caveStretchX = readDouble(br.readLine());
			caveStretchY = readDouble(br.readLine());
			caveStretchZ = readDouble(br.readLine());
			br.close();
			System.out.println("World generator settings loaded successfully!");
		}catch(Exception e) {
			System.err.println("Error loading world gen settings file: ");
			e.printStackTrace();
			return;
		}
	}
	
	private int readInt(String s) throws Exception {
		String[] parts = s.split("\\/\\/");
		s = parts[0].trim();
		return Integer.parseInt(s);
	}
	
	private double readDouble(String s) throws Exception {
		String[] parts = s.split("\\/\\/");
		s = parts[0].trim();
		return Double.parseDouble(s);
	}
	
}