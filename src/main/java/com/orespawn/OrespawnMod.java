package com.orespawn;

import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = OrespawnMod.MODID, name = "OreSpawn", version = "0.1", dependencies = "required-after:evilnotchlib")
public class OrespawnMod {
	
	public static final String MODID = "orespawn";

	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{
		Item item = new BasicItem(new ResourceLocation(MODID, "bertha"), CreativeTabs.REDSTONE, new LangEntry("en_us", "Bertha"));
		IItemRendererHandler.register(item, new RenderBertha());
	}

}
