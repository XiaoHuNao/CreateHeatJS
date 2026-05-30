package com.xiaohunao.create_heat_js;

import java.util.List;
import java.util.Locale;

import com.xiaohunao.create_heat_js.common.HeatData;
import com.xiaohunao.create_heat_js.common.HeatManager;
import com.xiaohunao.create_heat_js.common.event.RegisterHeatEvent;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.function.CustomRecipeSchemaFunctionRegistry;
import dev.latvian.mods.kubejs.recipe.schema.function.ResolvedRecipeSchemaFunction;
import net.minecraft.resources.ResourceLocation;


public class HeatJSKubeJSPlugin implements KubeJSPlugin {
    public static final EventGroup GROUP = EventGroup.of("CreateHeatJS");
    public static final EventHandler REGISTRY_HEAT = GROUP.startup("registerHeatEvent", () -> RegisterHeatEvent.class);
    public static final ResourceLocation HEAT_LEVEL_SCHEMA_FUNCTION_ID = CreateHeatJS.asResource("heat_level");

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(GROUP);
    }

    @Override
    public void afterInit() {
        HeatJSKubeJSPlugin.REGISTRY_HEAT.post(new RegisterHeatEvent());
    }

    @Override
    public void registerCustomRecipeSchemaFunctions(CustomRecipeSchemaFunctionRegistry registry) {
        registry.register(HEAT_LEVEL_SCHEMA_FUNCTION_ID, HeatLevelSchemaFunction.INSTANCE);
    }

    private static final class HeatLevelSchemaFunction implements ResolvedRecipeSchemaFunction {
        private static final HeatLevelSchemaFunction INSTANCE = new HeatLevelSchemaFunction();

        @Override
        public List<RecipeComponent<?>> arguments() {
            return List.of(StringComponent.STRING.instance());
        }

        @Override
        public void execute(RecipeScriptContext cx, List<Object> args) {
            String heatLevelName = args.isEmpty() || args.getFirst() == null ? null : String.valueOf(args.getFirst()).toUpperCase(Locale.ROOT);
            HeatData heatData = HeatManager.getInstance().getHeatData(heatLevelName);

            if (heatData == null) {
                throw new IllegalArgumentException("Unknown heat level: " + heatLevelName);
            }

            String conditionName = heatData.getCondition() == null ? null : heatData.getCondition().name().toLowerCase(Locale.ROOT);
            cx.recipe().set(cx.cx(), "heat_requirement", conditionName == null ? "none" : conditionName);
        }
    }
}
