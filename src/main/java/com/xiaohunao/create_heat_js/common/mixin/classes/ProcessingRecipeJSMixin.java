package com.xiaohunao.create_heat_js.common.mixin.classes;

import com.xiaohunao.create_heat_js.common.HeatData;
import com.xiaohunao.create_heat_js.common.HeatManager;
import dev.latvian.mods.kubejs.create.ProcessingRecipeSchema;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Locale;


@Mixin(
    value = {ProcessingRecipeSchema.ProcessingRecipeJS.class},
    remap = false
)
public abstract class ProcessingRecipeJSMixin extends RecipeJS {
    @Unique
    public RecipeJS heatLevel(String heatLevel) {
        return this.setValue(ProcessingRecipeSchema.HEAT_REQUIREMENT, normalizeHeatRequirement(heatLevel));
    }

    @Unique
    private static String normalizeHeatRequirement(String heatLevel) {
        HeatManager heatManager = HeatManager.getInstance();
        HeatData heatData = heatManager.getHeatData(heatLevel.toUpperCase(Locale.ROOT));
        if (heatData == null) {
            throw new IllegalArgumentException("Unknown heat level: " + heatLevel);
        }

        return heatData.getCondition().serialize();
    }
}
