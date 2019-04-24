package com.evilnotch.iitemrender.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;

public class IItemRendererHandler {

	private static Map<Item, IItemRenderer> registry = new HashMap<>();
	public static RenderItemObj renderItem;
	
	/**
	 * mipmapping lastBlur before starting this rendering process
	 */
	public static boolean lastBlur;
	/**
	 * mipmapping lastMipMap before starting this rendering process
	 */
	public static boolean lastMipmap;
	
	public static double lastX;
	public static double lastY;
	public static double lastZ;
	public static double lastYaw;
	public static double lastPitch;
	public static double lastYawHead;
	/**
	 * the last known entity holder for rendering your IItemRenderer may be null
	 */
	public static Entity lastEntity;
	
	/**
	 * this field gets updated when ForgeHooksClient#handleCameraTransforms()
	 */
	public static TransformType currentTransformType;
	
	/**
	 * returns true if the RenderItemObj is currently running an object
	 */
	public static boolean isRunning;
	
	/**
	 * tell whether or not ForgeHooksClient#handleCameraTransforms() can run the open gl translates
	 */
	public static boolean runGLTranslates;
	
	/**
	 * internal do not manipulate yourself
	 */
	public static boolean leftHandHackery;
	
	/**
	 * it's 64 at see level rather then y of 0 so fake worlds could have a chance of simulating light if they felt like it
	 */
	public static final BlockPos ORIGIN = new BlockPos(0, 64, 0);

	public static IItemRenderer get(Item item)
	{
		return registry.get(item);
	}

	public static IItemRenderer get(ItemStack itemstack)
	{
		return itemstack.isEmpty() ? null : get(itemstack.getItem());
	}
	
	public static Set<Item> getItems() 
	{
		return registry.keySet();
	}
	
	/**
	 * must be called before post init
	 */
	public static void remove(Item item)
	{
		registry.remove(item);
	}
	
	public static boolean hasKey(Item item)
	{
		return registry.containsKey(item);
	}
	
	public static boolean hasKey(ItemStack stack)
	{
		return stack.isEmpty() ? false : registry.containsKey(stack.getItem());
	}

	public static void register(Item item, IItemRenderer renderer)
	{
		registry.put(item, renderer);
	}

	public static void handleCameraTransforms(TransformType type)
	{
		currentTransformType = type;
	}
	
	/**
	 * warning not in it's own matrix. use this to render vanilla models that won't fire IItemRenderer's
	 */
	public static void renderItemStack(ItemStack stack)
	{
		Minecraft mc = Minecraft.getMinecraft();
		IBakedModel model = renderItem.getItemModelWithOverrides(stack, mc.world, mc.player);
		renderItemStack(stack, model);
	}
	
	/**
	 * render an itemstack
	 */
	public static void renderItemStack(ItemStack stack, IBakedModel model)
	{	
		 if(isRunning)
		 {
			 GlStateManager.translate(0.5F, 0.5F, 0.5F);
			 renderItem.child.renderItem(stack, model);
			 GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		 }
		 else
		 {
			 renderItem.child.renderItem(stack, model);
		 }
	}
	
	public static void renderOverlay(FontRenderer fr, ItemStack itemstack, int xPosition, int yPosition, String text) 
	{
		renderItem.child.renderItemOverlayIntoGUI(fr, itemstack, xPosition, yPosition, text);
	}
	
	/**
	 * this only renders the model doesn't call RenderItem#renderItem(stack, model) only will cause recursion if an IItemRenderer mod is doing it wrong and starts the whole process over again
	 */
	public static void renderModel(ItemStack stack)
	{
		renderModel(stack, true);
	}
	
	public static void renderModel(ItemStack stack, boolean enchants)
	{
		Minecraft mc = Minecraft.getMinecraft();
		IBakedModel model = getCurrentRenderItem().getItemModelWithOverrides(stack, mc.world, mc.player);
		renderModel(stack, model, enchants);
	}
	
	/**
	 * this only renders the model doesn't call RenderItem#renderItem(stack, model) and won't fire mod's custom asm data
	 */
	public static void renderModel(ItemStack stack, IBakedModel model, boolean enchants)
	{	
		//TESR support
		GlStateManager.pushMatrix();
		
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		applyGlTranslates(model);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		
		if (model.isBuiltInRenderer())
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            return;
        }
        
		RenderItem render = getCurrentRenderItem();
		render.renderModel(model, stack);

        if(enchants && stack.hasEffect())
        {
        	render.renderEffect(model);
        }
        
