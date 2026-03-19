package gcewing.sg.gui.malisis.util.bbcode.render;

import gcewing.sg.gui.malisis.util.bbcode.BBString;

/**
 * Minimal stub interface — BBCode rendering not used by SGCraft.
 */
public interface IBBCodeRenderer<T>
{
	public BBString getBBText();
	public T setText(BBString text);
	public float getFontScale();
	public int getStartLine();
	public int getVisibleLines();
	public int getLineHeight();
}
