package com.evilnotch.iitemrender.handlers;

import javax.annotation.Nullable;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;

/**
 * This is main interface (and only) of IItem Renderer API. This allows for super-ultra-hyper dynamic item rendering and <u>GL calls</u>. It also has methods for better overlay rendering. <b>It is <u>NOT</u> required to use BOTH!!!</b> 
 * <br>
 * Implement this (you can use anonymous classes) and register instance with {@link IItemRendererAPI#registerIItemRenderer(net.minecraft.item.Item, IItemRenderer)} to begin using dynamic rendering.
 * <br><br>
 * Interface consists of 2 parts, 2 methods each (pre and post): item rendering and item GUI overlay rendering. Item rendering is for item stack rendering and allows to do things like GL calls. GUI overlay is to change overlay displayed in GUI, like adding second durability bar.
 * <br>
 * 2 main things that you can do with this and you can't with model's system:
 * <li>GL Calls (Ex: depth buffer)
 * <li>Super-ultra-hyper dynamic item rendering (Ex: fluid in portable tanks)
 * </li>
 * While license does not allow me to prohibit you to use this where you can use models system, please don't.
 * @author jredfox, elix_x
 *
 */
public interface IItemRenderer {

	/**
	 * This is your rendering you can render vanilla by using {@link IItemRendererHandler#renderItemStack(ItemStack, IBakedModel) or IItemRendererHandler#renderModel(ItemStack, IBakedModel)}
	 * return false to stop vanilla rendering the itemstack
	 * @param renderItem {@link RenderItem} instance that will render the item stack may not be equal to the current Minecraft.getMinecraft().renderItem
	 * @param itemstack {@link ItemStack} that will be rendered
	 * @param model {@link IBakedModel} that will be used to render it
	 * @param type {@link TransformType} that will be/was used to transform model, if it is {@link IPerspectiveAwareModel}
	 * @param partialTicks allows partialTicks to interpolate for animations usually the same as Minecraft.getMinecraft().getPartialTicks() unless someone manually renders your renderer
	 */
	public void render(ItemStack itemstack, IBakedModel model, TransformType type, float partialTicks);
	
	/**
	 * the faster render for your renderer
	 */
	public default void renderFast(ItemStack itemstack, IBakedModel model, TransformType type, float partialTicks)
	{
		render(itemstack, model, type, partialTicks);
	}

	/**
	 * Called in replacement for {@link ItemStack}'s GUI overlay. You can cancel rendering by doing nothing when implementing it or add your own custom logic
	 * @param renderItem {@link RenderItem} instance that will render the overlay
	 * @param fontRenderer {@link FontRenderer} that will be used to render text parts
	 * @param itemstack {@link ItemStack} where data for overlay rendering will be taken
	 * @param xPosition position of overlay's left border on screen
	 * @param yPosition position of overlay's top border on screen
	 * @param text {@link String} that replaces item stack's size, if not null
	 * @return <b>TRUE</b> if rendering should proceed and default overlay should be rendered. <b>FALSE</b> if rendering should be cancelled.
	 */
	public default void renderOverlay(FontRenderer fr, ItemStack itemstack, int xPosition, int yPosition, @Nullable String text)
	{
		IItemRendererHandler.renderOverlay(fr, itemstack, xPosition, yPosition, text);
	}
	
	/**
	 * the faster render of renderOverlay used when Minecraft's game settings are not fancyGraphics
	 */
	public default void renderOverlayFast(FontRenderer fr, ItemStack itemstack, int xPosition, int yPosition, @Nullable String text)
	{
		renderOverlay(fr, itemstack, xPosition, yPosition, text);
	}

}
