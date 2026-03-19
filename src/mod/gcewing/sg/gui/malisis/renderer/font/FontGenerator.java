package gcewing.sg.gui.malisis.renderer.font;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Minimal stub — font texture generation not used at runtime.
 */
public class FontGenerator
{
	public FontGenerator(Font font, CharData[] charData, FontGeneratorOptions options) {}

	public BufferedImage generate(int size, File uvFile, File textureFile)
	{
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	}
}
