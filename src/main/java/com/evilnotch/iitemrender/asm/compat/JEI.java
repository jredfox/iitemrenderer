package com.evilnotch.iitemrender.asm.compat;

import static org.objectweb.asm.Opcodes.ALOAD;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

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
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.item.Item;

public class JEI {
	
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

		
		//append && JavaUtil.returnFalse() to jei patcher so jei always renders RenderItem#renderItem()
		JumpInsnNode spotting = (JumpInsnNode)spot;
		int varLoad = ASMHelper.getLocalVarIndexFromOwner(method,"Lnet/minecraft/item/ItemStack;");
		
		InsnList toInsert = new InsnList();
	    toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/util/JavaUtil", "returnFalse", "()Z", false));
	    toInsert.add(new JumpInsnNode(Opcodes.IFEQ, spotting.label));
	    
   		method.instructions.insert(spotting, toInsert);
	}
}
