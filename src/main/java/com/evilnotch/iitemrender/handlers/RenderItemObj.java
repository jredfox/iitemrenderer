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
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
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
		if(IItemRendererHandler.instance == null)
		{
			IItemRendererHandler.instance = this;
		}
	}

	@Override
	public void renderItem(ItemStack itemstack, IBakedModel model)
	{
		IItemRenderer renderer = IItemRendererHandler.getIItemRenderer(itemstack);
		if(renderer != null)
		{
			GlStateManager.pushMatrix();
           
            GlStateManager.translate(-0.5F, -0.5F, -0.5F); //setup so the item starts rendering in the top left corner
            
            //enable these for TEISR like rendering
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            
            GlStateManager.enableLighting();//enable this for 3d rendering
  
            TransformType t = IItemRendererHandler.currentTransformType;
            if(t == t.GUI || t == t.FIRST_PERSON_RIGHT_HAND  || t == t.FIRST_PERSON_LEFT_HAND|| t == t.NONE)
            {
            	//if this renderes in a main menu or something don't break it with the player being null
            	if(this.mc.player != null)
            	{
            		IItemRendererHandler.updateLastPosForcibly(this.mc.player);//since I already know it's running don't check
            	}
            	else
            	{
            		IItemRendererHandler.updateLastPosForcibly(IItemRendererHandler.ORIGIN);//since I already know it's running don't check
            	}
            }
            
            IItemRendererHandler.isRunning = true;
    		boolean fancy = this.mc.gameSettings.fancyGraphics;
    		if(fancy)
    		{
    			renderer.render(itemstack, model, IItemRendererHandler.currentTransformType, this.mc.getRenderPartialTicks());
    		}
    		else
    		{
    			renderer.renderFast(itemstack, model, IItemRendererHandler.currentTransformType, this.mc.getRenderPartialTicks());
    		}
            IItemRendererHandler.isRunning = false;
			
			GlStateManager.popMatrix();
		}
		else
		{
			this.child.renderItem(itemstack, model);
		}
	}

	@Override
	public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack itemstack, int xPosition, int yPosition, String text)
	{
		IItemRenderer renderer = IItemRendererHandler.getIItemRenderer(itemstack);
		if(renderer != null)
		{
			boolean fancy = this.mc.gameSettings.fancyGraphics;
			if(fancy)
			{
				renderer.renderOverlay(fr, itemstack, xPosition, yPosition, text);
			}
			else
			{
				renderer.renderOverlayFast(fr, itemstack, xPosition, yPosition, text);
			}
		}
		else
		{
			this.child.renderItemOverlayIntoGUI(fr, itemstack, xPosition, yPosition, text);
		}
	}
}
