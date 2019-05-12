package theGhastModding.meshingTest.world.gen;

import theGhastModding.meshingTest.world.Chunk;
import theGhastModding.meshingTest.world.World;
import theGhastModding.meshingTest.world.blocks.Block;

public class WorldGeneratorMandel extends WorldGenerator {
	
	//private double re = -1.99999911758738;
	//private double imag = 0.0;
	//private double r = 5.9e-13;
	private double scale = 5.0;
	private double re = 0.432539867562512;
	private double imag = 0.226118675951765;
	private double r = 3.2e-6;
	private int maxIters = 8192;
	
	public WorldGeneratorMandel(World world) {
		super(world);
	}
	
	private double c1,c2,c3;
	
	@Override
	public void prepare() {
		c1 = 4.0 / (world.getWidth() * scale) * r;
		c2 = (world.getWidth() * scale) / 2.0 * c1;
		c3 = (world.getDepth() * scale) / 2.0 * c1;
		
		/*try {
			BufferedImage testImg = new BufferedImage(512, 16, BufferedImage.TYPE_INT_RGB);
			for(int i = 0; i < 512; i++) {
				int rgb = Color.HSBtoRGB(i / 256f, 1f, i / (i + 8f));
				for(int j = 0; j < 16; j++) {
					testImg.setRGB(i, j, rgb);
				}
			}
			ImageIO.write(testImg, "png", new File("color_test.png"));
		}catch(Exception e) {
			e.printStackTrace();
		}*/
	}
	
	//gold block, leaves, grass, iron block, glass, log, dirt, ores, stone
	
	private final int[] theblocks = {Block.glass.getBlockID(), Block.iron.getBlockID(), Block.gold.getBlockID(), Block.leaves.getBlockID(), Block.log.getBlockID(),
									 Block.dirt.getBlockID(), Block.oreCoal.getBlockID(), Block.oreIron.getBlockID(), Block.oreGold.getBlockID(), Block.oreDiamond.getBlockID(), Block.stone.getBlockID()};;
	
	@Override
	public void generateChunks(Chunk[] c, int chunkx, int chunkz) {
		
		double x,y,xx,yy;
		int iteration;
		for(int row = 0; row < Chunk.CHUNK_WIDTH; row++) {
			double c_im = imag + ((row + chunkx * 16) * c1) - c3;
			for(int col = 0; col < Chunk.CHUNK_DEPTH; col++) {
				double c_re = re + ((col + chunkz * 16) * c1) - c2;
				x = 0; y = 0;
				iteration = 0;
				do {
					xx = x*x;
					yy = y*y;
					y = x*y;
					y += y;
					y += c_im;
					x = xx - yy + c_re;
					iteration++;
				}while(xx + yy <= 4 && iteration < maxIters);
				
				if(iteration < maxIters) {
					iteration %= theblocks.length;
					//iteration++;
					//if(iteration == Block.bedrock.getBlockID()) iteration++;
					c[0].setBlock(row, 8, col, theblocks[iteration]);
				}else{
					c[0].setBlock(row, 8, col, Block.bedrock.getBlockID());
				}
				/*int height = (int)((double)iteration / (double)maxIters * (double)world.getHeight());
				for(int i = 0; i < height; i++) {
					int block = i < height - 3 ? Block.stone.getBlockID() : Block.dirt.getBlockID();
					if(i == height - 1) block = Block.grass.getBlockID();
					c[i / Chunk.CHUNK_HEIGHT].setBlock(row, i % Chunk.CHUNK_HEIGHT, col, block);
				}*/
			}
		}
		
	}
	
	@Override
	public void decorate(int chunkx, int chunkz) {
		
	}
	
	@Override
	public void postLightingFixes(int chunkx, int chunkz) {
		
	}
	
}