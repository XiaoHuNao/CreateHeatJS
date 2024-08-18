package com.xiaohunao.createheatjs.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BasinRecipe.class, remap = false)
public class BasinRecipeMixin {

    @Inject(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z"
            , at = @At(value = "INVOKE"
            , target = "Lcom/simibubi/create/content/processing/recipe/HeatCondition;testBlazeBurner(Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;)Z"
            , shift = At.Shift.AFTER)
            , cancellable = true
    )
    private static void createheatjs$checkForCustomHeatLevels(BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir) {
        Level level = basin.getLevel();
        if (level != null) {
            BlockPos blockPos = basin.getBlockPos().below(1);
            BlockState blockState = level.getBlockState(blockPos);

            if (recipe instanceof BasinRecipe basinRecipe) {
                HeatCondition recipeHeatCondition = basinRecipe.getRequiredHeat();
                BlazeBurnerBlock.HeatLevel recipeHeatHeatLevel = CreateHeatJS.heatMap.inverse().get(recipeHeatCondition);
                HeatData recipeHeatData = CreateHeatJS.heatDataMapByLevel.get(recipeHeatHeatLevel);
                recipeHeatData.getHeatSourceData().forEach((block, heatSourceData) -> {
                    if (heatSourceData.getStates().contains(blockState)) {
                        return;
                    }
                    if (heatSourceData.getPredicate() != null && heatSourceData.getPredicate().test(level, blockPos, blockState)) {
                        return;
                    }
                });
            }
        }
    }
}