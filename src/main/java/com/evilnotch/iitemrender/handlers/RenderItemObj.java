package com.evilnotch.iitemrender.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RenderItemObj extends RenderItem {
	
	public RenderItem child;
	public Minecraft mc;

	public RenderItemObj(RenderItem renderItem)
	{
		super(Minecraft.getMinecraft().getTextureManager(), renderItem.getItemModelMesher().getModelManager(), Minecraft.getMinecraft().getItemColors());
		this.child = renderItem;
		this.mc = Minecraft.getMinecraft();
		if(IItemRendererHandler.renderItem == null)
		{
			IItemRendererHandler.renderItem = this;
		}
	}

	@Override
	public void renderItem(ItemStack itemstack, IBakedModel model)
	{	
        IItemRendererHandler.updateMipMap();//sync mipmaps if not in recursion loop
		if(IItemRendererHandler.isRunning)
		{
			 IItemRendererHandler.startBlurMipmap();//make sure in recursion loops this gets set and at the end of the loop it gets set with whatever it was before
		}
		
		IItemRenderer renderer = IItemRendererHandler.get(itemstack);
		if(renderer != null)
		{
			GlStateManager.pushMatrix();
			
			IItemRendererHandler.applyTransforms(renderer, model);
			
            GlStateManager.enableLighting();//enable this for 3d rendering
            GlStateManager.translate(-0.5F, -0.5F, -0.5F); //setup so the item starts rendering in the top left corner
            
            //enable these for TEISR like rendering
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
  
            TransformType t = IItemRendererHandler.currentTransformType;
            float pt = this.mc.getRenderPartialTicks();
            IItemRendererHandler.updateLastPos(t);
            
    		IItemRendererHandler.isRunning = true;
            IItemRendererHandler.render(renderer, itemstack, model, t, pt);
    		IItemRendererHandler.isRunning = false;
    		
			GlStateManager.popMatrix();
		}
		else
		{
			this.child.renderItem(itemstack, model);
		}
	}
	
	/**
	 * use this for custom tooltips and custom bars
	 * gets called after the iitemrenderer is rendered so holder data is auto synced
	 */
	@Override
	public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text)
	{ 
		IItemRenderer renderer = IItemRendererHandler.get(stack);
		if(renderer != null)
		{
			IItemRendererHandler.renderOverlay(renderer, fr, stack, xPosition, yPosition, text);
		}
		else
		{
			this.child.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text);
		}
	}
}
