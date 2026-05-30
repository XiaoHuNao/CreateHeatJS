package com.xiaohunao.create_heat_js.common.event;

import java.util.function.Consumer;
import java.util.function.Function;

import com.xiaohunao.create_heat_js.common.HeatData;
import com.xiaohunao.create_heat_js.common.HeatManager;

import dev.latvian.mods.kubejs.event.KubeEvent;


public class RegisterHeatEvent implements KubeEvent {


    public void registerHeat(String heatName, Consumer<HeatData.Builder> builder) {
        HeatData.Builder buildered = HeatData.builder(heatName);
        if (builder != null) {
            builder.accept(buildered);
        }
        HeatData heatData = buildered.build();
        HeatManager heatManager = HeatManager.getInstance();
        heatManager.registerHeatData(heatData);
    }

    public void modifyHeat(String heatName, Consumer<HeatData> data) {
        HeatManager heatManager = HeatManager.getInstance();
        HeatData heatData = heatManager.getHeatData(heatName);
        if (heatData == null) {
            return;
        }

        if (data != null) {
            data.accept(heatData);
        }
        heatManager.reindexHeatData(heatData);
    }
}
