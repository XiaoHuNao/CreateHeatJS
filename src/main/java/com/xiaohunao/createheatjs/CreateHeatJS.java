package com.xiaohunao.createheatjs;

import com.google.common.collect.*;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(CreateHeatJS.MOD_ID)
public class CreateHeatJS {
    public static final String MOD_ID = "create_heat_js";
    public static final Logger log = LogManager.getLogger(CreateHeatJS.class);


    public static final BiMap<BlazeBurnerBlock.HeatLevel,HeatCondition> heatMap = HashBiMap.create();
    public static final Map<String,HeatData> heatDataMap = Maps.newLinkedHashMap();
    public static final BiMap<BlazeBurnerBlock.HeatLevel,HeatData> heatDataMapByLevel = HashBiMap.create();
    public static final Multimap<Block,BlazeBurnerBlock.HeatLevel> heatSourceMap = ArrayListMultimap.create();
    public static boolean LOW_ACTIVE;
    public static boolean CCA_ACTIVE;

    public CreateHeatJS() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onFMLCommonSetup);
        MinecraftForge.EVENT_BUS.register(this);


        CCA_ACTIVE = ModList.get().isLoaded("createaddition");
        LOW_ACTIVE = ModList.get().isLoaded("createlowheated");
    }

    @SubscribeEvent
    public void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }


}
