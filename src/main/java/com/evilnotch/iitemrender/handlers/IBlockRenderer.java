package com.evilnotch.iitemrender.handlers;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IBlockRenderer {
	
	/**
	 * This is your rendering you can render vanilla by using {@link IItemRendererHandler#renderItemStack(ItemStack, IBakedModel) or IItemRendererHandler#renderModel(ItemStack, IBakedModel)}
	 * @param partialTicks allows partialTicks to interpolate for animations usually the same as Minecraft.getMinecraft().getPartialTicks() unless someone manually renders your renderer
	 * @param breakStage tells you at what break stage this is occuring at
	 */
	public void render(BlockPos pos, IBlockState state, int breakStage, float partialTicks);
	
	/**
	 * the faster render for your renderer
	 */
	public default void renderFast(BlockPos pos, IBlockState state, int breakStage, float partialTicks)
	{
		render(pos, state, breakStage, partialTicks);
	}

	/**
	 * by default most iblockrenderers can just simply render the whole thing again without the model changing each call for the enchantment effect. if it does then simply override this
	 * note: binding textures will not occur here without re-enabling them first {@link IItemRendererHandler#canBind}
	 */
	public default void renderEffect(BlockPos pos, IBlockState state, int breakStage, float partialTicks)
	{
		IBlockRendererHandler.renderIBlockRenderer(this, pos, state, breakStage, partialTicks);
	}

}