        GlStateManager.popMatrix();
	}
	
	/**
	 * may or may not be equal to IItemRendererHandler#instance
	 * @return
	 */
	public static RenderItem getCurrentRenderItem()
	{
		return Minecraft.getMinecraft().renderItem;
	}
	
	/**
	 * update last known pos if not currently running so an entity item starts the intial render the isRunning will be false thus update it
	 */
	public static void updateLastPossiblePos(Entity entityIn)
	{
		if(!isRunning)
		{
			updateLastPosForcibly(entityIn);
		}
	}
	
	/**
	 * update the holder's data to a position with a non existent entity
	 */
	public static void updateLastPossiblePos(BlockPos origin) 
	{
		if(!isRunning)
		{
			updateLastPosForcibly(origin);
		}
	}
	
	public static void updateMipMap()
	{
		if(!isRunning)
		{
			renderItem.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			AbstractTexture texture = (AbstractTexture) renderItem.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			lastBlur = texture.blurLast;
			lastMipmap = texture.mipmapLast;
		}
	}
	
	/**
	 * calling this directly can screw up other IItemRenderers use with caution
	 */
	public static void updateLastPosForcibly(Entity entityIn)
	{
		lastX = entityIn.posX;
		lastY = entityIn.posY;
		lastZ = entityIn.posZ;
		lastYaw = entityIn.rotationYaw;
		lastPitch = entityIn.rotationPitch;
		lastYawHead = entityIn.getRotationYawHead();
		lastEntity = entityIn;
	}
	
	/**
	 * calling this directly can screw up other IItemRenderers use with caution
	 */
	public static void updateLastPosForcibly(BlockPos origin) 
	{
		lastX = origin.getX();
		lastY = origin.getY();
		lastZ = origin.getZ();
		lastYaw = 0.0F;
		lastPitch = 0.0F;
		lastYawHead = 0.0F;
		lastEntity = null;
	}
	
	public static void startBlurMipmap()
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		AbstractTexture texture = (AbstractTexture) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		texture.setBlurMipmap(false, false);
		texture.blurLast = lastBlur;
		texture.mipmapLast = lastMipmap;
	}
	
	public static void restoreLastBlurMipmap()
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        AbstractTexture texture = (AbstractTexture) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        texture.blurLast = lastBlur;
        texture.mipmapLast = lastMipmap;
	}
	/**
	 * is it currently rendering an iitemrenderer enchantment
	 */
	public static boolean enchants = false;
	public static final ResourceLocation GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	public static float enchR = 0.5019608F;
	public static float enchG = 0.2509804F;
	public static float enchB = 0.8F;
	/**
	 * tell mc if you canBind textures or not
	 */
	public static boolean canBind = true;
	/**
	 * disable this for rendering an IBakedModel and it won't render the effect then re-enable it
	 */
	public static boolean allowEnchants = true;
	
	/**
	 * do not call this outside of your model matrix
	 * do not bind textures in your iitemrenderer when rendering the enchantments
	 */
	public static void renderEffect(IItemRenderer renderer, ItemStack stack, IBakedModel model, TransformType type, float partialTicks) 
	{
		renderEffect(renderer, stack, model, type, partialTicks, enchR, enchG, enchB);
	}
	
	/**
	 * do not call this outside of your model matrix
	 * do not bind textures in your iitemrenderer when rendering the enchantments
	 * the rgb vars are float rgb for the enchantment color overlay
	 */
	public static void renderEffect(IItemRenderer renderer, ItemStack stack, IBakedModel model, TransformType type, float partialTicks, float r, float g, float b) 
	{
		if(enchants)
			return;//prevent recursion loops or return if it's disabled
		
		enchants = true;
        GlStateManager.color(r, g, b);
		Minecraft mc = renderItem.mc;
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        mc.getTextureManager().bindTexture(GLINT);
        canBind = false;
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        renderer.renderEffect(stack, model, type, partialTicks);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        renderer.renderEffect(stack, model, type, partialTicks);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        canBind = true;
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		enchants = false;
	}
	
	/**
	 * render enchantments onto a TEISR note that this needs to get called inside of your TEISR and needs to be all one matrix in order to work properly
	 */
	public static void renderEffectTEISR(ItemStack stack) 
	{
		renderEffectTEISR(stack, enchR, enchG, enchB);
	}
	
	/**
	 * render enchantments onto a TEISR note that this needs to get called inside of your TEISR and needs to be all one matrix in order to work properly
	 */
	public static void renderEffectTEISR(ItemStack stack, float r, float g, float b) 
	{
		if(enchants)
			return;//prevent recursion loops
		
		enchants = true;
        GlStateManager.color(r, g, b);
		Minecraft mc = renderItem.mc;
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        mc.getTextureManager().bindTexture(GLINT);
        canBind = false;
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        canBind = true;
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		enchants = false;
	}
	
	/**
	 * render iitemrenderer based upon fancy or fast graphics
	 */
	public static void render(IItemRenderer renderer, ItemStack stack, IBakedModel model, TransformType type, float partialTicks)
	{
		Minecraft mc = renderItem.mc;
		boolean fancy = mc.gameSettings.fancyGraphics;
		if(fancy)
		{
			renderer.render(stack, model, type, partialTicks);
		}
		else
		{
			renderer.renderFast(stack, model, type, partialTicks);
		}
	}

	public static void renderOverlay(IItemRenderer renderer, FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) 
	{
		boolean fancy = renderItem.mc.gameSettings.fancyGraphics;
		if(fancy)
		{
			renderer.renderOverlay(fr, stack, xPosition, yPosition, text);
		}
		else
		{
			renderer.renderOverlayFast(fr, stack, xPosition, yPosition, text);
		}
	}
	
	/**
	 * apply the gl translates vertex data here from an IBakedModel so your post rendering logic will work
	 */
	public static void applyGlTranslates(IBakedModel model) 
	{
		IItemRendererHandler.runGLTranslates = true;
		ForgeHooksClient.handleCameraTransforms(model, IItemRendererHandler.currentTransformType, IItemRendererHandler.leftHandHackery);
		IItemRendererHandler.runGLTranslates = false;
	}

}
