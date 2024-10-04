package com.xiaohunao.createheatjs;

import com.xiaohunao.createheatjs.event.registerHeatEvent;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraft.world.level.block.state.BlockState;

public class KubeJSCreatePlugin extends KubeJSPlugin {
    public final static EventGroup GROUP = EventGroup.of("CreateHeatJS");
    public final static EventHandler REGISTRY_HEAT = GROUP.startup("registerHeatEvent", () -> registerHeatEvent.class);

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("BlockState", BlockState.class);
    }

    @Override
    public void registerEvents() {
        GROUP.register();
    }

    @Override
    public void initStartup() {
        REGISTRY_HEAT.post(new registerHeatEvent());
    }

    @Override
    public void registerTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
        typeWrappers.register(BlockState.class, (TypeWrapperFactory.Simple<BlockState>) o -> {
            if (o instanceof BlockState) {
                return (BlockState) o;
            }
            if (o instanceof String) {
                return UtilsJS.parseBlockState((String) o);
            }
            return null;
        });
    }
}