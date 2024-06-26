package com.xiaohunao.createheatjs;


import com.google.common.collect.Maps;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;



@Mod(CreateHeatJS.MOD_ID)
public class CreateHeatJS {
    public static final String MOD_ID = "create_heat_js";
//    public static final HeatManager Manager = HeatManager.getInstance();
    public static final Map<String,HeatData> heatDataMap = Maps.newLinkedHashMap();
    public CreateHeatJS() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onFMLCommonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void onFMLCommonSetup(FMLCommonSetupEvent event) {
        CreateHeatJS.heatDataMap.put(BlazeBurnerBlock.HeatLevel.NONE.toString(),new HeatData.Builder(BlazeBurnerBlock.HeatLevel.NONE.toString(),0,0xff0000)
                .addHeatSource("*")
                .register());
        CreateHeatJS.heatDataMap.put(BlazeBurnerBlock.HeatLevel.SMOULDERING.toString(),new HeatData.Builder(BlazeBurnerBlock.HeatLevel.SMOULDERING.toString(),1,0xff0000)
                .addHeatSource("create:passive_boiler_heaters")
                .register());
        CreateHeatJS.heatDataMap.put(BlazeBurnerBlock.HeatLevel.KINDLED.toString(),new HeatData.Builder(BlazeBurnerBlock.HeatLevel.KINDLED.toString(),2,0xff0000)
                .addHeatSource("create:blaze_burner",((level, pos, state) -> {
                    return BasinBlockEntity.getHeatLevelOf(state) == BlazeBurnerBlock.HeatLevel.KINDLED;
                }))
                .register());
        CreateHeatJS.heatDataMap.put(BlazeBurnerBlock.HeatLevel.SEETHING.toString(),new HeatData.Builder(BlazeBurnerBlock.HeatLevel.SEETHING.toString(),3,0xff0000)
                .addHeatSource("create:blaze_burner",((level, pos, state) -> {
                    return BasinBlockEntity.getHeatLevelOf(state) == BlazeBurnerBlock.HeatLevel.SEETHING;
                }))
                .register());
    }

}
