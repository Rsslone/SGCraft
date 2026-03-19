package gcewing.sg.gui.malisis.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Minimal stub — inventory container not used by SGCraft GUI.
 */
public class MalisisInventoryContainer
{
	public enum ActionType
	{
		DROP_ONE, DROP_STACK, DRAG_RESET, DRAG_END, DRAG_START, DRAG_ADD,
		LEFT_CLICK, RIGHT_CLICK, SHIFT_LEFT_CLICK, SHIFT_RIGHT_CLICK,
		DOUBLE_CLICK, PICKBLOCK, HOTBAR;
	}

	public ItemStack getPickedItemStack()
	{
		return ItemStack.EMPTY;
	}

	public boolean shouldResetDrag(int button)
	{
		return false;
	}

	public boolean shouldEndDrag(int button)
	{
		return false;
	}

	public void onContainerClosed(EntityPlayer player) {}

	public void handleAction(ActionType action, int inventoryId, int slotNumber, int code) {}
}
