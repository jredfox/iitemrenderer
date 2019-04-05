package com.orespawn;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelBertha extends ModelBase {
   ModelRenderer Grip;
   ModelRenderer Blade;
   ModelRenderer Handguard2;
   ModelRenderer Handguard1;
   ModelRenderer hg2;
   ModelRenderer hg4;
   ModelRenderer hg3;
   ModelRenderer hg1;
   ModelRenderer BaseGrip;
   ModelRenderer Tip1;
   ModelRenderer Tip2;
   ModelRenderer Bottom;

   public ModelBertha() {
      this.textureWidth = 64;
      this.textureHeight = 128;
      this.Grip = new ModelRenderer(this, 0, 0);
      this.Grip.addBox(0.0F, -6.0F, 0.0F, 1, 12, 1);
      this.Grip.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.Grip.setTextureSize(64, 128);
      this.Grip.mirror = true;
      this.setRotation(this.Grip, 0.0F, 0.0F, 0.0F);
      this.Blade = new ModelRenderer(this, 6, 0);
      this.Blade.addBox(0.0F, -41.0F, -1.0F, 1, 34, 3);
      this.Blade.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.Blade.setTextureSize(64, 128);
      this.Blade.mirror = true;
      this.setRotation(this.Blade, 0.0F, 0.0F, 0.0F);
      this.Handguard2 = new ModelRenderer(this, 16, 0);
      this.Handguard2.addBox(0.0F, -7.0F, -4.0F, 1, 1, 9);
      this.Handguard2.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.Handguard2.setTextureSize(64, 128);
      this.Handguard2.mirror = true;
      this.setRotation(this.Handguard2, 0.0F, 0.0F, 0.0F);
      this.Handguard1 = new ModelRenderer(this, 18, 12);
      this.Handguard1.addBox(-3.0F, -7.0F, 0.0F, 7, 1, 1);
      this.Handguard1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.Handguard1.setTextureSize(64, 128);
      this.Handguard1.mirror = true;
      this.setRotation(this.Handguard1, 0.0F, 0.0F, 0.0F);
      this.hg2 = new ModelRenderer(this, 0, 15);
      this.hg2.addBox(0.0F, -8.0F, -5.0F, 1, 1, 1);
      this.hg2.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.hg2.setTextureSize(64, 128);
      this.hg2.mirror = true;
      this.setRotation(this.hg2, 0.0F, 0.0F, 0.0F);
      this.hg4 = new ModelRenderer(this, 0, 18);
      this.hg4.addBox(0.0F, -8.0F, 5.0F, 1, 1, 1);
      this.hg4.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.hg4.setTextureSize(64, 128);
      this.hg4.mirror = true;
      this.setRotation(this.hg4, 0.0F, 0.0F, 0.0F);
      this.hg3 = new ModelRenderer(this, 0, 21);
      this.hg3.addBox(-4.0F, -8.0F, 0.0F, 1, 1, 1);
      this.hg3.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.hg3.setTextureSize(64, 128);
      this.hg3.mirror = true;
      this.setRotation(this.hg3, 0.0F, 0.0F, 0.0F);
      this.hg1 = new ModelRenderer(this, 0, 24);
      this.hg1.addBox(4.0F, -8.0F, 0.0F, 1, 1, 1);
      this.hg1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.hg1.setTextureSize(64, 128);
      this.hg1.mirror = true;
      this.setRotation(this.hg1, 0.0F, 0.0F, 0.0F);
      this.BaseGrip = new ModelRenderer(this, 0, 39);
      this.BaseGrip.addBox(-1.0F, 5.0F, -1.0F, 3, 1, 3);
      this.BaseGrip.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.BaseGrip.setTextureSize(64, 128);
      this.BaseGrip.mirror = true;
      this.setRotation(this.BaseGrip, 0.0F, 0.0F, 0.0F);
      this.Tip1 = new ModelRenderer(this, 21, 16);
      this.Tip1.addBox(0.0F, -42.0F, -0.5F, 1, 1, 2);
      this.Tip1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.Tip1.setTextureSize(64, 128);
      this.Tip1.mirror = true;
      this.setRotation(this.Tip1, 0.0F, 0.0F, 0.0F);
      this.Tip2 = new ModelRenderer(this, 22, 20);
      this.Tip2.addBox(0.0F, -43.0F, 0.0F, 1, 1, 1);
      this.Tip2.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.Tip2.setTextureSize(64, 128);
      this.Tip2.mirror = true;
      this.setRotation(this.Tip2, 0.0F, 0.0F, 0.0F);
      this.Bottom = new ModelRenderer(this, 0, 45);
      this.Bottom.addBox(0.0F, 6.0F, 0.0F, 1, 1, 1);
      this.Bottom.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.Bottom.setTextureSize(64, 128);
      this.Bottom.mirror = true;
      this.setRotation(this.Bottom, 0.0F, 0.0F, 0.0F);
   }

   public void render() {
      float f5 = 1.0F;
      this.Grip.render(f5);
      this.Blade.render(f5);
      this.Handguard2.render(f5);
      this.Handguard1.render(f5);
      this.hg2.render(f5);
      this.hg4.render(f5);
      this.hg3.render(f5);
      this.hg1.render(f5);
      this.BaseGrip.render(f5);
      this.Tip1.render(f5);
      this.Tip2.render(f5);
      this.Bottom.render(f5);
   }

   public void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }
}
