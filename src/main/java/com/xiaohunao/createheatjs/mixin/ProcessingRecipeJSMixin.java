package com.xiaohunao.createheatjs.mixin;


import com.simibubi.create.content.processing.recipe.HeatCondition;
import dev.latvian.mods.kubejs.create.ProcessingRecipeSchema;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@RemapPrefixForJS("createheatjs$")
@Mixin(ProcessingRecipeSchema.ProcessingRecipeJS.class)
public abstract class ProcessingRecipeJSMixin extends RecipeJS{
    @Unique
    public RecipeJS createheatjs$heatLevel(HeatCondition heatLevel) {
        return setValue(ProcessingRecipeSchema.HEAT_REQUIREMENT, heatLevel.serialize());
    }
}