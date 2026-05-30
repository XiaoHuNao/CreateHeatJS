<div align="center">

# Create Heat JS

[中文](README_zh.md) | **English**

![icon.png](./src/main/resources/icon.png)

[![curseforge-badge]][curseforge-url] [![modrinth-badge]][modrinth-url] [![github-badge]][github-url]
</div>

Allows using KubeJS to customize Create's Heat Source Blocks and Heat Levels.

Please use [KubeJS Create Mod](https://modrinth.com/mod/kubejs-create/) when adding recipes, and use the `.heatLevel()` method to set the heat level.

## Example

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
  event.recipes.create.compacting("minecraft:water", "minecraft:blue_ice").heatLevel("CRYOTHEUM");
});
```

### Startup Scripts

```js
CreateHeatJS.registerHeatEvent(event => {
    // 1. Basic Example: Register custom heat source BLAZE
    event.registerHeat("BLAZE", builder => builder
        .color(0xFF4500)
        .addHeatSource("minecraft:magma_block")
        .satisfies("HEATED")
    )

    // 2. Advanced Example: Register CRYOTHEUM
    event.registerHeat("CRYOTHEUM", builder => builder
        .color(0x00BFFF)
        .addHeatSource("#minecraft:ice")

        // Conditional heat source: only works in the Nether
        .addHeatSourceIf((level, pos) => {
            if (level.dimension === "minecraft:the_nether") {
                return level.getBlockState(pos).block.id === "minecraft:soul_lantern"
            }
            return false
        },"minecraft:soul_lantern",Component.translatable("create_heat_js.heat_source.cryotheum.soul_lantern.tip"))

        .satisfies("HEATED")
        .satisfiesIf("SUPERHEATED", ctx => ctx.getRecipeId() == "create:mixing/lava_from_cobble"))

    // 3. Modify existing heat level
    event.modifyHeat("SUPERHEATED", data => data
        .satisfies("CUSTOM_LEVEL")
    )
})

/**
 * Heat Source String Format:
 * - blocktag:namespace:path (Block Tag)
 * - fluidtag:namespace:path (Fluid Tag)
 * - block:namespace:path (Block ID)
 * - fluid:namespace:path (Fluid ID)
 * - namespace:path[prop=value] (Block State)
 * - #namespace:path (Try to match Block or Fluid Tag)
 */
```

## Understanding `satisfies` Relationship

### Basic Concept

`satisfies()` establishes a **单向关系**: if `A.satisfies("B")`, then:
- Heat source with level **A** can complete recipes requiring **B**
- But heat source with level **B** **cannot** complete recipes requiring **A**

```
A ──satisfies──> B    means    A ≥ B

Recipes need B ──> Can use A ✓
Recipes need A ──> Cannot use B ✗
```

### Example: Create's Original Hierarchy

Create mod has a built-in relationship: `SUPERHEATED → HEATED`

| Recipe Requirement | Blaze Burner (HEATED) | Blaze Burner (SUPERHEATED) |
|-------------------|----------------------|---------------------------|
| HEATED | ✓ | ✓ |
| SUPERHEATED | ✗ | ✓ |

This means: SUPERHEATED satisfies HEATED, but HEATED does not satisfy SUPERHEATED.

### Your Custom Relationships

When you write:
```js
event.registerHeat("BLAZE", builder => builder
    .satisfies("HEATED")
)
```

You're creating: `BLAZE → HEATED`

| Recipe Requirement | BLAZE heat source | Blaze Burner (HEATED) |
|-------------------|-------------------|----------------------|
| HEATED | ✓ | ✓ |
| BLAZE | ✓ | ✗ |

### Three Types of Relationships

#### 1. Vertical Relationship (Linear Hierarchy)
```js
// Create a chain: LEVEL3 → LEVEL2 → LEVEL1
event.registerHeat("LEVEL2", builder => builder.satisfies("LEVEL1"))
event.registerHeat("LEVEL3", builder => builder.satisfies("LEVEL2"))
```
Result: LEVEL3 can satisfy LEVEL2 and LEVEL1 (transitive)

#### Transitive Property (Auto-compatibility)

The relationship is **transitive**. If you write:
```js
// Create already has: SUPERHEATED → HEATED
// You add: PYROTHEUM → SUPERHEATED
event.registerHeat("PYROTHEUM", builder => builder
    .satisfies("SUPERHEATED")
)
```

PYROTHEUM will **automatically** satisfy HEATED too! No need to write `.satisfies("HEATED")`.

```
PYROTHEUM → SUPERHEATED → HEATED
    │           │           │
    └───────────┴───────────┘
         (all satisfied by PYROTHEUM)
```

| Recipe Requirement | PYROTHEUM |
|-------------------|-----------|
| HEATED | ✓ (auto) |
| SUPERHEATED | ✓ |
| PYROTHEUM | ✓ |

#### 2. Horizontal Relationship (Independent System)
```js
// No satisfies() - completely independent
event.registerHeat("COLD", builder => builder
    .color(0x00BFFF)
    .addHeatSource("#minecraft:ice")
)
```
Result: COLD is independent, not connected to Create's heat system

#### 3. Cross Relationship
```js
// CRYOTHEUM is independent but can also satisfy Create's conditions
event.registerHeat("CRYOTHEUM", builder => builder
    .satisfies("HEATED")  // Can satisfy HEATED recipes
    .satisfiesIf("SUPERHEATED", ctx => ctx.getRecipeId() == "specific_recipe")
)
```
Result: CRYOTHEUM is its own level, but can cross into Create's hierarchy

### Relationship Diagram

```
Create's Original:
  NONE ← HEATED ← SUPERHEATED
         (satisfies)

Your Custom:
  BLAZE ──satisfies──> HEATED
  (BLAZE ≥ HEATED)

Combined:
  NONE ← HEATED ← SUPERHEATED
          ↑
          └── BLAZE

Recipes needing HEATED: Can use HEATED, SUPERHEATED, or BLAZE
Recipes needing BLAZE:  Can only use BLAZE
```
```

[curseforge-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/curseforge_vector.svg
[curseforge-url]: https://www.curseforge.com/minecraft/mc-mods/create-heat-js
[modrinth-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/modrinth_vector.svg
[modrinth-url]: https://modrinth.com/mod/create-heat-js
[github-badge]: https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/github_vector.svg
[github-url]: https://github.com/XiaoHuNao/CreateHeatJS
