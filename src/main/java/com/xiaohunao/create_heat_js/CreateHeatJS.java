package com.xiaohunao.create_heat_js;

import com.xiaohunao.create_heat_js.common.HeatManager;
import com.xiaohunao.create_heat_js.common.HeatProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateHeatJS.MOD_ID)
public class CreateHeatJS {
    public static final String MOD_ID = "create_heat_js";

    public static final HeatProvider  heatProvider = new HeatProvider();

    public CreateHeatJS() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(HeatManager.getInstance()::onFMLCommonSetup);
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
