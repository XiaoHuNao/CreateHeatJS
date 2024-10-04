package com.xiaohunao.createheatjs.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(BasinBlockEntity.class)
public class BasinBlockEntityMixin {

    @Inject(method = "getHeatLevelOf", at = @At("HEAD"), cancellable = true,remap = false)
    private static void createheatjs$getHeatLevelOfMixin(BlockState state, CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
        Collection<BlazeBurnerBlock.HeatLevel> heatLevels = CreateHeatJS.heatSourceMap.get(state.getBlock());
        HeatData heatData = null;
        for (BlazeBurnerBlock.HeatLevel heatLevel : heatLevels) {
            HeatData heatData1 = CreateHeatJS.heatDataMapByLevel.get(heatLevel);
            if (heatData == null) {
                heatData = heatData1;
            }
            if (heatData.getPriority() < heatData1.getPriority()){
                heatData = heatData1;
            }
        }

        if (heatData == null) {
            return;
        }
        cir.setReturnValue(heatData.getHeatLevel());
    }
}
