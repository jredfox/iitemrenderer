package com.evilnotch.iitemrender.asm.compat;

import static org.objectweb.asm.Opcodes.ALOAD;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.util.ASMHelper;

import net.minecraft.item.Item;

public class JEI {
	
	public static Set<Item> slowItems = new HashSet();
	
	public static void patchJEI(ClassNode classNode)
	{
		System.out.println("Transforming JEI(mezz.jei.render.IngredientListBatchRenderer) to Accept IItemRenderer");
		MethodNode method = ASMHelper.getMethodNode(classNode, "set", "(Lmezz/jei/render/IngredientListSlot;Lmezz/jei/gui/ingredients/IIngredientListElement;)V");

		AbstractInsnNode[] arr = method.instructions.toArray();
		AbstractInsnNode spot = null;
		for(int i=arr.length-1;i>=0;i--)
		{
			AbstractInsnNode node = arr[i];
			if(node.getOpcode() == Opcodes.INSTANCEOF && node instanceof TypeInsnNode)
			{
				TypeInsnNode type = (TypeInsnNode)node;
				if(type.desc.equals("mezz/jei/api/ingredients/ISlowRenderItem"))
				{
					spot = type;
					while(spot.getOpcode() != Opcodes.IFNE)
					{
						spot = spot.getNext();
						if(spot.getOpcode() == Opcodes.RETURN)
							break;
					}
					break;
				}
			}
		}
	
		JumpInsnNode spotting = (JumpInsnNode)spot;
		int varLoad = ASMHelper.getLocalVarIndexFromOwner(method,"Lnet/minecraft/item/ItemStack;");
		InsnList toInsert = new InsnList();
		toInsert.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/iitemrender/asm/compat/JEI", "slowItems", "Ljava/util/Set;"));
		toInsert.add(new VarInsnNode(ALOAD,varLoad));
   		toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"net/minecraft/item/ItemStack", new MCPSidedString("getItem","func_77973_b").toString(), "()Lnet/minecraft/item/Item;", false));
   		toInsert.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Set", "contains", "(Ljava/lang/Object;)Z", true));
   		toInsert.add(new JumpInsnNode(Opcodes.IFNE,spotting.label));
   		method.instructions.insert(spotting,toInsert);
	}
}
