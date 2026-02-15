package com.xiaohunao.create_heat_js.common.mixin.classes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.create_heat_js.common.HeatManager;
import com.xiaohunao.create_heat_js.common.mixin.extensions.HeatConditionExpandAccessor;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;



@Mixin(value = HeatCondition.class, remap = false)
public class HeatConditionMixin implements HeatConditionExpandAccessor {
    @Shadow
    @Final
    @Mutable
    private static HeatCondition[] $VALUES;

    @Shadow
    @Final
    @Mutable
    private static Codec<HeatCondition> CODEC;

    @Invoker("<init>")
    public static HeatCondition createheatjs$init(String internalName, int color, int internalId) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void createheatjs$clinit(CallbackInfo ci) {
        CODEC = Codec.STRING.flatXmap(
            name -> {
                if (name == null) {
                    return DataResult.error(() -> "HeatCondition name cannot be null");
                }
                HeatCondition[] allValues = $VALUES;
                for (HeatCondition value : allValues) {
                    if (value.name().equals(name)) {
                        return DataResult.success(value);
                    }
                }
                String upperName = name.toUpperCase();
                for (HeatCondition value : allValues) {
                    if (value.name().equals(upperName)) {
                        return DataResult.success(value);
                    }
                }
                return DataResult.error(() -> "Unknown HeatCondition: " + name);
            },
            value -> {
                if (value == null) {
                    return DataResult.error(() -> "HeatCondition cannot be null");
                }
                return DataResult.success(value.name());
            }
        );
    }

    @Unique
    public HeatCondition chj$addEnumValue(String internalName, int color) {
        ArrayList<HeatCondition> heatConditions = new ArrayList<>(Arrays.asList($VALUES));
        HeatCondition heat = createheatjs$init(internalName, heatConditions.get(heatConditions.size() - 1).ordinal() + 1, color);
        heatConditions.add(heat);
        HeatConditionMixin.$VALUES = heatConditions.toArray(new HeatCondition[0]);
        return heat;
    }

    @Override
    public void chj$removeEnumValue(String name) {
        ArrayList<HeatCondition> heatConditions = new ArrayList<>(Arrays.asList($VALUES));
        heatConditions.removeIf(heatCondition -> heatCondition.name().equals(name));
        HeatConditionMixin.$VALUES = heatConditions.toArray(new HeatCondition[0]);
    }

    @Inject(method = "testBlazeBurner", at = @At("HEAD"), cancellable = true)
    public void testBlazeBurner(BlazeBurnerBlock.HeatLevel level, CallbackInfoReturnable<Boolean> cir){
        if (!HeatManager.getInstance().hasHeatLevel(level)){
            return;
        }

        HeatCondition condition = (HeatCondition) (Object) this;
        HeatCondition heatCondition = HeatManager.getInstance().getHeatCondition(level);
        if (heatCondition == null) {
            return;
        }

        boolean matched = HeatManager.getInstance()
            .matchesHeatRequirement(heatCondition.name(), condition.name(), null);
        cir.setReturnValue(matched);
    }

    @Inject(method = "visualizeAsBlazeBurner",at = @At("HEAD"), cancellable = true)
    public void visualizeAsBlazeBurner(CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir){
        HeatCondition condition = (HeatCondition) (Object) this;
        if (!HeatManager.INSTANCE.hasHeatCondition(condition)){
            return;
        }

        cir.setReturnValue(HeatManager.getInstance().getHeatLevel(condition));
    }



}
