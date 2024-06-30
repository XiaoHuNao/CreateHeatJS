package com.xiaohunao.createheatjs.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
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

import java.util.Map;

@Mixin(value = HeatLevel.class, remap = false)
public abstract class HeatLevelMixin {
    @Shadow
    @Final
    @Mutable
    private static HeatLevel[] $VALUES;

    @Invoker("<init>")
    public static HeatLevel createheatjs$invokeInit(String internalName, int internalId) {
        throw new AssertionError();
    }
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void createheatjs$injectExtraHeatLevels(CallbackInfo ci) {
        if (CreateHeatJS.heatDataMap.isEmpty()) return;

        int nextIndex = $VALUES.length;
        HeatLevel[] newValues = new HeatLevel[$VALUES.length + CreateHeatJS.heatDataMap.size()];
        System.arraycopy($VALUES, 0, newValues, 0, $VALUES.length);

        for (Map.Entry<String, HeatData> stringHeatDataEntry : CreateHeatJS.heatDataMap.entrySet()) {
            int index = nextIndex++;
            HeatLevel heat = createheatjs$invokeInit(stringHeatDataEntry.getKey(), index);
            stringHeatDataEntry.getValue().setHeatLevel(heat);
            newValues[index] = heat;
        }
        $VALUES = newValues;
    }

}