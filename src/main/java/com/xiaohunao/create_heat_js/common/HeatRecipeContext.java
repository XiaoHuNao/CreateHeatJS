package com.xiaohunao.create_heat_js.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;


public class HeatRecipeContext {
    private final ResourceLocation recipeId;
    private final ResourceLocation recipeTypeId;


    public HeatRecipeContext(ResourceLocation recipeId, ResourceLocation recipeTypeId) {
        this.recipeId = recipeId;
        this.recipeTypeId = recipeTypeId;
    }

    public static HeatRecipeContext of(Recipe<?> recipe) {
        if (recipe == null) {
            return new HeatRecipeContext(ResourceLocation.fromNamespaceAndPath("minecraft", "unknown"), null);
        }
        ResourceLocation recipeId = recipe.getId();
        ResourceLocation typeId = ForgeRegistries.RECIPE_TYPES.getKey(recipe.getType());
        return new HeatRecipeContext(recipeId, typeId);
    }

    public ResourceLocation getRecipeId() {
        return recipeId;
    }

    public ResourceLocation getRecipeTypeId() {
        return recipeTypeId;
    }
}
