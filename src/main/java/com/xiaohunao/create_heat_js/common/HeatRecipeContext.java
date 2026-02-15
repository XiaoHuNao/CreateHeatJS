package com.xiaohunao.create_heat_js.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;


public class HeatRecipeContext {
    private final ResourceLocation recipeId;
    private final ResourceLocation recipeTypeId;


    public HeatRecipeContext(ResourceLocation recipeId, ResourceLocation recipeTypeId) {
        this.recipeId = recipeId;
        this.recipeTypeId = recipeTypeId;
    }

    public static HeatRecipeContext of(Recipe<?> recipe) {
        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath("minecraft", "unknown");
        ResourceLocation typeId = recipe == null ? null : BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
        return new HeatRecipeContext(recipeId, typeId);
    }

    public ResourceLocation getRecipeId() {
        return recipeId;
    }

    public ResourceLocation getRecipeTypeId() {
        return recipeTypeId;
    }
}
