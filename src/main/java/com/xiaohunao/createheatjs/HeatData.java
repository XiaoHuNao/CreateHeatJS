package com.xiaohunao.createheatjs;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

public class HeatData {
    private final String name;
    private final int prior;
    private final int color;
    private Map<Pair<String,String>,TriPredicate<Level, BlockPos, BlockState>> heatSource = Maps.newHashMap();
    private boolean jeiTip = false;
    private HeatCondition heatCondition;
    private BlazeBurnerBlock.HeatLevel heatLevel;

    private HeatData(String name, int prior, int color) {
        this.name = name;
        this.prior = prior;
        this.color = color;
    }

    public static HeatData getHeatData(String heatLevel){
        if (heatLevel.equals(HeatCondition.NONE.toString())){
            return CreateHeatJS.heatDataMap.get(BlazeBurnerBlock.HeatLevel.NONE.toString());
        }
        if (heatLevel.equals(HeatCondition.HEATED.toString())){
            return CreateHeatJS.heatDataMap.get(BlazeBurnerBlock.HeatLevel.KINDLED.toString());
        }
        if (heatLevel.equals(HeatCondition.SUPERHEATED.toString())){
            return CreateHeatJS.heatDataMap.get(BlazeBurnerBlock.HeatLevel.SEETHING.toString());
        }
        return CreateHeatJS.heatDataMap.get(heatLevel);
    }

    public String getName() {
        return name;
    }

    public int getPrior() {
        return prior;
    }

    public int getColor() {
        return color;
    }

    public Map<Pair<String,String>, TriPredicate<Level, BlockPos, BlockState>> getHeatSource() {
        return heatSource;
    }
    public TriPredicate<Level, BlockPos, BlockState> getHeatSource(String blockState) {
        return heatSource.get(blockState);
    }

    public HeatData setHeatSource(Map<Pair<String,String>, TriPredicate<Level, BlockPos, BlockState>> blockStates) {
        this.heatSource = blockStates;
        return this;
    }
    public HeatData addHeatSource(String block,String jeiBlockStack, TriPredicate<Level, BlockPos, BlockState> predicate) {
        heatSource.put(new Pair<>(block,jeiBlockStack),predicate);
        return this;
    }
    public HeatData addHeatSource(String block,TriPredicate<Level, BlockPos, BlockState> predicate) {
        addHeatSource(block,block,predicate);
        return this;
    }
    public HeatData addHeatSource(String block,String jeiBlockStack) {
        addHeatSource(block,jeiBlockStack,(level, pos, blockState) -> true);
        return this;
    }
    public HeatData addHeatSource(String block) {
        addHeatSource(block,block);
        return this;
    }

    public boolean isJeiTip() {
        return jeiTip;
    }

    public HeatData setJeiTip(boolean jeiTip) {
        this.jeiTip = jeiTip;
        return this;
    }

    public HeatData srtJeiTip(boolean jeiTip) {
        this.jeiTip = jeiTip;
        return this;
    }

    public BlazeBurnerBlock.HeatLevel getHeatLevel() {
        return heatLevel;
    }

    public HeatData setHeatLevel(BlazeBurnerBlock.HeatLevel heatLevel) {
        this.heatLevel = heatLevel;
        return this;
    }

    public HeatCondition getHeatCondition() {
        return heatCondition;
    }

    public HeatData setHeatCondition(HeatCondition heatCondition) {
        this.heatCondition = heatCondition;
        return this;
    }
    public static class Builder {
        private final HeatData heatData;

        public Builder(String name, int prior, int color) {
            heatData = new HeatData(name,prior,color);
        }

        public HeatData register() {
            CreateHeatJS.heatDataMap.put(heatData.name,heatData);
            return heatData;
        }

        public Builder addHeatSource(String block,String jeiBlockStack, TriPredicate<Level, BlockPos, BlockState> predicate) {
            heatData.heatSource.put(new Pair<>(block,jeiBlockStack),predicate);
            return this;
        }
        public Builder addHeatSource(String block,TriPredicate<Level, BlockPos, BlockState> predicate) {
            addHeatSource(block,block,predicate);
            return this;
        }
        public Builder addHeatSource(String block) {
            addHeatSource(block,block,(level, pos, blockState) -> true);
            return this;
        }

        public Builder jeiTip() {
            heatData.setJeiTip(true);
            return this;
        }
    }
}
