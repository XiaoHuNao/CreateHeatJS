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
    event.registerHeat("BLAZE", builder => builder
        .color(0xFF4500)
        .addHeatSource("minecraft:magma_block")
        .satisfies("HEATED")
    )

    // 2. 高级示例：注册 CRYOTHEUM
    event.registerHeat("CRYOTHEUM", builder => builder
        .color(0x00BFFF)
        .addHeatSource("#minecraft:ice")

        // 条件热源：仅在下界生效
        .addHeatSourceIf((level, pos) => {
            if (level.dimension === "minecraft:the_nether") {
                return level.getBlockState(pos).block.id === "minecraft:soul_lantern"
            }
            return false
        },"minecraft:soul_lantern",Component.translatable("create_heat_js.heat_source.cryotheum.soul_lantern.tip"))

        .satisfies("HEATED")
        .satisfiesIf("SUPERHEATED", ctx => ctx.getRecipeId() == "create:mixing/lava_from_cobble"))

    // 3. 修改已存在的热量等级
    event.modifyHeat("SUPERHEATED", data => data
        .satisfies("CUSTOM_LEVEL")
    )
})

/**
 * 热源字符串格式：
 * - blocktag:namespace:path (方块标签)
 * - fluidtag:namespace:path (流体标签)
 * - block:namespace:path (方块 ID)
 * - fluid:namespace:path (流体 ID)
 * - namespace:path[prop=value] (方块状态)
 * - #namespace:path (尝试匹配方块或流体标签)
 */
```

## 理解 `satisfies` 关系网

### 基本概念

`satisfies()` 建立的是**单向关系**：如果 `A.satisfies("B")`，则：
- 拥有 **A** 等级的热源可以完成需要 **B** 的配方
- 但拥有 **B** 等级的热源**不能**完成需要 **A** 的配方

```
A ──satisfies──> B    表示    A ≥ B

配方需要 B ──> 可以用 A 完成 ✓
配方需要 A ──> 不能用 B 完成 ✗
```

### 示例：Create 原版的层级关系

Create 模组内置关系：`SUPERHEATED → HEATED`

| 配方需求 | 烈焰人燃烧室 (HEATED) | 烈焰人燃烧室 (SUPERHEATED) |
|---------|---------------------|---------------------------|
| HEATED | ✓ | ✓ |
| SUPERHEATED | ✗ | ✓ |

意思：SUPERHEATED 满足 HEATED，但 HEATED 不满足 SUPERHEATED。

### 你的自定义关系

当你写：
```js
event.registerHeat("BLAZE", builder => builder
    .satisfies("HEATED")
)
```

你创建的是：`BLAZE → HEATED`

| 配方需求 | BLAZE 热源 | 烈焰人燃烧室 (HEATED) |
|---------|-----------|----------------------|
| HEATED | ✓ | ✓ |
| BLAZE | ✓ | ✗ |

### 三种关系类型

#### 1. 竖向关系（线性层级）
```js
// 创建链式关系：LEVEL3 → LEVEL2 → LEVEL1
event.registerHeat("LEVEL2", builder => builder.satisfies("LEVEL1"))
event.registerHeat("LEVEL3", builder => builder.satisfies("LEVEL2"))
```
结果：LEVEL3 可以满足 LEVEL2 和 LEVEL1（传递性）

#### 传递性（自动兼容）

关系具有**传递性**。如果你写：
```js
// Create 已有：SUPERHEATED → HEATED
// 你添加：PYROTHEUM → SUPERHEATED
event.registerHeat("PYROTHEUM", builder => builder
    .satisfies("SUPERHEATED")
)
```

PYROTHEUM 会**自动**满足 HEATED！不需要再写 `.satisfies("HEATED")`。

```
PYROTHEUM → SUPERHEATED → HEATED
    │           │           │
    └───────────┴───────────┘
         (都能被 PYROTHEUM 满足)
```

| 配方需求 | PYROTHEUM |
|---------|-----------|
| HEATED | ✓ (自动) |
| SUPERHEATED | ✓ |
| PYROTHEUM | ✓ |

#### 2. 横向关系（独立体系）
```js
// 没有 satisfies() - 完全独立
event.registerHeat("COLD", builder => builder
    .color(0x00BFFF)
    .addHeatSource("#minecraft:ice")
)
```
结果：COLD 是独立的，不与 Create 的热量系统产生关联

#### 3. 交叉关系
```js
// CRYOTHEUM 是独立的，但也可以满足 Create 的条件
event.registerHeat("CRYOTHEUM", builder => builder
    .satisfies("HEATED")  // 可以满足 HEATED 配方
    .satisfiesIf("SUPERHEATED", ctx => ctx.getRecipeId() == "特定配方")
)
```
结果：CRYOTHEUM 是自己的等级，但可以交叉进入 Create 的层级体系

### 关系图示

```
Create 原版：
  NONE ← HEATED ← SUPERHEATED
         (满足关系)

你的自定义：
  BLAZE ──satisfies──> HEATED
  (BLAZE ≥ HEATED)

合并后：
  NONE ← HEATED ← SUPERHEATED
          ↑
          └── BLAZE

需要 HEATED 的配方：可用 HEATED、SUPERHEATED、BLAZE
需要 BLAZE 的配方：只能用 BLAZE
```

[curseforge-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/curseforge_vector.svg
[curseforge-url]: https://www.curseforge.com/minecraft/mc-mods/create-heat-js
[modrinth-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/modrinth_vector.svg
[modrinth-url]: https://modrinth.com/mod/create-heat-js
[github-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/github_vector.svg
[github-url]: https://github.com/XiaoHuNao/CreateHeatJS
