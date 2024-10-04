package com.xiaohunao.createheatjs.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.createheatjs.CreateHeatJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinBlockEntityMixin {
    @Inject(method = "getHeatLevelOf", at = @At("HEAD"), cancellable = true)
    private static void createheatjs$getHeatLevelOf(BlockState state, CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
//        CreateHeatJS.heatDataMapByLevel.forEach((heatLevel, heatData) -> {
//            if (heatData.getHeatSourceData().containsKey(state.getBlock())) {
//                cir.setReturnValue(heatLevel);
//            }
//        });
        cir.setReturnValue(BlazeBurnerBlock.HeatLevel.KINDLED);
    }
}
