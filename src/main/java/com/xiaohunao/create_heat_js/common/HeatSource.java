package com.xiaohunao.create_heat_js.common;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;


public class HeatSource {
    @Nullable
    private Component infoTooltip;

    @Nullable
    public Component getInfoTooltip() {
        return infoTooltip;
    }

    public HeatSource withInfoTooltip(@Nullable Component infoTooltip) {
        this.infoTooltip = infoTooltip;
        return this;
    }

    public boolean hasInfoTooltip() {
        return infoTooltip != null;
    }

    public boolean matches(Level level, BlockPos pos, BlockState state) {
        return false;
    }

    public void forEachDisplayBlock(Consumer<Block> consumer) {
    }

    public static class BlockHeatSource extends HeatSource {
        public final Block block;

        private BlockHeatSource(Block block) {
            this.block = block;
        }

        public static BlockHeatSource of(Block block) {
            return new BlockHeatSource(block);
        }

        public static BlockHeatSource of(Block block, @Nullable Component infoTooltip) {
            BlockHeatSource source = new BlockHeatSource(block);
            source.withInfoTooltip(infoTooltip);
            return source;
        }

        @Override
        public boolean matches(Level level, BlockPos pos, BlockState state) {
            return block != null && state != null && state.getBlock() == block;
        }

        @Override
        public void forEachDisplayBlock(Consumer<Block> consumer) {
            if (block != null) {
                consumer.accept(block);
            }
        }
    }

    public static class BlockStateHeatSource extends HeatSource {
        public final List<BlockState> blockStates;

        private BlockStateHeatSource(List<BlockState> blockStates) {
            this.blockStates = blockStates;
        }

        public static BlockStateHeatSource of(BlockState... blockStates) {
            return new BlockStateHeatSource(List.of(blockStates));
        }

        public static BlockStateHeatSource of(@Nullable Component infoTooltip, BlockState... blockStates) {
            BlockStateHeatSource source = new BlockStateHeatSource(List.of(blockStates));
            source.withInfoTooltip(infoTooltip);
            return source;
        }

        @Override
        public boolean matches(Level level, BlockPos pos, BlockState state) {
            return blockStates != null && state != null && blockStates.contains(state);
        }

        @Override
        public void forEachDisplayBlock(Consumer<Block> consumer) {
            if (blockStates == null) {
                return;
            }
            for (BlockState blockState : blockStates) {
                if (blockState == null) {
                    continue;
                }
                Block block = blockState.getBlock();
                consumer.accept(block);
            }
        }

    }

    public static class BlockTagHeatSource extends HeatSource {
        public final TagKey<Block> tag;

        private BlockTagHeatSource(TagKey<Block> tag) {
            this.tag = tag;
        }

        public static BlockTagHeatSource of(TagKey<Block> tag) {
            return new BlockTagHeatSource(tag);
        }

        public static BlockTagHeatSource of(TagKey<Block> tag, @Nullable Component infoTooltip) {
            BlockTagHeatSource source = new BlockTagHeatSource(tag);
            source.withInfoTooltip(infoTooltip);
            return source;
        }

        @Override
        public boolean matches(Level level, BlockPos pos, BlockState state) {
            return tag != null && state != null && state.is(tag);
        }

        @Override
        public void forEachDisplayBlock(Consumer<Block> consumer) {
            if (tag == null) {
                return;
            }
            for (Block block : ForgeRegistries.BLOCKS.getValues()) {
                if (block != null && block.builtInRegistryHolder().is(tag)) {
                    consumer.accept(block);
                }
            }
        }
    }

    public static class FluidHeatSource extends HeatSource {
        public final Fluid fluid;

        private FluidHeatSource(Fluid fluid) {
            this.fluid = fluid;
        }

        public static FluidHeatSource of(Fluid fluid) {
            return new FluidHeatSource(fluid);
        }

        public static FluidHeatSource of(Fluid fluid, @Nullable Component infoTooltip) {
            FluidHeatSource source = new FluidHeatSource(fluid);
            source.withInfoTooltip(infoTooltip);
            return source;
        }

        @Override
        public boolean matches(Level level, BlockPos pos, BlockState state) {
            return fluid != null && state != null && state.getFluidState().getType() == fluid;
        }

        @Override
        public void forEachDisplayBlock(Consumer<Block> consumer) {
            if (fluid == null) {
                return;
            }
            BlockState legacy = fluid.defaultFluidState().createLegacyBlock();
            consumer.accept(legacy.getBlock());
        }
    }

    public static class FluidTagHeatSource extends HeatSource {
        public final TagKey<Fluid> tag;

        private FluidTagHeatSource(TagKey<Fluid> tag) {
            this.tag = tag;
        }

        public static FluidTagHeatSource of(TagKey<Fluid> tag) {
            return new FluidTagHeatSource(tag);
        }

        public static FluidTagHeatSource of(TagKey<Fluid> tag, @Nullable Component infoTooltip) {
            FluidTagHeatSource source = new FluidTagHeatSource(tag);
            source.withInfoTooltip(infoTooltip);
            return source;
        }

        @Override
        public boolean matches(Level level, BlockPos pos, BlockState state) {
            return tag != null && state != null && state.getFluidState().is(tag);
        }

        @Override
        public void forEachDisplayBlock(Consumer<Block> consumer) {
            if (tag == null) {
                return;
            }
            for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
                if (fluid == null) {
                    continue;
                }
                if (fluid.builtInRegistryHolder().is(tag)) {
                    BlockState legacy = fluid.defaultFluidState().createLegacyBlock();
                    if (legacy != null) {
                        Block block = legacy.getBlock();
                        if (block != null) {
                            consumer.accept(block);
                        }
                    }
                }
            }
        }
    }

    public static class FunctionalHeatSource extends HeatSource{
        public final BiPredicate<Level, BlockPos> function;
        public final HeatSource displayHeatSource;

        private FunctionalHeatSource(BiPredicate<Level, BlockPos> function, HeatSource displayHeatSource) {
            this.function = function;
            this.displayHeatSource = displayHeatSource;
        }

        public static FunctionalHeatSource of(BiPredicate<Level, BlockPos> function) {
            return new FunctionalHeatSource(function, null);
        }

        public static FunctionalHeatSource of(BiPredicate<Level, BlockPos> function, HeatSource displayHeatSource) {
            return new FunctionalHeatSource(function, displayHeatSource);
        }

        public static FunctionalHeatSource of(BiPredicate<Level, BlockPos> function, @Nullable Component infoTooltip) {
            FunctionalHeatSource source = new FunctionalHeatSource(function, null);
            source.withInfoTooltip(infoTooltip);
            return source;
        }

        public static FunctionalHeatSource of(BiPredicate<Level, BlockPos> function, HeatSource displayHeatSource, @Nullable Component infoTooltip) {
            FunctionalHeatSource source = new FunctionalHeatSource(function, displayHeatSource);
            source.withInfoTooltip(infoTooltip);
            return source;
        }

        @Override
        public boolean matches(Level level, BlockPos pos, BlockState state) {
            return function != null && level != null && pos != null && function.test(level, pos);
        }

        @Override
        public void forEachDisplayBlock(Consumer<Block> consumer) {
            if (displayHeatSource != null && consumer != null) {
                displayHeatSource.forEachDisplayBlock(consumer);
            }
        }
    }
}
