<div align="center">

# Create Heat JS

![icon.png](./src/main/resources/icon.png)

[![curseforge-badge]][curseforge-url] [![modrinth-badge]][modrinth-url] [![github-badge]][github-url]
</div>

Allow to use KubeJS customize Create's Heat Source Block, HeatLevel.

Please use [KubeJS Create Mod](https://modrinth.com/mod/kubejs-create/) when adding recipes, and use the `.heatLevel()` method to set the heat level.

## Example

![Recipe Example](./example/recipe_example_1.png)
![Recipe Example](./example/recipe_example_2.png)

### Client Scripts

```js
ClientEvents.lang("en_us", (event) => {
    event.add("create.recipe.heat_requirement.blaze", "Blaze");
    event.add("create.recipe.heat_requirement.cryotheum", "Cryotheum");
    event.add("gui.create.jei.category.basin.heat.cryotheum.title", "Need Ice Spikes Biome");
});
```

### Server Scripts

```js
ServerEvents.recipes((event) => {
  event.recipes.create.mixing("minecraft:diamond", "minecraft:coal_block").heatLevel("BLAZE");
  event.recipes.create.compacting("thermal:blizz_powder", "minecraft:blue_ice").heatLevel("CRYOTHEUM");
});
```

### Startup Scripts

```js
const $AbstractFurnaceBlock = Java.loadClass("net.minecraft.world.level.block.AbstractFurnaceBlock");

CreateHeatJS.registerHeatEvent((event) => {
    event.registerHeat("BLAZE", 3, 0xed9c33)
        .addHeatSource("minecraft:furnace","minecraft:furnace[lit=true]",(level,pos,blockStack) => {
            if (blockStack.hasProperty($AbstractFurnaceBlock.LIT)) {
                return blockStack.getValue($AbstractFurnaceBlock.LIT).booleanValue();
            }
            return false
        })
        .register()

    event.registerHeat("CRYOTHEUM", -1, 0x8BAAFF)
        .addHeatSource("minecraft:blue_ice", (level, pos, blockStack) => {
            return level.getBiome(pos).is(new ResourceLocation("minecraft:ice_spikes"));
        })
        .jeiTip()
        .register()
})
```

[curseforge-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/curseforge_vector.svg
[curseforge-url]: https://www.curseforge.com/minecraft/mc-mods/create-heat-js
[modrinth-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/modrinth_vector.svg
[modrinth-url]: https://modrinth.com/mod/create-heat-js
[github-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/github_vector.svg
[github-url]: https://github.com/XiaoHuNao/CreateHeatJS
