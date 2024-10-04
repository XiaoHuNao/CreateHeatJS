package com.xiaohunao.createheatjs.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

import static com.xiaohunao.createheatjs.CreateHeatJS.heatSourceMap;

@Mixin(value = BlazeBurnerBlock.HeatLevel.class, remap = false ,priority = 2000)
public abstract class HeatLevelMixin {
    @Shadow
    @Final
    @Mutable
    private static BlazeBurnerBlock.HeatLevel[] $VALUES;

    @Invoker("<init>")
    public static BlazeBurnerBlock.HeatLevel createheatjs$invokeInit(String internalName, int internalId) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void createheatjs$injectExtraHeatLevels(CallbackInfo ci) {
        createHeatJS$initHeatLevel();
    }

    private static BlazeBurnerBlock.HeatLevel heatExpansion$addVariant(String internalName) {
        ArrayList<BlazeBurnerBlock.HeatLevel> variants = new ArrayList<>(Arrays.asList(HeatLevelMixin.$VALUES));
        BlazeBurnerBlock.HeatLevel heat = createheatjs$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1);
        variants.add(heat);
        HeatLevelMixin.$VALUES = variants.toArray(new BlazeBurnerBlock.HeatLevel[0]);
        return heat;
    }

    @Unique
    private static void createHeatJS$initHeatLevel(){
        // Add custom heat levels
        CreateHeatJS.heatDataMap.forEach((name,heatData) -> {
            BlazeBurnerBlock.HeatLevel level = heatExpansion$addVariant(name);
            heatData.setHeatLevel(level);
            CreateHeatJS.heatDataMapByLevel.put(level,heatData);
        });

        // Add default heat levels
        for (BlazeBurnerBlock.HeatLevel level : $VALUES) {
            if (CreateHeatJS.heatDataMapByLevel.containsKey(level)) {
                continue;
            }
            HeatData heatData = new HeatData(level.getSerializedName());
            heatData.setHeatLevel(level).register();
            CreateHeatJS.heatDataMapByLevel.put(level,heatData);
        }
    }


}
