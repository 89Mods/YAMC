package theGhastModding.meshingTest.resources;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class TextureHelper {
	
	Loader loader;
	
	public TextureHelper(Loader loader){
		this.loader = loader;
	}
	
	public int createAdvancedCubeTexture(BufferedImage back, BufferedImage front, BufferedImage right, BufferedImage left, BufferedImage top, BufferedImage bottom, int width, int height) throws Exception {
		if(back.getWidth() != width || front.getWidth() != width || right.getWidth() != width || left.getWidth() != width || top.getWidth() != width || bottom.getWidth() != width) throw new Exception("Invalid image size(s)");
		if(back.getHeight() != height || front.getHeight() != height || right.getHeight() != height || left.getHeight() != height || top.getHeight() != height || bottom.getHeight() != height) throw new Exception("Invalid image size(s)");
		BufferedImage fullTexture = new BufferedImage(width * 3, height * 2, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				fullTexture.setRGB(i, j, back.getRGB(i, j));
				fullTexture.setRGB(i + width, j, front.getRGB(i, j));
				fullTexture.setRGB(i + width + width, j, right.getRGB(i, j));
				fullTexture.setRGB(i, j + height, left.getRGB(i, j));
				fullTexture.setRGB(i + width, j + height, top.getRGB(i, j));
				fullTexture.setRGB(i + width + width, j + height, bottom.getRGB(i, j));
			}
		}
		return loader.loadTextureFromBufferedImage(fullTexture);
	}
	
	public int createAdvancedCubeTexture(String back, String front, String right, String left, String top, String bottom, int width, int height) throws Exception {
		return createAdvancedCubeTexture(ImageIO.read(new File(back)), ImageIO.read(new File(front)), ImageIO.read(new File(right)), ImageIO.read(new File(left)), ImageIO.read(new File(top)), ImageIO.read(new File(bottom)), width, height);
	}
	
}