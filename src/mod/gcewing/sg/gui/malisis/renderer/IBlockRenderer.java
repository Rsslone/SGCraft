package gcewing.sg.gui.malisis.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.vecmath.Matrix4f;

/**
 * Minimal stub interface — block rendering not used by SGCraft GUI.
 */
public interface IBlockRenderer
{
	public boolean renderBlock(BufferBuilder wr, IBlockAccess world, BlockPos pos, IBlockState state);
	public boolean renderItem(ItemStack itemStack, float partialTick);
	public void setTransformType(TransformType transformType);
	public boolean isGui3d();
	public Matrix4f getTransform(Item item, TransformType tranformType);
}
