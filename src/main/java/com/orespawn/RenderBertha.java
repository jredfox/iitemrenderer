package com.orespawn;

import org.lwjgl.opengl.GL11;

import com.evilnotch.iitemrender.handlers.IItemRenderer;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

public class RenderBertha implements IItemRenderer {
   public ModelBertha modelBertha = new ModelBertha();
   public static final ResourceLocation texture = new ResourceLocation(OrespawnMod.MODID, "textures/models/bertha.png");

   public void renderSword(float x, float y, float z, float scale) 
   {
      GlStateManager.rotate(190.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(25.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.scale(scale, scale, scale);
      GlStateManager.translate(x, y, z);
      Minecraft.getMinecraft().renderEngine.bindTexture(texture);
      this.modelBertha.render();
   }

   public void renderSwordF5(float x, float y, float z, float scale) 
   {
      GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.scale(scale, scale, scale);
      GlStateManager.translate(x, y, z);
      Minecraft.getMinecraft().renderEngine.bindTexture(texture);
      this.modelBertha.render();
   }

   @Override
   public void render(ItemStack stack, IBakedModel model, TransformType type, float partialTicks) 
   {   
	   if(type == TransformType.FIRST_PERSON_LEFT_HAND || type == TransformType.FIRST_PERSON_RIGHT_HAND)
	   {
		   this.renderSword(6.0F, 3.0F, -5.0F, 0.25F);
		   if(stack.hasEffect())
			   IItemRendererHandler.renderModelEffect(this, stack, model, type, partialTicks);
	   }
	   else if(type == type.THIRD_PERSON_RIGHT_HAND || type == type.THIRD_PERSON_LEFT_HAND)
	   {
		   this.renderSwordF5(-4.0F, 2.0F, -3.0F, 0.25F);
		   if(stack.hasEffect())
			   IItemRendererHandler.renderModelEffect(this, stack, model, type, partialTicks);
	   }
	   else
	   {
		   IItemRendererHandler.renderModel(stack);
	   }
   }
}
