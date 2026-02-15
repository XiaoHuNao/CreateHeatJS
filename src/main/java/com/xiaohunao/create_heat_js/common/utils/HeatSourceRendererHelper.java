package com.xiaohunao.create_heat_js.common.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.xiaohunao.create_heat_js.common.HeatData;
import com.xiaohunao.create_heat_js.common.HeatSource;

import net.minecraft.Util;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;


public class HeatSourceRendererHelper {
    private static final long CAROUSEL_MS = 1000L;
    private static final Map<HeatData, List<Block>> DISPLAY_BLOCK_CACHE = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<HeatData, List<ItemStack>> DISPLAY_STACK_CACHE = Collections.synchronizedMap(new WeakHashMap<>());
    private static Map<Block, ItemStack> LEGACY_BLOCK_BUCKET_STACKS;

    /**
     * 从 HeatData 中获取一个代表性的 BlockState 用于渲染
     * 优先级：BlockStateHeatSource > BlockHeatSource > null
     * 
     * @param heatData 热量数据
     * @return 要显示的方块状态，如果没有找到则返回 null
     */
    public static BlockState getDisplayBlockState(HeatData heatData) {
        List<Block> blocks = getDisplayBlocks(heatData);
        if (blocks.isEmpty()) {
            return null;
        }
        return blocks.get(0).defaultBlockState();
    }

    /**
     * 获取用于轮播展示的 BlockState
     * 根据当前时间轮流返回 HeatData 中包含的所有热源方块。
     * 
     * @param heatData 热量数据
     * @return 当前时间点应该显示的方块状态
     */
    public static BlockState getCarouselDisplayBlockState(HeatData heatData) {
        List<Block> blocks = getDisplayBlocks(heatData);
        if (blocks.isEmpty()) {
            return null;
        }
        int index = (int) ((Util.getMillis() / CAROUSEL_MS) % blocks.size());
        return blocks.get(index).defaultBlockState();
    }

    /**
     * 获取 HeatData 对应的所有可展示方块列表
     * 结果会被缓存以提高性能。
     * 
     * @param heatData 热量数据
     * @return 方块列表
     */
    public static List<Block> getDisplayBlocks(HeatData heatData) {
        if (heatData == null) {
            return List.of();
        }
        List<Block> cached = DISPLAY_BLOCK_CACHE.get(heatData);
        if (cached != null) {
            return cached;
        }

        LinkedHashMap<Block, ItemStack> result = new LinkedHashMap<>();
        List<HeatSource> heatSources = heatData.getHeatSources();
        if (heatSources != null) {
            for (HeatSource heatSource : heatSources) {
                heatSource.forEachDisplayBlock(block -> putIfDisplayable(result, block));
            }
        }

        List<Block> blocks = List.copyOf(result.keySet());
        List<ItemStack> stacks = List.copyOf(result.values());
        DISPLAY_BLOCK_CACHE.put(heatData, blocks);
        DISPLAY_STACK_CACHE.put(heatData, stacks);
        return blocks;
    }

    /**
     * 获取 HeatData 对应的所有可展示物品堆列表
     * 
     * @param heatData 热量数据
     * @return 物品堆列表
     */
    public static List<ItemStack> getHeatSourceSlotItemStacks(HeatData heatData) {
        if (heatData == null) {
            return List.of();
        }
        List<ItemStack> cached = DISPLAY_STACK_CACHE.get(heatData);
        if (cached != null) {
            return cached;
        }

        getDisplayBlocks(heatData);
        cached = DISPLAY_STACK_CACHE.get(heatData);
        return cached != null ? cached : List.of();
    }

    /**
     * 检查 HeatData 是否有可渲染的热源
     * 
     * @param heatData 热量数据
     * @return 如果有可渲染的热源返回 true，否则返回 false
     */
    public static boolean hasRenderableHeatSource(HeatData heatData) {
        return !getDisplayBlocks(heatData).isEmpty();
    }

    /**
     * 从物品堆解析出对应的方块状态
     * 支持方块物品和桶物品（解析为对应的流体方块）。
     * 
     * @param stack 物品堆
     * @return 对应的方块状态，如果无法解析则返回 null
     */
    public static BlockState getDisplayStateFromHeatSourceSlotItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        Item item = stack.getItem();
        if (item instanceof BlockItem blockItem) {
            return blockItem.getBlock().defaultBlockState();
        }
        Fluid fluid = FluidUtil.getFluidContained(stack).map(FluidStack::getFluid).orElse(null);
        if (fluid != null) {
            return fluid.defaultFluidState().createLegacyBlock();
        }
        return null;
    }

    private static void putIfDisplayable(LinkedHashMap<Block, ItemStack> map, Block block) {
        if (block == null || map.containsKey(block)) {
            return;
        }
        ItemStack stack = toDisplayStack(block);
        if (stack != null && !stack.isEmpty()) {
            map.put(block, stack);
        }
    }

    private static ItemStack toDisplayStack(Block block) {
        Item item = block.asItem();
        if (item != null && item != Items.AIR) {
            return new ItemStack(item);
        }

        ItemStack bucket = getLegacyBlockBucketStacks().get(block);
        if (bucket == null || bucket.isEmpty()) {
            return null;
        }
        return bucket.copy();
    }

    private static Map<Block, ItemStack> getLegacyBlockBucketStacks() {
        if (LEGACY_BLOCK_BUCKET_STACKS != null) {
            return LEGACY_BLOCK_BUCKET_STACKS;
        }

        LinkedHashMap<Block, ItemStack> map = new LinkedHashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof BucketItem bucketItem)) {
                continue;
            }
            Fluid fluid = FluidUtil.getFluidContained(new ItemStack(bucketItem)).map(FluidStack::getFluid).orElse(null);
            if (fluid == null) {
                continue;
            }
            BlockState legacy = fluid.defaultFluidState().createLegacyBlock();
            if (legacy == null) {
                continue;
            }
            Block block = legacy.getBlock();
            if (block == null || map.containsKey(block)) {
                continue;
            }
            map.put(block, new ItemStack(item));
        }
        LEGACY_BLOCK_BUCKET_STACKS = map;
        return map;
    }
}
