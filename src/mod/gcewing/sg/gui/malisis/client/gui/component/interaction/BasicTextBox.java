package gcewing.sg.gui.malisis.client.gui.component.interaction;

import gcewing.sg.gui.malisis.client.gui.MalisisGui;

/**
 * Minimal stub — BasicTextBox is a tabbing-aware text box not used by SGCraft.
 */
public class BasicTextBox extends UITextField
{
	private int tabIndex = 0;

	public BasicTextBox(MalisisGui gui, String text)
	{
		super(gui, text);
	}

	public int getTabIndex()
	{
		return tabIndex;
	}

	public BasicTextBox setTabIndex(int index)
	{
		this.tabIndex = index;
		return this;
	}

	public void selectAll() {}

	public void deselectAll() {}

	public void focus() {}
}
