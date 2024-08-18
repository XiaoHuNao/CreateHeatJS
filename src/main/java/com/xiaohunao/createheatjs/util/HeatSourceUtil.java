package com.xiaohunao.createheatjs.util;

import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.AllBlocks;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;

public class HeatSourceUtil {
    public static void manualInitHeatSource(String name, HeatData heatData) {
        if (CreateHeatJS.LOW_ACTIVE && name.equals("low")) {
            heatData.removeHeatSource(AllBlocks.BLAZE_BURNER.get());
            if (CreateHeatJS.CCA_ACTIVE) {
                heatData.removeHeatSource(CABlocks.LIQUID_BLAZE_BURNER.get());
            }
            CreateHeatJS.heatDataMapByLevel.put(heatData.getHeatLevel(), heatData);
        }
    }
}
