package theGhastModding.meshingTest.resources;

import java.io.File;

import theGhastModding.meshingTest.text.FontSet;
import theGhastModding.meshingTest.text.FontType;

public class BasicFonts {
	
	public final FontSet arial;
	
	public BasicFonts(long window) throws Exception {
		FontType plain = new FontType(Loader.loadTextureFromFile("res/fonts/arial.png"), new File("res/fonts/arial.fnt"), window);
		FontType italic = new FontType(Loader.loadTextureFromFile("res/fonts/arial_italic.png"), new File("res/fonts/arial_italic.fnt"), window);
		FontType bold = new FontType(Loader.loadTextureFromFile("res/fonts/arial_bold.png"), new File("res/fonts/arial_bold.fnt"), window);
		FontType italicBold = new FontType(Loader.loadTextureFromFile("res/fonts/arial_bold_italic.png"), new File("res/fonts/arial_bold_italic.fnt"), window);
		arial = new FontSet(plain, italic, bold, italicBold);
	}
	
}