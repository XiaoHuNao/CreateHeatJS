package com.xiaohunao.createheatjs.util;

import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlock;

import static com.xiaohunao.createheatjs.CreateHeatJS.*;

public class HeatSourceUtil {
    public static void manualInitHeatCondition(BlockState blockState, Block block) {
        if (blockState.hasProperty(BasicBurnerBlock.HEAT_LEVEL)) {
            BlazeBurnerBlock.HeatLevel level = blockState.getValue(BasicBurnerBlock.HEAT_LEVEL);
            HeatData heatData = CreateHeatJS.heatDataMapByLevel.get(level);
            heatData.addHeatSource(block);
        }
    }

    public static void manualInitHeatSource(String name, HeatData heatData) {
        if (CreateHeatJS.LOW_ACTIVE && name.equals("low")) {
            heatData.removeHeatSource(AllBlocks.BLAZE_BURNER.get());
            if (CreateHeatJS.CCA_ACTIVE) {
                heatData.removeHeatSource(CABlocks.LIQUID_BLAZE_BURNER.get());
            }
        }
        CreateHeatJS.heatDataMapByLevel.put(heatData.getHeatLevel(), heatData);
    }
    public static void manualInitHeatPriority(String name, HeatData heatData) {
        if (heatData.getHeatLevel() == BlazeBurnerBlock.HeatLevel.NONE)
            heatData.setPriority(0);
        if (heatData.getHeatLevel() == BlazeBurnerBlock.HeatLevel.SMOULDERING)
            heatData.setPriority(1);
        if (heatData.getHeatLevel() == BlazeBurnerBlock.HeatLevel.FADING)
            heatData.setPriority(2);
        if (heatData.getHeatLevel() == BlazeBurnerBlock.HeatLevel.KINDLED)
            heatData.setPriority(3);
        if (heatData.getHeatLevel() == BlazeBurnerBlock.HeatLevel.SEETHING)
            heatData.setPriority(4);
    }
    public static void manualInitCreateHeatLevelPriority() {

    }

    public static void debugHeatSource() {
        CreateHeatJS.log.debug("Initialized heat source");
        heatMap.forEach((heatLevel, heatCondition) -> {
            CreateHeatJS.log.debug("{} -> {}", heatLevel.getSerializedName(), heatCondition.serialize());
        });
        heatDataMap.forEach((name, heatData) -> {
            CreateHeatJS.log.debug("{} -> {}", heatData.getHeatLevel().getSerializedName(), heatData.toString());
        });
        heatSourceMap.forEach((block, heatLevel) -> {
            CreateHeatJS.log.debug("{} -> {}", block.getDescriptionId(), heatLevel.getSerializedName());
        });
        heatDataMapByLevel.forEach((heatLevel, heatData) -> {
            CreateHeatJS.log.debug("{} -> {}", heatLevel.getSerializedName(), heatData.toString());
        });
    }
}
