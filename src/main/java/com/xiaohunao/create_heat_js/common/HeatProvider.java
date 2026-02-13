package com.xiaohunao.create_heat_js.common;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.create_heat_js.common.mixin.extensions.HeatConditionExpandAccessor;

import java.util.ArrayList;

public class HeatProvider {
    public HeatProvider() {
        HeatManager heatManager = HeatManager.getInstance();

        heatManager.registerHeatData(new HeatData("NONE", 0xffffff, BlazeBurnerBlock.HeatLevel.NONE, HeatCondition.NONE, new ArrayList<>()));
        heatManager.registerHeatData(new HeatData("SMOULDERING", 0x5C93E8, BlazeBurnerBlock.HeatLevel.SMOULDERING, HeatConditionExpandAccessor.SMOULDERING, new ArrayList<>()));
        heatManager.registerHeatData(new HeatData("FADING", 0x3273C2, BlazeBurnerBlock.HeatLevel.FADING, HeatConditionExpandAccessor.FADING, new ArrayList<>()));
        heatManager.registerHeatData(new HeatData("HEATED", 0xE88300, BlazeBurnerBlock.HeatLevel.KINDLED, HeatCondition.HEATED, new ArrayList<>()));
        heatManager.registerHeatData(
            new HeatData("SUPERHEATED", 0x5C93E8, BlazeBurnerBlock.HeatLevel.SEETHING, HeatCondition.SUPERHEATED, new ArrayList<>())
                .satisfies(HeatCondition.HEATED.name())
        );
    }

    public void addDefaultHeatSources() {
        HeatManager heatManager = HeatManager.getInstance();
        heatManager.getAllHeatLevels().forEach((block, heatLevel) -> {
            heatLevel.forEach((blockState, level) -> {
                HeatData heatData = heatManager.getHeatData(level);
                if (heatData != null) {
                    heatData.addHeatSource(blockState);
                    heatManager.reindexHeatData(heatData);
                }
            });
        });
    }

}
