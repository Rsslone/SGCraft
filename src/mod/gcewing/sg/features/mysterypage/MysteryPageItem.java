package gcewing.sg.features.mysterypage;

import gcewing.sg.SGCraft;
import gcewing.sg.generator.GeneratorAddressRegistry;
import gcewing.sg.util.GeneralAddressRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.List;

public class MysteryPageItem extends Item {

    public MysteryPageItem() {
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.ITALIC + "I wonder what's written here.");
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            tooltip.add(TextFormatting.GRAY + "Use to discover what it contains.");
        } else {
            tooltip.add(TextFormatting.GRAY + "[Shift] for more info");
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            // Selects general list if player gates allowed, otherwise generated stargates only.
            String address = SGCraft.allowGeneratedStargateAddressInLoot
                    ? GeneralAddressRegistry.randomAddress(world, "", world.rand)
                    : GeneratorAddressRegistry.randomAddress(world, "", world.rand);
            // If no gate addresses exist, print message.
            if (address == null) {
                player.sendMessage(new TextComponentString(
                        "No stargates known \u2014 explore and build gates first."));
                return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
            }

            ItemStack page = new ItemStack(SGCraft.addressPage, 1);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("address", address);
            page.setTagCompound(tag);

            ItemStack held = player.getHeldItem(hand);
            held.shrink(1);

            if (held.isEmpty()) {
                player.setHeldItem(hand, page);
            } else {
                player.addItemStackToInventory(page);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
