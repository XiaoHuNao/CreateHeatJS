package com.xiaohunao.create_heat_js;

import com.xiaohunao.create_heat_js.common.HeatManager;
import com.xiaohunao.create_heat_js.common.HeatProvider;
import com.xiaohunao.create_heat_js.common.event.CommonEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CreateHeatJS.MOD_ID)
public class CreateHeatJS {
    public static final String MOD_ID = "create_heat_js";

    public static final HeatProvider  heatProvider = new HeatProvider();

    public CreateHeatJS(IEventBus modEventBus) {
        modEventBus.addListener(HeatManager.getInstance()::onFMLCommonSetup);
        NeoForge.EVENT_BUS.addListener(CommonEvents::onAddReloadListener);
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static String asDescriptionId(String path) {
        return MOD_ID + "." + path;
    }

    public static <T> ResourceKey<T> asResourceKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, asResource(path));
    }

    public static <T> ResourceKey<Registry<T>> asResourceKey(String path) {
        return ResourceKey.createRegistryKey(asResource(path));
    }
}
