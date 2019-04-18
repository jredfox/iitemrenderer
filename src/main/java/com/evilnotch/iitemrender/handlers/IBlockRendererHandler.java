package com.evilnotch.iitemrender.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class IBlockRendererHandler {
	
	public static HashMap<Block,IBlockRenderer> registry = new HashMap(0);
	
	public static IBlockRenderer register(Block item)
	{
		return registry.get(item);
	}
	
	public static Set<Block> getBlocks() 
	{
		return registry.keySet();
	}
	
	/**
	 * must be called before post init
	 */
	public static void remove(Block block)
	{
		registry.remove(block);
	}
	
	public static boolean hasKey(Block block)
	{
		return registry.containsKey(block);
	}
	
	
	public static void renderBlockIntoWorld(BlockPos pos, IBlockState state, IBlockAccess acess)
	{
		//TODO:
	}
	
	/**
	 * do not call this outside of your model matrix
	 * do not bind textures in your iitemrenderer when rendering the enchantments
	 */
	public static void renderEffect(IBlockRenderer renderer, BlockPos pos, IBlockState state, int breakstage, float partialTicks) 
	{
		renderEffect(renderer, pos, state, breakstage, partialTicks, IItemRendererHandler.enchR, IItemRendererHandler.enchG, IItemRendererHandler.enchB);
	}
	
	/**
	 * do not call this outside of your model matrix
	 * do not bind textures in your iitemrenderer when rendering the enchantments
	 * the rgb vars are float rgb for the enchantment color overlay
	 */
	public static void renderEffect(IBlockRenderer renderer, BlockPos pos, IBlockState state, int breakstage, float partialTicks, float r, float g, float b) 
	{
		if(IItemRendererHandler.enchants)
			return;//prevent recursion loops
		
		IItemRendererHandler.enchants = true;
        GlStateManager.color(r, g, b);
		Minecraft mc = IItemRendererHandler.instance.mc;
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        mc.getTextureManager().bindTexture(IItemRendererHandler.GLINT);
        IItemRendererHandler.canBind = false;
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        renderer.renderEffect(pos, state, breakstage, partialTicks);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        renderer.renderEffect(pos, state, breakstage, partialTicks);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        IItemRendererHandler.canBind = true;
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        IItemRendererHandler.enchants = false;
	}
	
	/**
	 * render iitemrenderer based upon fancy or fast graphics
	 */
	public static void render(IBlockRenderer renderer, BlockPos pos, IBlockState state, int breakStage, float partialTicks)
	{
		Minecraft mc = IItemRendererHandler.instance.mc;
		boolean fancy = mc.gameSettings.fancyGraphics;
		if(fancy)
		{
			renderer.render(pos, state, breakStage, partialTicks);
		}
		else
		{
			renderer.renderFast(pos, state, breakStage, partialTicks);
		}
	}


}
