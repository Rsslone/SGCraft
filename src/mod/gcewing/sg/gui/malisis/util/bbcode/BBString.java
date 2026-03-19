package gcewing.sg.gui.malisis.util.bbcode;

import java.util.List;

import gcewing.sg.gui.malisis.client.gui.GuiRenderer;
import gcewing.sg.gui.malisis.renderer.font.StringWalker;

/**
 * Minimal stub — BBCode text not used by SGCraft.
 */
public class BBString
{
	private String rawText;

	public BBString(String text)
	{
		this.rawText = text != null ? text : "";
	}

	public String getRawText()
	{
		return rawText;
	}

	public void buildRenderLines(List<?> lines)
	{
		// No-op
	}

	public void render(GuiRenderer renderer, int x, int y, int z, Object... params)
	{
		// No-op — BBCode rendering not supported
	}
}
