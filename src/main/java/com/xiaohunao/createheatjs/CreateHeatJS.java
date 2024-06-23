package com.xiaohunao.createheatjs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.createheatjs.event.registerHeatEvent;
import com.xiaohunao.createheatjs.util.BlockHelper;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.xiaohunao.createheatjs.KubeJSCreatePlugin.REGISTRY_HEAT;

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
//        HeatManager manager = CreateHeatJS.Manager;
//        manager.registerHeatSource(BlazeBurnerBlock.HeatLevel.SEETHING.toString(),"create:blaze_burner",((level, pos, state) -> {
//            return BasinBlockEntity.getHeatLevelOf(state) == BlazeBurnerBlock.HeatLevel.SEETHING;
//        }));
//        manager.registerHeatSource(BlazeBurnerBlock.HeatLevel.KINDLED.toString(),"create:blaze_burner",((level, pos, state) -> {
//            return BasinBlockEntity.getHeatLevelOf(state) == BlazeBurnerBlock.HeatLevel.KINDLED;
//        }));
//        manager.registerHeatSource(BlazeBurnerBlock.HeatLevel.SMOULDERING.toString(),"create:blaze_burner",((level, pos, state) -> {
//            return BasinBlockEntity.getHeatLevelOf(state) == BlazeBurnerBlock.HeatLevel.SMOULDERING;
//        }));
//        manager.registerHeatLevelPrior(BlazeBurnerBlock.HeatLevel.NONE.name(),-1);
//        manager.registerHeatLevelPrior(BlazeBurnerBlock.HeatLevel.SMOULDERING.name(),0);
//        manager.registerHeatLevelPrior(BlazeBurnerBlock.HeatLevel.FADING.name(),1);
//        manager.registerHeatLevelPrior(BlazeBurnerBlock.HeatLevel.KINDLED.name(),1);
//        manager.registerHeatLevelPrior(BlazeBurnerBlock.HeatLevel.SEETHING.name(),2);

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
