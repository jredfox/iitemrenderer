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
	public void render(ItemStack stack, IBakedModel model, TransformType type, float partialTicks);
	
	/**
	 * return what type of preset open gl transformations will occur
	 * @TYPE FIXED 1.7.10 fixed transforms ported
	 * @TYPE NONE no transforms done besides GlStateManager.translate(-0.5F, -0.5F, -0.5F);
	 */
	public TransformPreset getTransformPreset();
	
	/**
	 * This gets fired when your iitemrenderer is rendering another item and was unable to reset opn gl before 
	 * continuing due to recursion. Example An item renders an entity which then enables cull. Entity Head starts to render an item you want cull to be disabled so you override this
	 * do not use this for transforms(scaling,rotation,translate) as this will can get fired more then once without checks
	 */
	public default void restoreLastOpenGl()
	{
		
	}
	
	/**
	 * does your iitemrenderer support resource pack overrides. if it's a hook like silkspawners return false but, if it's like just a model like orespawn big bertha return true
	 */
//	public boolean allowRPOverride(ItemStack itemstack, IBakedModel model, TransformType type);
	
	/**
	 * the faster render for your renderer
	 */
	public default void renderFast(ItemStack stack, IBakedModel model, TransformType type, float partialTicks)
	{
		render(stack, model, type, partialTicks);
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
	public default void renderOverlay(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text)
	{
		IItemRendererHandler.renderOverlay(fr, stack, xPosition, yPosition, text);
	}
	
	/**
	 * the faster render of renderOverlay used when Minecraft's game settings are not fancyGraphics
	 */
	public default void renderOverlayFast(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text)
	{
		renderOverlay(fr, stack, xPosition, yPosition, text);
	}
	
	/**
	 * by default most iitemrenderers can just simply render the whole thing again without the model changing each call for the enchantment effect. if it does then simply override this
	 * note: binding textures will not occur here without re-enabling them first {@link IItemRendererHandler#canBind}
	 */
	public default void renderEffect(ItemStack stack, IBakedModel model, TransformType type, float partialTicks)
	{
		IItemRendererHandler.render(this, stack, model, type, partialTicks);
	}
	
	public static enum TransformPreset
	{
		FIXED(),
		VANILLA(),
		NONE();
	}

}
