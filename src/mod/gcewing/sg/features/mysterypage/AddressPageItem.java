package gcewing.sg.features.mysterypage;

import gcewing.sg.network.SGChannel;
import gcewing.sg.tileentity.DHDTE;
import gcewing.sg.tileentity.SGBaseTE;
import gcewing.sg.util.SGAddressing;
import gcewing.sg.util.SGState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.List;

public class AddressPageItem extends Item {

    public AddressPageItem() {
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String base = super.getItemStackDisplayName(stack);
        if (stack.hasTagCompound()) {
            String title = stack.getTagCompound().getString("title");
            if (!title.isEmpty()) return base + " \u2014 " + title;
        }
        return base;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        if (stack.hasTagCompound()) {
            String title = stack.getTagCompound().getString("title");
            if (!title.isEmpty()) tooltip.add(TextFormatting.GREEN + title);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            tooltip.add(TextFormatting.GRAY + "Can shift+right click a DHD to dial.");
        } else {
            tooltip.add(TextFormatting.GRAY + "[Shift] for more info");
        }
    }

    // Shift-right-clicking a DHD with an address page attempt to dial.
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,
            EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) return EnumActionResult.PASS;
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.hasTagCompound()) return EnumActionResult.PASS;
        String address = stack.getTagCompound().getString("address");
        if (address.isEmpty()) return EnumActionResult.PASS;
        try {
            SGAddressing.validateAddress(address);
        } catch (SGAddressing.AddressingError e) {
            return EnumActionResult.PASS;
        }
        DHDTE dhd = DHDTE.at(world, pos);
        if (dhd == null || !dhd.isLinkedToStargate) return EnumActionResult.PASS;
        SGBaseTE gate = dhd.getLinkedStargateTE();
        if (gate == null) return EnumActionResult.PASS;
        if (world.isRemote) {
            SGChannel.sendConnectOrDisconnectToServer(gate,
                    gate.state == SGState.Idle ? address : "");
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            openScreen(player.getHeldItem(hand), hand);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @SideOnly(Side.CLIENT)
    private void openScreen(ItemStack stack, EnumHand hand) {
        if (!stack.hasTagCompound()) return;
        String address = stack.getTagCompound().getString("address");
        if (address.isEmpty()) return;
        String title = stack.getTagCompound().getString("title");
        Minecraft.getMinecraft().displayGuiScreen(
                new gcewing.sg.features.mysterypage.client.gui.AddressPageScreen(address, title, hand));
    }
}
