package com.evilnotch.iitemrender.mod;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = IItemRendererMod.MODID, name = IItemRendererMod.NAME, version = IItemRendererMod.VERSION, clientSideOnly = true, dependencies = "required-after:evilnotchlib")
public class IItemRendererMod {

	public static final String MODID = "iitemrenderer";
	public static final String NAME = "IItem Renderer";
	public static final String VERSION = "1.0";//pre-2
	
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
