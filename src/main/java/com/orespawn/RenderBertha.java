package com.orespawn;

import org.lwjgl.opengl.GL11;

import com.evilnotch.iitemrender.handlers.IItemRenderer;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

public class RenderBertha implements IItemRenderer {
   public ModelBertha modelBertha = new ModelBertha();
   public static final ResourceLocation texture = new ResourceLocation(OrespawnMod.MODID, "bertha_model.png");

   public void renderSword(float x, float y, float z, float scale) 
   {
      GL11.glPushMatrix();
      GL11.glRotatef(190.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
      GL11.glScalef(scale, scale, scale);
      GL11.glTranslatef(x, y, z);
      Minecraft.getMinecraft().renderEngine.bindTexture(texture);
      this.modelBertha.render();
      GL11.glPopMatrix();
   }

   public void renderSwordF5(float x, float y, float z, float scale) 
   {
      GL11.glPushMatrix();
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
      GL11.glScalef(scale, scale, scale);
      GL11.glTranslatef(x, y, z);
   	  Minecraft.getMinecraft().renderEngine.bindTexture(texture);
      this.modelBertha.render();
      GL11.glPopMatrix();
   }

   @Override
   public void render(ItemStack itemstack, IBakedModel model, TransformType type, float partialTicks) 
   {
	   if(type == TransformType.FIRST_PERSON_LEFT_HAND || type == TransformType.FIRST_PERSON_RIGHT_HAND)
	   {
		   this.renderSword(6.0F, 3.0F, -5.0F, 0.25F);
	   }
	   else if(type == type.THIRD_PERSON_RIGHT_HAND)
	   {
		   this.renderSwordF5(-4.0F, 2.0F, -3.0F, 0.25F);
	   }
	   else
	   {
//		   this.renderSword(6.0F, 3.0F, -5.0F, 0.25F);
		   IItemRendererHandler.renderItemStack(itemstack, model);
	   }
   }
}
