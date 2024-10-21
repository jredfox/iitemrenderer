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

	public static boolean hasChecked = false;
	public static boolean hasLib = true;
	
	@Override
	public String[] getASMTransformerClass() 
	{
		if(!hasChecked)
		{
			try
			{
				Class.forName("com.evilnotch.lib.asm.util.ASMHelper");
			}
			catch(Throwable t)
			{
				t.printStackTrace();
				hasLib = false;
			}
			hasChecked = true;
		}
		return hasLib ? new String[]{"com.evilnotch.iitemrender.asm.RenderTransformer"} : null;
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