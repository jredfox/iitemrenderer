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
- Register using IItemRendererHandler.registerIItemRenderer()
- For custom overlays override IItemRenderer#renderOverlay
- For faster rendering without fancy graphics override fast methods inside of IItemRenderer
- Render Enchantments IItemRendererHandler.renderModelEffect()
- Access Entity Holder data in IItemRendererHandler
- If your model contains random you need to cache data for enchantment support then override renderEffect() in your IItemRenderer to fully support enchantments into your dynamic random containing mode

### Differences with forge 1.7.10 IItem Renderer
- Rendering items with your IItemRenderer you call IItemRendererHandler.renderItem(itemstack,ibakedmodel) or just itemstack.
- You can now override gui overlays it has a default override inside of the interface you can override for your object
- Ability to render enchantments you must call it yourself in IItemRendererHandler
- Ability to have a fast builtin version of your IItemRenderer for when graphics are fast

### TODO
- IBlockRenderer
- Bug Fixes
- Support for Resource Pack System 1.8+ where you can override the iitemrenderer with a static jsons model

### Demos
https://minecraft.curseforge.com/projects/silk-spawners-forge-edition
