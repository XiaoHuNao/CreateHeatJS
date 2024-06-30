package com.xiaohunao.createheatjs.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = HeatCondition.class, remap = false)
public abstract class HeatConditionMixin {
    @Shadow
    @Final
    @Mutable
    private static HeatCondition[] $VALUES;

    @Invoker("<init>")
    public static HeatCondition createheatjs$invokeInit(String internalName, int internalId, int color) {
        throw new AssertionError();
    }
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void createheatjs$injectExtraHeatLevels(CallbackInfo ci) {
        if (CreateHeatJS.heatDataMap.isEmpty()) return;

        int nextIndex = $VALUES.length;
        HeatCondition[] newValues = new HeatCondition[$VALUES.length + CreateHeatJS.heatDataMap.size()];
        System.arraycopy($VALUES, 0, newValues, 0, $VALUES.length);

        for (Map.Entry<String, HeatData> stringHeatDataEntry : CreateHeatJS.heatDataMap.entrySet()) {
            HeatData heatData = stringHeatDataEntry.getValue();
            int index = nextIndex++;
            HeatCondition heat = createheatjs$invokeInit(stringHeatDataEntry.getKey(), index, heatData.getColor());
            heatData.setHeatCondition(heat);
            newValues[index] = heat;
        }
        $VALUES = newValues;
    }

    @Inject(method = "testBlazeBurner", at = @At("HEAD"), cancellable = true)
    private void createheatjs$testBlazeBurnerMixin(BlazeBurnerBlock.HeatLevel level, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}