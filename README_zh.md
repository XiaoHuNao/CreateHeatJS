<div align="center">

# Create Heat JS

**中文** | [English](README.md)

![icon.png](./src/main/resources/icon.png)

[![curseforge-badge]][curseforge-url] [![modrinth-badge]][modrinth-url] [![github-badge]][github-url]
</div>

允许使用KubeJS自定义Create的热源与热源等级

添加配方时请使用模组[KubeJS Create Mod](https://modrinth.com/mod/kubejs-create/)  `.heatLevel()` 方法设置配方所需热源等级

## 示例

![Recipe Example](./example/recipe_example_1.gif)
![Recipe Example](./example/recipe_example_2.png)

### Client Scripts

```js
ClientEvents.lang("en_us", (event) => {
    event.add("create.recipe.heat_requirement.blaze", "Blaze");
    event.add("create.recipe.heat_requirement.cryotheum", "Cryotheum");
    event.add("create_heat_js.heat_source.cryotheum.soul_lantern.tip", "needs to be in the nether dimension");
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
CreateHeatJS.registerHeatEvent(event => {
    // 1. 基础示例：注册自定义热源 BLAZE
    // 使用 addHeatSource(String blockId) - 最常用的方式
    event.registerHeat("BLAZE", builder => builder
        .color(0xFF4500)
        .addHeatSource("minecraft:magma_block") //Block
        .satisfies("HEATED")
    )

    // 2. 高级示例：注册 CRYOTHEUM
    // 展示所有重载方法的使用
    event.registerHeat("CRYOTHEUM", builder => builder
        .color(0x00BFFF)
        .addHeatSource("#minecraft:ice") //BlockTags

        // 示例：如果在下界 (dimension check)，且方块是 soul_lantern
        .addHeatSourceIf((level, pos) => {
            if (level.dimension === "minecraft:the_nether") {
                return level.getBlockState(pos).block.id === "minecraft:soul_lantern"
            }
            return false
        },"minecraft:soul_lantern",Component.translatable("create_heat_js.heat_source.cryotheum.soul_lantern.tip"))

        // 关系网：满足 HEATED 条件
        .satisfies("HEATED")

        // 条件关系网：仅在特定配方中满足
        // 示例：如果配方 ID  "create:mixing/lava_from_cobble"，则视为满足 "SUPERHEATED"
        .satisfiesIf("SUPERHEATED", ctx => ctx.getRecipeId() == "create:mixing/lava_from_cobble"))

    /**
     * 添加热源（支持字符串格式）
     * 支持的格式：
     * - blocktag:namespace:path (方块标签)
     * - fluidtag:namespace:path (流体标签)
     * - block:namespace:path (方块 ID)
     * - fluid:namespace:path (流体 ID)
     * - namespace:path[prop=value] (方块状态)
     * - #namespace:path (尝试匹配方块或流体标签)
     *
     * @param heatSource 热源
     * @return Builder
     */
    //addHeatSource(String heatSource)

    /**
     * @param heatSourceDisplayItem jei热源槽显示的物品
     * @return Builder
     */
    //heatSourceSlotItem(ItemStack heatSourceDisplayItem)

    /**
     * @param catalystDisplayItem jei催化物槽显示的物品
     * @return Builder
     */
    //catalyst(ItemStack catalystDisplayItem)


    //可以通过satisfies或satisfiesIf方法完善热量等级关系网,拓展原Create模组线性关系等级,允许竖向,横向,交叉关系

    //竖向关系：使用satisfies()创建线性层级
    //示例：TEST1满足HEATED需求（需要HEATED的配方可以使用TEST1）
    // event.registerHeat("TEST1", builder => builder.satisfies("HEATED"))

    //横向关系：注册新等级但不绑定到现有等级
    //示例：COLD是独立的，不满足任何Create热量条件

    //交叉关系：如上注册的CRYOTHEUM等级,属于横向独立体系,但使用satisfies和satisfiesIf交叉回Create模组的SUPERHEATED, HEATED线性关系


    // 3. 修改已存在的热量等级
    // 使用 modifyHeat() 向已存在的热量等级添加热源或关系
    event.modifyHeat("SUPERHEATED", data => data
        .satisfies("TEST1") // SUPERHEATED 满足 TEST1（需要 TEST1 的配方可以使用 SUPERHEATED）
    )
})
```

[curseforge-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/curseforge_vector.svg
[curseforge-url]: https://www.curseforge.com/minecraft/mc-mods/create-heat-js
[modrinth-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/modrinth_vector.svg
[modrinth-url]: https://modrinth.com/mod/create-heat-js
[github-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/github_vector.svg
[github-url]: https://github.com/XiaoHuNao/CreateHeatJS
