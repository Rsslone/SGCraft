package gcewing.sg.gui.malisis.renderer;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderWorldLastEvent;

/**
 * Minimal stub interface — world-last rendering not used by SGCraft GUI.
 */
public interface IRenderWorldLast
{
	public boolean shouldSetViewportPosition();
	public boolean shouldRender(RenderWorldLastEvent event, IBlockAccess world);
	public void renderWorldLastEvent(RenderWorldLastEvent event, IBlockAccess world);
}
