package com.xiaohunao.createheatjs;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.xiaohunao.createheatjs.event.registerHeatEvent;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraft.world.level.block.state.BlockState;

public class KubeJSCreatePlugin extends KubeJSPlugin {
    public final static EventGroup GROUP = EventGroup.of("CreateHeatJS");
    public final static EventHandler REGISTRY_HEAT = GROUP.startup("registerHeatEvent", () -> registerHeatEvent.class);

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("HeatLevel", BlazeBurnerBlock.HeatLevel.class);
        event.add("BlockState", BlockState.class);
    }

    @Override
    public void initStartup() {
        REGISTRY_HEAT.post(new registerHeatEvent());
    }

    @Override
    public void registerEvents() {
        GROUP.register();
    }
}