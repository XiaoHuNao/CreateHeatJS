package com.xiaohunao.createheatjs.mixin;


import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


@Mixin(value = HeatCondition.class, remap = false, priority = 2000)
public abstract class HeatConditionMixin {
    @Shadow
    @Final
    @Mutable
    private static HeatCondition[] $VALUES;
    
    @Invoker("<init>")
    public static HeatCondition createheatjs$invokeInit(String internalName, int color,int internalId) {
        throw new AssertionError();
    }

    @Unique
    private static HeatCondition heatExpansion$addVariant(String internalName, int color) {
        ArrayList<HeatCondition> variants = new ArrayList<>(Arrays.asList($VALUES));
        HeatCondition heat = createheatjs$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, color);
        variants.add(heat);
        HeatConditionMixin.$VALUES = variants.toArray(new HeatCondition[0]);
        return heat;
    }


    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void createheatjs$injectExtraHeatLevels(CallbackInfo ci) {
        createHeatJS$initHeatCondition();
    }
    @Inject(method = "visualizeAsBlazeBurner", at = @At("HEAD"), cancellable = true)
    private void createheatjs$visualizeAsBlazeBurnerMixin(CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
        HeatCondition heatCondition = (HeatCondition) (Object) this;
        BlazeBurnerBlock.HeatLevel level = CreateHeatJS.heatMap.inverse().getOrDefault(heatCondition, null);
        if (level != null){
            cir.setReturnValue(level);
        }
    }
    @Inject(method = "testBlazeBurner", at = @At("HEAD"), cancellable = true)
    private void createheatjs$testBlazeBurnerMixin(BlazeBurnerBlock.HeatLevel level, CallbackInfoReturnable<Boolean> cir) {
        HeatCondition condition = CreateHeatJS.heatMap.getOrDefault(level, null);
        HeatCondition heatCondition = (HeatCondition) (Object) this;
        if (condition != null) {
            cir.setReturnValue(heatCondition == condition);
        }
    }


    @Unique
    private static void createHeatJS$initHeatCondition(){
        for (HeatCondition condition : $VALUES) {
            int color = condition.getColor();
            BlazeBurnerBlock.HeatLevel level = condition.visualizeAsBlazeBurner();
            if (condition != HeatCondition.NONE && level == BlazeBurnerBlock.HeatLevel.NONE){
                continue;
            }
            HeatData heatData = CreateHeatJS.heatDataMap.get(level.getSerializedName());
            heatData.setHeatCondition(condition).setColor(color).register();
            CreateHeatJS.heatMap.put(level,condition);
        }

        CreateHeatJS.heatDataMapByLevel.forEach((heatLevel,heatData) -> {
            HeatCondition condition = heatData.getCondition();
            if (condition == null) {
                HeatCondition heatCondition = heatExpansion$addVariant(heatData.getName().toUpperCase(Locale.ROOT),heatData.getColor());
                heatData.setHeatCondition(heatCondition).register();
                CreateHeatJS.heatMap.put(heatLevel,heatCondition);
            }
        });



        ForgeRegistries.BLOCKS.getEntries().forEach(entry -> {
            Block block = entry.getValue();
            block.getStateDefinition().getPossibleStates().forEach(blockState -> {
                if (blockState.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
                    BlazeBurnerBlock.HeatLevel level = blockState.getValue(BlazeBurnerBlock.HEAT_LEVEL);
                    List<String> heatLevel = List.of("NONE", "SMOULDERING", "FADING", "KINDLED", "SEETHING");
                    if (heatLevel.contains(level.getSerializedName().toUpperCase(Locale.ROOT))) {
                        HeatData heatData = CreateHeatJS.heatDataMapByLevel.get(level);
                        heatData.addHeatSource(block,blockState);
                    }
                }
            });
        });
    }

}