package com.xiaohunao.create_heat_js.common.mixin.classes.category;

import com.xiaohunao.create_heat_js.common.utils.CategoryHelper;
import fr.lucreeper74.createmetallurgy.compat.jei.category.FoundryBasinCategory;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = FoundryBasinCategory.class, remap = false)
public abstract class FoundryBasinCategoryMixin {
    @Inject(
            method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lfr/lucreeper74/createmetallurgy/content/blocks/foundry_basin/FoundryBasinRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At(
                    value = "INVOKE",
                    ordinal = 0,
                    target = "Lfr/lucreeper74/createmetallurgy/content/blocks/foundry_basin/FoundryBasinRecipe;getRequiredHeat()Lcom/simibubi/create/content/processing/recipe/HeatCondition;"),
            cancellable = true
    )
    private void chj$setRecipe(IRecipeLayoutBuilder builder, FoundryBasinRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
        if (!CategoryHelper.setCustomHeatSourceRecipe(builder, recipe)) {
            return;
        }

        ci.cancel();
    }
}
