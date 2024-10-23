package com.evilnotch.iitemrender.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.evilnotch.iitemrender.handlers.IItemRenderer.TransformPreset;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;

public class IItemRendererHandler {

	private static Map<Item, IItemRenderer> registry = new HashMap<>();
	public static RenderItemObj renderItem;
	/**
	 * An IBakedModel cache that tells {@link ForgeHooksClient#handleCameraTransforms(IBakedModel, TransformType, boolean)} to not apply transforms
	 * This is dynamically populated and works regardless of IBakedModel Wrappers or unexpected overrides. It should Be compatible with Everything that works with the 1.8+ wheel
	 */
	public static Set<IBakedModel> cache = new HashSet<>();
	
	/**
	 * mipmapping lastBlur before starting this rendering process
	 */
	public static volatile boolean lastBlur;
	/**
	 * mipmapping lastMipMap before starting this rendering process
	 */
	public static volatile boolean lastMipmap;
	
	public static double lastX;
	public static double lastY = 64;
	public static double lastZ;
	public static double lastYaw;
	public static double lastPitch;
	public static double lastYawHead;
	/**
	 * the last known entity holder for rendering your IItemRenderer may be null
	 */
	public static Entity lastEntity;
	
	/**
	 * this field gets updated when ForgeHooksClient#handleCameraTransforms() use with caution as this can change even during an iitemrenderer use lastTransformType or cachedTrasnformType
	 */
	public static TransformType currentTransformType;
	/**
	 * this is always the first transform type at the start of the recursive loop of camera transitions
	 */
	public static TransformType cachedTransformType;
	/**
	 * gets set to current transform type after {@link IItemRenderer#restoreLastOpenGl()} is called.
	 */
	public static TransformType lastTransformType;
	
	/**
	 * returns true if the RenderItemObj is currently running an object
	 */
	public static volatile boolean isRunning;
	
	/**
	 * returns true if and only if your IItemRendererHandler is at the top of the recursion of rendering
	 */
	public static volatile boolean firstRun = true;
	
	/**
	 * tell whether or not ForgeHooksClient#handleCameraTransforms() can run the open gl transforms(scaling,translates,rotations)
	 */
	public static volatile boolean runTransforms;
	
