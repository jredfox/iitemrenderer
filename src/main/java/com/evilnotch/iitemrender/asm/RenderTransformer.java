package com.evilnotch.iitemrender.asm;

import static org.objectweb.asm.Opcodes.ALOAD;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.iitemrender.asm.compat.JEI;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.classwriter.MCWriter;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;

public class RenderTransformer  implements IClassTransformer{
	
    public static final List<String> clazzes = JavaUtil.<String>asArray(new Object[]
    {
		"net.minecraftforge.client.ForgeHooksClient",
		"net.minecraft.client.Minecraft",
		"net.minecraft.client.renderer.entity.RenderManager",
		"net.minecraft.client.renderer.texture.TextureManager",
		"net.minecraft.client.renderer.RenderItem",
		"mezz.jei.render.IngredientListBatchRenderer"
    });

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		if(basicClass == null)
			return null;
		int index = clazzes.indexOf(transformedName);
	    return index != -1 ? transform(index, basicClass, FMLCorePlugin.isObf) : basicClass;
	}
	
	/**
	 * f**k jei
	 */
	public byte[] transform(int index, byte[] basicClass, boolean obfuscated) 
	{
		try 
		{
			ClassNode classNode = ASMHelper.getClassNode(basicClass);
			switch (index)
			{
			  case 0:
				  patchCameraTransform(classNode);
			  break;
			  
			  case 1:
				  patchMinecraft(classNode);
		      break;
		      
			  case 2:
				  patchRenderManager(classNode);
			  break;
			  
			  case 3:
				  patchTextureManager(classNode);
			  break;
			  
			  case 4:
				  patchRenderItem(classNode);
			  break;
			  
			  case 5:
				  JEI.patchJEI(classNode);
		   	  break;
			  
			}
	   		ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        	classNode.accept(classWriter);
        	
        	byte[] bytes = classWriter.toByteArray();
            if(ConfigCore.dumpASM)
            {
            	ASMHelper.dumpFile(clazzes.get(index), bytes);
            }
            
        	return bytes;
		}
		catch (Throwable e) 
		{
			e.printStackTrace();
			return basicClass;
		}
	}

	public static void patchRenderItem(ClassNode classNode) 
	{	
		//add IItemRendererHandler.applyGlTranslates(model) to RenderItem#renderItem
		System.out.println("patching RenderItem.class");
		
		//disable enchantment effects
		MethodNode effects = ASMHelper.getMethodNode(classNode, new MCPSidedString("renderEffect", "func_191966_a").toString(), "(Lnet/minecraft/client/renderer/block/model/IBakedModel;)V");
		AbstractInsnNode spotEffect = ASMHelper.getFirstInstruction(effects, Opcodes.ICONST_0);
		
		//add if(!IItemRendererHandler.allowEnchants) return; to RenderItem#renderEffect(model)
		InsnList toInsert2 = new InsnList();
		toInsert2.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/iitemrender/handlers/IItemRendererHandler", "allowEnchants", "Z"));
		LabelNode l1 = new LabelNode();
		toInsert2.add(new JumpInsnNode(Opcodes.IFNE, l1));
		LabelNode l2 = new LabelNode();
		toInsert2.add(l2);
		toInsert2.add(new InsnNode(Opcodes.RETURN));
		toInsert2.add(l1);
		effects.instructions.insertBefore(spotEffect, toInsert2);
	}

	public static void patchCameraTransform(ClassNode classNode) 
	{
		  System.out.println("patching ForgeHooksClient#handleCameraTransforms");
		  MethodNode camera = ASMHelper.getMethodNode(classNode, "handleCameraTransforms", "(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)Lnet/minecraft/client/renderer/block/model/IBakedModel;");
		 
		  InsnList toInsert = new InsnList();
		  toInsert.add(new VarInsnNode(Opcodes.ALOAD,1));
		  toInsert.add(new VarInsnNode(Opcodes.ILOAD, 2));
		  toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/iitemrender/handlers/IItemRendererHandler", "handleCameraTransforms", "(Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V", false));
	      camera.instructions.insertBefore(ASMHelper.getFirstInstruction(camera, Opcodes.ALOAD), toInsert);
	      
	      AbstractInsnNode[] arr = camera.instructions.toArray();
	      JumpInsnNode spotIf = null;
	      for(AbstractInsnNode ab : arr)
	      {
	    	  if(ab instanceof JumpInsnNode && ab.getOpcode() == Opcodes.IFNULL)
	    	  {
	    		  spotIf = (JumpInsnNode) ab;
	    		  break;
	    	  }
	      }
	      
	      InsnList toInsert2 = new InsnList();
	      toInsert2.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/iitemrender/handlers/IItemRendererHandler", "runTransforms", "Z"));
	      toInsert2.add(new JumpInsnNode(Opcodes.IFEQ, spotIf.label));
	      camera.instructions.insert(spotIf, toInsert2);
	}
	
	public static void patchMinecraft(ClassNode classNode) 
	{
		  System.out.println("patching Minecraft.class for custom RenderItem");
		  MethodNode method = ASMHelper.getMethodNode(classNode, new MCPSidedString("init", "func_71384_a").toString(), "()V");
		  AbstractInsnNode start = null;
		  AbstractInsnNode end = null;
		  
		  AbstractInsnNode[] nodes = method.instructions.toArray();
		  for(AbstractInsnNode ab : nodes)
		  {
			  if(ab.getOpcode() == Opcodes.NEW)
			  {
				  TypeInsnNode node = (TypeInsnNode)ab;
				  if(node.desc.equals("net/minecraft/client/renderer/RenderItem"))
				  {
					  start = node.getPrevious();
				  }
			  }
			  else if(start != null && ab.getOpcode() == Opcodes.PUTFIELD)
			  {
				  end = ab.getPrevious();
				  break;
			  }
		  }
		 
		  InsnList toInsert = new InsnList();
		  toInsert.add(new TypeInsnNode(Opcodes.NEW, "com/evilnotch/iitemrender/handlers/RenderItemObj"));
		  toInsert.add(new InsnNode(Opcodes.DUP));
		  method.instructions.insert(start,toInsert);
		  method.instructions.insert(end,new MethodInsnNode(Opcodes.INVOKESPECIAL,"com/evilnotch/iitemrender/handlers/RenderItemObj", "<init>", "(Lnet/minecraft/client/renderer/RenderItem;)V", false));
	}
	
	/**
	 * make sure IItemRenderHandler knows the entity's last known position for like say item frames
	 */
	public static void patchRenderManager(ClassNode classNode) 
	{
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("renderEntity", "func_188391_a").toString(), "(Lnet/minecraft/entity/Entity;DDDFFZ)V");
		
		InsnList toInsert = new InsnList();
		toInsert.add(new VarInsnNode(ALOAD, 1));
		toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/iitemrender/handlers/IItemRendererHandler", "updateLastPossiblePos", "(Lnet/minecraft/entity/Entity;)V", false));
		AbstractInsnNode point = ASMHelper.getFirstInstruction(node, Opcodes.ACONST_NULL);
		node.instructions.insertBefore(point, toInsert);
	}
	
	/**
	 * stop the binding of textures when it is not allowed
	 */
	public static void patchTextureManager(ClassNode classNode) 
	{
		System.out.println("Patching TextureManager.class to deny binding textures when IItemRendererHandler#canBind is false");
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("bindTexture","func_110577_a").toString(), "(Lnet/minecraft/util/ResourceLocation;)V");
		AbstractInsnNode start = ASMHelper.getFirstInstruction(node, Opcodes.ALOAD);
		
		InsnList toInsert = new InsnList();
		toInsert.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/iitemrender/handlers/IItemRendererHandler", "canBind", "Z"));
		LabelNode l1 = new LabelNode();
		toInsert.add(new JumpInsnNode(Opcodes.IFNE, l1) );
		LabelNode l2 = new LabelNode();
		toInsert.add(l2);
		toInsert.add(new InsnNode(Opcodes.RETURN));
		toInsert.add(l1);
		
		node.instructions.insertBefore(start, toInsert);
	}

}
