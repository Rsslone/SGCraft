package gcewing.sg.gui.malisis.client.gui.component;

import gcewing.sg.gui.malisis.client.gui.MalisisGui;

/**
 * Minimal stub — inventory slots not used by SGCraft GUI.
 */
import gcewing.sg.gui.malisis.client.gui.GuiRenderer;

public class UISlot extends UIComponent<UISlot>
{
	public static boolean buttonRelased = false;

	public UISlot(MalisisGui gui)
	{
		super(gui);
	}

	@Override
	public void drawBackground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick) {}

	@Override
	public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick) {}
}
