package com.xiaohunao.createheatjs.event;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

import java.util.List;


public class registerHeatEvent extends EventJS {
    public HeatData registerHeat(String name,int color) {
        return new HeatData(name, color);
    }

    public HeatData addHeatSource(BlazeBurnerBlock.HeatLevel heatLevel, Block block) {
        CreateHeatJS.heatSourceMap.put(block, heatLevel);
        return CreateHeatJS.heatDataMapByLevel.get(heatLevel).addHeatSource(block);
    }
    public HeatData addHeatSourceWithJei(BlazeBurnerBlock.HeatLevel heatLevel, Block block, BlockState jeiBlockStack) {
        HeatData heatData = addHeatSource(heatLevel, block);
        return heatData.addHeatSourceWithJei(block, jeiBlockStack);
    }

    public HeatData addHeatSource(BlazeBurnerBlock.HeatLevel heatLevel, Block block, TriPredicate<Level, BlockPos, BlockState> predicate) {
        return addHeatSource(heatLevel,block).addHeatSource(block, predicate);
    }
    public HeatData addHeatSourceWithJei(BlazeBurnerBlock.HeatLevel heatLevel, Block block,BlockState jeiBlockStack, TriPredicate<Level, BlockPos, BlockState> predicate) {
        HeatData heatData = addHeatSource(heatLevel, block);
        return heatData.addHeatSourceWithJei(block,jeiBlockStack,predicate);
    }

}