	/**
	 * internal do not manipulate yourself
	 */
	public static volatile boolean leftHandHackery;
	/**
	 * the left hand hackery at the start of rendering your iitemrenderer
	 */
	public static volatile boolean cachedLeftHandHackery;
	
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
		if(item == Items.AIR)
			throw new IllegalArgumentException("Item/Block cannot be AIR");
		registry.put(item, renderer);
	}
	
	public static void register(Block block, IItemRenderer renderer)
	{
		Item item = Item.getItemFromBlock(block);
		register(item, renderer);
	}
	
	/**
	 * gets ran by ASM when forge handle camera transforms gets called
	 */
	public static void handleCameraTransforms(TransformType type, boolean leftHand)
	{
		lastTransformType = currentTransformType != null ? currentTransformType : type;
		currentTransformType = type;
		leftHandHackery = leftHand;
		if(!isRunning)
		{
			cachedTransformType = type;
			cachedLeftHandHackery = leftHand;
		}
	}
	
	/**
	 * use this to restore last known Transform type that it started out with
	 */
	public static void restoreCachedTransforms()
	{
		handleCameraTransforms(cachedTransformType, cachedLeftHandHackery);
	}

	/**
	 * render a vanilla itemstack
	 */
	public static void renderItemStack(ItemStack stack)
	{
		Minecraft mc = Minecraft.getMinecraft();
		IBakedModel model = renderItem.getItemModelWithOverrides(stack, mc.world, mc.player);
		renderItemStack(stack, model);
	}
	
	/**
	 * render a vanilla itemstack
	 */
	public static void renderItemStack(ItemStack stack, IBakedModel model)
	{
		renderItemStack(stack, model, true);
	}
	
	/**
	 * render a vanilla itemstack
	 * WARNING this can make the depthMask(true) again use with caution
	 */
	public static void renderItemStack(ItemStack stack, IBakedModel model, boolean allowEnch)
	{	
		boolean cachedEnch = allowEnchants;
		allowEnchants = allowEnch;//if false disables enchantments for vanilla IBakedModels
		GlStateManager.pushMatrix();
		
		Block b = Block.getBlockFromItem(stack.getItem());
		boolean flag = model.isGui3d() && b.getBlockLayer() == BlockRenderLayer.TRANSLUCENT;
		if(flag)
		{
			GlStateManager.depthMask(false);//for things like ice rendering and crap
		}
		
		if(isRunning)
		{
			translateDefault(false);
			applyTransforms(model);
			renderItem.child.renderItem(stack, model);
		 	translateDefault(true);
		}
		else
		{
			applyTransforms(model);
			renderItem.child.renderItem(stack, model);
		}
		
	 	if(flag)
	 	{
	 		GlStateManager.depthMask(true);
	 	}
		GlStateManager.popMatrix();
		allowEnchants = cachedEnch;
	}
	
	public static void renderOverlay(FontRenderer fr, ItemStack itemstack, int xPosition, int yPosition, String text) 
	{
		renderItem.child.renderItemOverlayIntoGUI(fr, itemstack, xPosition, yPosition, text);
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
	
	/**
	 * update last pos when entity holder data is null/player but, still renders
	 */
	public static void updateLastPos(TransformType t)
	{
        if(IItemRendererHandler.isGui(t) || IItemRendererHandler.isFirstPerson(t))
        {
        	//if this renderes in a main menu or something don't break it with the player being null
        	if(renderItem.mc.player != null)
        	{
        		IItemRendererHandler.updateLastPossiblePos(renderItem.mc.player);//we don't know if this is calling it recursively so don't assume that it's not running
        	}
        	else
        	{
        		IItemRendererHandler.updateLastPossiblePos(IItemRendererHandler.ORIGIN);//we don't know if this is calling it recursively so don't assume that it's not running
        	}
        }
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
	public static IItemRenderer lastRenderer = null;
	
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
        GlStateManager.color(r, g, b);
        renderer.renderEffect(stack, model, type, partialTicks);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.color(r, g, b);
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
        GlStateManager.color(r, g, b);
        stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.color(r, g, b);
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
	 * apply the vanilla gl translates/scaling/rotations data here from an IBakedModel so your post rendering logic will work
	 * do not call this directly from an iitemrenderer without gl translating 0.5F+
	 */
	public static void applyTransforms(IBakedModel model) 
	{
		applyTransforms(model, IItemRendererHandler.currentTransformType, IItemRendererHandler.leftHandHackery);
	}
	
	public static void applyTransforms(IBakedModel model, TransformType transformType, boolean leftHandHackery) 
	{
		IItemRendererHandler.runTransforms = true;
		ForgeHooksClient.handleCameraTransforms(model, transformType, leftHandHackery);
		IItemRendererHandler.runTransforms = false;
	}
	
	/**
	 * Tells {@link ForgeHooksClient#handleCameraTransforms(IBakedModel, TransformType, boolean)} can run or not
	 */
	public static boolean canRunTransforms(IBakedModel model) 
	{
		return !cache.contains(model) || runTransforms;
	}
	
	/**
	 * if you want your iitemrenderer to conform to transforms of the IBakedModel call this
	 */
	public static void applyTransformPreset(IBakedModel model)
	{
		translateDefault(false);
		applyTransforms(model);
		translateDefault(true);
	}

	/**
	 * if you want your iitemrenderer to conform to transforms of the IBakedModel call this
	 */
	public static void applyTransformPreset(IBakedModel model, TransformType transformType, boolean leftHandHackery)
	{
		translateDefault(false);
		applyTransforms(model, transformType, leftHandHackery);
		translateDefault(true);
	}
	
	public static void translateDefault(boolean negative)
	{
		if(negative)
		{
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		}
		else
		{
			GlStateManager.translate(0.5F, 0.5F, 0.5F);
		}
	}

	/**
	 * apply the transforms per iitemrenderer
	 */
	public static void applyTransforms(IItemRenderer renderer, IBakedModel vanilla) 
	{
		TransformPreset preset = renderer.getTransformPreset();
		if(preset == TransformPreset.FIXED)
		{
			applyLegacyTransforms(currentTransformType);
		}
		else if(preset == TransformPreset.VANILLA)
		{
			applyTransforms(vanilla);
		}
	}

	public static void applyLegacyTransforms(TransformType type)
	{
		//TODO:
	}
	
	public static boolean isThirdPerson(TransformType type)
	{
		return type == type.THIRD_PERSON_RIGHT_HAND || type == type.THIRD_PERSON_LEFT_HAND;
	}
	
	public static boolean isFirstPerson(TransformType type)
	{
		return type == type.FIRST_PERSON_RIGHT_HAND || type == type.FIRST_PERSON_LEFT_HAND;
	}
	
	/**
	 * type none is most likely a gui or gui like thing so both gui and none type return true
	 */
	public static boolean isGui(TransformType type)
	{
		return type == type.GUI || type == type.NONE;
	}
	
	public static boolean isEntityItem(TransformType type)
	{
		return type == type.GROUND;
	}
	
	public static boolean isItemFrame(TransformType type)
	{
		return type == type.FIXED;
	}
	
	public static boolean isUnkown(TransformType type)
	{
		return type == type.NONE;
	}

	public static boolean isHead(TransformType type)
	{
		return type == type.HEAD;
	}

	public static void restoreLastOpenGl()
	{
		if(lastRenderer != null)
			lastRenderer.restoreLastOpenGl();
	}
}
