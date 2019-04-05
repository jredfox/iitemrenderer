# IItem Renderer
IItem Renderer API gives you ability to hook into item rendering. More specifically:

- Use GL calls during item rendering.
- Super-ultra-hyper-dynamic rendering.
- Change GUI overlay.
- Acess to camera transformations and itemstack nbt always unlike TEISR

### Installation
- Download Source
- Download Evil Notch Lib srg/deob jar and do the same with MC ClassWriter Mod
- Throw these jars in your libs folder.
- add "useDepAts = true" under the minecraft section in your build.gradle
- run gradlew setupDecompWorkspace eclipse
- add the libs of to your java build path in eclipse

### Usage
- Call IItemRendererHandler.registerIItemRenderer(Item item, IItemRenderer renderer);
- You then implement the interface you just registered

### Differences with forge 1.7.10 IItem Renderer
While mostly everything is the same, there is 1 crucial difference: In forge's 1.7.10 IItemRenderer, you used it to override rendering. Here you use it in conjuction with default rendering. But you can use it to override.

### Demos
https://minecraft.curseforge.com/projects/silk-spawners-forge-edition
