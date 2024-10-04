package com.xiaohunao.createheatjs.event;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

public class registerHeatEvent extends EventJS {
    public HeatData registerHeat(String name,int color) {
        return new HeatData(name, color);
    }

    public HeatData addHeatSource(BlazeBurnerBlock.HeatLevel heatLevel, Block block) {
        return CreateHeatJS.heatDataMapByLevel.get(heatLevel).addHeatSource(block);
    }
    public HeatData addHeatSource(BlazeBurnerBlock.HeatLevel heatLevel, Block block, TriPredicate<Level, BlockPos, BlockState> predicate) {
        return CreateHeatJS.heatDataMapByLevel.get(heatLevel).addHeatSource(block, predicate);
    }
//    public HeatData addHeatSource(BlazeBurnerBlock.HeatLevel heatLevel, TagKey<Block> tagKey) {
//
//    }
}
