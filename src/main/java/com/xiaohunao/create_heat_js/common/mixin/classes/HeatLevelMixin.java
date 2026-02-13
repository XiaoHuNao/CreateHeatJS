package com.xiaohunao.create_heat_js.common.mixin.classes;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.xiaohunao.create_heat_js.common.mixin.extensions.HeatLevelExpandAccessor;
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

@Mixin(value = BlazeBurnerBlock.HeatLevel.class, remap = false)
public class HeatLevelMixin implements HeatLevelExpandAccessor {
    @Shadow
    @Final
    @Mutable
    private static BlazeBurnerBlock.HeatLevel[] $VALUES;

    @Invoker("<init>")
    public static BlazeBurnerBlock.HeatLevel createheatjs$init(String internalName, int internalId) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void createheatjs$clinit(CallbackInfo ci) {
        // System.out.println("CreateHeatJS: Initializing Heat Levels");
    }

    @Override
    public BlazeBurnerBlock.HeatLevel chj$addEnumValue(String name) {
        ArrayList<BlazeBurnerBlock.HeatLevel> variants = new ArrayList<>(Arrays.asList(HeatLevelMixin.$VALUES));
        BlazeBurnerBlock.HeatLevel heat = createheatjs$init(name, variants.get(variants.size() - 1).ordinal() + 1);
        variants.add(heat);
        HeatLevelMixin.$VALUES = variants.toArray(new BlazeBurnerBlock.HeatLevel[0]);
        return heat;
    }

    @Override
    public void chj$removeEnumValue(String name) {
        ArrayList<BlazeBurnerBlock.HeatLevel> variants = new ArrayList<>(Arrays.asList($VALUES));
        variants.removeIf(heatLevel -> heatLevel.name().equals(name));
        HeatLevelMixin.$VALUES = variants.toArray(new BlazeBurnerBlock.HeatLevel[0]);
    }

    private static BlazeBurnerBlock.HeatLevel chj$create(String name) {
        return ((HeatLevelExpandAccessor) (Object) BlazeBurnerBlock.HeatLevel.NONE).chj$addEnumValue(name);
    }

}
