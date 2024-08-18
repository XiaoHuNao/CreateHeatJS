package com.xiaohunao.createheatjs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HeatData {
    private final String name;
    private int color = 0xff0000;
    private BlazeBurnerBlock.HeatLevel heatLevel;
    private HeatCondition condition;
    private Map<Block,HeatSourceData> heatSourceData = Maps.newHashMap();


    public HeatData(String name) {
        this.name = name;
    }

    public HeatData(String name, int color) {
        this.name = name;
        this.color = color;
    }
    public HeatData removeHeatSource(Block block) {
        this.heatSourceData.remove(block);
        return this;
    }
    public HeatData addHeatSource(Block block) {
        if (this.heatSourceData.containsKey(block)) {
            this.heatSourceData.get(block);
        }else {
            this.heatSourceData.put(block, new HeatSourceData());
        }
        return this;
    }
    public HeatData addHeatSource(Block block, BlockState... states) {
        if (this.heatSourceData.containsKey(block)) {
            this.heatSourceData.get(block).addState(states);
        }else {
            this.heatSourceData.put(block, new HeatSourceData().setStates(states));
        }
        return this;
    }
    public HeatData addHeatSource(Block block, TriPredicate<Level, BlockPos, BlockState> predicate) {
        if (this.heatSourceData.containsKey(block)) {
            this.heatSourceData.get(block).setPredicate(predicate);
        }else {
            this.heatSourceData.put(block, new HeatSourceData().setPredicate(predicate));
        }
        return this;
    }

    public Map<Block, HeatSourceData> getHeatSourceData() {
        return heatSourceData;
    }

    public List<ItemStack> getHeatSourceStacks() {
        return heatSourceData.keySet().stream().map(block -> block.asItem().getDefaultInstance()).toList();
    }
    public List<Block> getHeatSourceBlocks() {
        return Lists.newArrayList(heatSourceData.keySet());
    }
    public List<BlockState> getHeatSourceStates(Block block) {
        return heatSourceData.get(block).getStates();
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public HeatData setColor(int color) {
        this.color = color;
        return this;
    }

    public void register() {
        CreateHeatJS.heatDataMap.put(name.toLowerCase(Locale.ROOT),this);
    }

    public HeatData setHeatLevel(BlazeBurnerBlock.HeatLevel level) {
        this.heatLevel = level;
        return this;
    }

    public BlazeBurnerBlock.HeatLevel getHeatLevel() {
        return heatLevel;
    }

    public HeatData setHeatCondition(HeatCondition condition) {
        this.condition = condition;
        return this;
    }

    public HeatCondition getCondition() {
        return condition;
    }

    public static class HeatSourceData {
        private List<BlockState> states = Lists.newArrayList();
        private TriPredicate<Level, BlockPos, BlockState> predicate;

        public List<BlockState> getStates() {
            return states;
        }

        public TriPredicate<Level, BlockPos, BlockState> getPredicate() {
            return predicate;
        }

        public HeatSourceData setStates(List<BlockState> states) {
            this.states = states;
            return this;
        }
        public HeatSourceData setStates(BlockState... states) {
            this.states = Lists.newArrayList(states);
            return this;
        }
        public HeatSourceData addState(BlockState state) {
            this.states.add(state);
            return this;
        }
        public void addState(BlockState... states) {
            this.states.addAll(Lists.newArrayList(states));
        }
        public HeatSourceData setPredicate(TriPredicate<Level, BlockPos, BlockState> predicate) {
            this.predicate = predicate;
            return this;
        }
    }
}
