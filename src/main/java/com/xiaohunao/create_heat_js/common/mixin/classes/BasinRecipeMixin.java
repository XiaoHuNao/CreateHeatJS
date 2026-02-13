package com.xiaohunao.create_heat_js.common.mixin.classes;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.create_heat_js.common.HeatData;
import com.xiaohunao.create_heat_js.common.HeatManager;
import com.xiaohunao.create_heat_js.common.HeatRecipeContext;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(value = BasinRecipe.class, remap = false)
public class BasinRecipeMixin {

    @WrapOperation(
        method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/processing/recipe/HeatCondition;testBlazeBurner(Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;)Z"
        )
    )
    private static boolean createheatjs$wrapHeatCheck(HeatCondition instance, BlazeBurnerBlock.HeatLevel level, Operation<Boolean> original, BasinBlockEntity basin, Recipe<?> recipe, boolean test) {
        HeatManager heatManager = HeatManager.getInstance();
        Level world = basin.getLevel();
        if (world == null) {
            return original.call(instance, level);
        }

        BlockPos pos = basin.getBlockPos().below(1);
        BlockState state = world.getBlockState(pos);
        HeatRecipeContext context = HeatRecipeContext.of(recipe);

        boolean hasAnyProvider = false;
        for (HeatData provider : heatManager.getCandidateHeatDatas(state)) {
            if (!provider.matchesHeatSource(world, pos, state)) {
                continue;
            }
            hasAnyProvider = true;
            boolean matched = heatManager.matchesHeatRequirement(
                provider.getCondition().name(),
                instance.name(),
                context
            );
            if (matched) {
                 return true;
            }
        }

        if (hasAnyProvider) {
            return false;
        }

        return original.call(instance, level);
    }
}
