package com.xiaohunao.createheatjs.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.xiaohunao.createheatjs.CreateHeatJS;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = HeatLevel.class, remap = false)
public abstract class HeatLevelMixin {
    @Shadow
    @Final
    @Mutable
    private static HeatLevel[] $VALUES;

    @Invoker("<init>")
    public static HeatLevel heatExpansion$invokeInit(String internalName, int internalId) {
        throw new AssertionError();
    }
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void clinit(CallbackInfo ci) {
        CreateHeatJS.heatDataMap.forEach((condition, heatData) -> {
            HeatLevel heatLevel = heatExpansion$addVariant(condition);
            heatData.setHeatLevel(heatLevel);
        });
    }


    private static HeatLevel heatExpansion$addVariant(String internalName) {
        ArrayList<HeatLevel> variants = new ArrayList<>(Arrays.asList(HeatLevelMixin.$VALUES));
        HeatLevel heat = heatExpansion$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1);
        variants.add(heat);
        HeatLevelMixin.$VALUES = variants.toArray(new HeatLevel[0]);
        return heat;
    }

}