package com.xiaohunao.create_heat_js.common;

import java.util.Optional;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;


public class HeatRecipeContext {
    private final ResourceLocation recipeId;
    private final ResourceLocation recipeTypeId;


    public HeatRecipeContext(ResourceLocation recipeId, ResourceLocation recipeTypeId) {
        this.recipeId = recipeId;
        this.recipeTypeId = recipeTypeId;
    }

    public static HeatRecipeContext of(Level level, Recipe<?> recipe) {
        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null");
        }
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null");
        }

        RecipeManager recipeManager = level.getRecipeManager();
        ResourceLocation typeId = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());

        Optional<RecipeHolder<?>> recipeHolder = recipeManager.getRecipes()
            .stream()
            .filter(holder -> {
                Recipe<?> holderRecipe = holder.value();
                return holderRecipe == recipe || 
                       (holderRecipe.getType().equals(recipe.getType()) && holderRecipe.equals(recipe));
            })
            .findFirst();

        if (recipeHolder.isEmpty()) {
            throw new IllegalStateException("Recipe not found in RecipeManager. Recipe type: " + typeId);
        }

        ResourceLocation recipeId = recipeHolder.get().id();
        return new HeatRecipeContext(recipeId, typeId);
    }

    public ResourceLocation getRecipeId() {
        return recipeId;
    }

    public ResourceLocation getRecipeTypeId() {
        return recipeTypeId;
    }
}
