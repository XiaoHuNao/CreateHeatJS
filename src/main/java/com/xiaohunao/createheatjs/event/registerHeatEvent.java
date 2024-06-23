package com.xiaohunao.createheatjs.event;

import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

public class registerHeatEvent extends EventJS {
//    private HeatManager manager = HeatManager.getInstance();
//    public void registerHeatLevel(String heatLevel,int prior,int color) {
//        manager.registerHeatLevel(heatLevel,prior,color);
//    }
//    public void registerHeatSource(String heatLevel, String blockState, TriPredicate<Level, BlockPos, BlockState> predicate) {
//        manager.registerHeatSource(heatLevel,blockState,predicate);
//    }
//    public void registerHeatSource(String heatLevel,String blockState) {
//        manager.registerHeatSource(heatLevel,blockState);
//    }
    public HeatData.Builder registerHeat(String level, int prior, int color) {
        return new HeatData.Builder(level,prior,color);
    }
    private void addHeatSource(String name,String block, TriPredicate<Level, BlockPos, BlockState> predicate) {
        CreateHeatJS.heatDataMap.get(name).addHeatSource(block,predicate);
    }

}
