package com.evilnotch.iitemrender.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@IFMLLoadingPlugin.Name("iitemrenderer-transformer_fixes")
@IFMLLoadingPlugin.SortingIndex(1010)
@IFMLLoadingPlugin.MCVersion("1.12.2")
@TransformerExclusions("com.evilnotch.iitemrender.asm.")
public class Plugin implements IFMLLoadingPlugin
{

	@Override
	public String[] getASMTransformerClass() 
	{
		return new String[]{"com.evilnotch.iitemrender.asm.RenderTransformer"};
	}

	@Override
	public String getModContainerClass() 
	{
		return null;
	}

	@Override
	public String getSetupClass() 
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) 
	{
		
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
	
}