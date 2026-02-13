package com.xiaohunao.create_heat_js;

import com.xiaohunao.create_heat_js.common.event.RegisterHeatEvent;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;


public class HeatJSKubeJSPlugin extends KubeJSPlugin {
    public final static EventGroup GROUP = EventGroup.of("CreateHeatJS");
    public final static EventHandler REGISTRY_HEAT = GROUP.server("registerHeatEvent", () -> RegisterHeatEvent.class);

    @Override
    public void registerEvents() {
        GROUP.register();
    }
}
