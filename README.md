# IItem Renderer
- Use GL calls during item rendering.
- Allow animations and dynamic rendering
- Change GUI overlay.
- Acess to camera transformations
- Acess to item stack's NBT
- Acess to current partialTicks and TEISR doesn't provide any access to these things that say access

### Installation
1. Download Source
2. Download Evil Notch Lib srg/deob jar and do the same with MC ClassWriter Mod
3. Throw these jars in your libs folder.
3. add "useDepAts = true" under the minecraft section in your build.gradle
4. run gradlew setupDecompWorkspace eclipse
5. add the libs of to your java build path in eclipse

### Usage
- Call IItemRendererHandler.registerIItemRenderer(Item item, IItemRenderer renderer);
- You then implement the interface you just registered
- You can use IItemRendererHandler for rendering enchantment effects as well as access entity holder data there

### Differences with forge 1.7.10 IItem Renderer
- Rendering items with your IItemRenderer you call IItemRendererHandler.renderItem(itemstack,ibakedmodel) or just itemstack.
- You can now override gui overlays it has a default override inside of the interface you can override for your object
- TODO: add ablitility for users to specify if they want to allow enchantments rendering

### Demos
https://minecraft.curseforge.com/projects/silk-spawners-forge-edition
