package theGhastModding.meshingTest.text;

public class FontSet {
	
	private FontType plain;
	private FontType italic;
	private FontType bold;
	private FontType italicBold;
	
	public FontSet(FontType plain, FontType italic, FontType bold, FontType italicBold) {
		super();
		this.plain = plain;
		this.italic = italic;
		this.bold = bold;
		this.italicBold = italicBold;
	}
	
	public FontType getPlain() {
		return plain;
	}
	
	public FontType getItalic() {
		return italic;
	}
	
	public FontType getBold() {
		return bold;
	}
	
	public FontType getItalicBold() {
		return italicBold;
	}
	
}