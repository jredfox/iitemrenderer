package com.evilnotch.iitemrender.mod;

import java.io.File;

import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = IItemRendererMod.MODID, name = IItemRendererMod.NAME, version = IItemRendererMod.VERSION, clientSideOnly = true, dependencies = "required-after:evilnotchlib")
public class IItemRendererMod {

	public static final String MODID = "iitemrenderer";
	public static final String NAME = "IItem Renderer";
	public static final String VERSION = "1.0";//pre-6
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		cfg.load();
		String[] strbl = cfg.getStringList("BlacklistedModels", "general", new String[]{"codechicken.lib.render.item.IItemRenderer"}, "Add IBakedModels From other Mods that Use a Different RenderItemObj like Code Chicken Lib");
		for(String sc : strbl)
		{
			sc = sc.trim();
			if(sc.isEmpty())
				continue;
			Class c = ReflectionUtil.classForName(sc);
			if(c != null && JavaUtil.isClassExtending(IBakedModel.class, c))
			{
				IItemRendererHandler.HasBLT = true;
				IItemRendererHandler.BLT.add(c);
			}
			else
			{
				System.out.println("Skipping Blacklisted IBakedModel:" + sc);
			}
		}
		cfg.save();
	}
	
	/**
	 * add jei support to the tabs
	 */
	@EventHandler
	public void postinit(FMLPostInitializationEvent event)
	{
		if(Loader.isModLoaded("jei"))
		{
			try 
			{
				Class c = Class.forName("mezz.jei.render.IngredientListBatchRenderer");
				String s = c.getName();
			} 
			catch (Throwable e) 
			{
				e.printStackTrace();
			}
		}
	}

}
