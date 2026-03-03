package com.xiaohunao.create_heat_js.common.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import com.xiaohunao.create_heat_js.common.HeatData;
import com.xiaohunao.create_heat_js.common.HeatSource;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;


public class HeatSourceRendererHelper {
    private static final long CAROUSEL_MS = 1000L;
    private static final Map<HeatData, List<Block>> DISPLAY_BLOCK_CACHE = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<HeatData, List<ItemStack>> DISPLAY_STACK_CACHE = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<HeatData, List<DisplayEntry>> DISPLAY_ENTRY_CACHE = Collections.synchronizedMap(new WeakHashMap<>());
    private static Map<Block, ItemStack> LEGACY_BLOCK_BUCKET_STACKS;

    public static final class DisplayEntry {
        public final Block block;
        public final ItemStack displayStack;
        @Nullable
        public final Component infoTooltip;

        private DisplayEntry(Block block, ItemStack displayStack, @Nullable Component infoTooltip) {
            this.block = block;
            this.displayStack = displayStack;
            this.infoTooltip = infoTooltip;
        }
    }

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

    public static int getCarouselIndex(HeatData heatData) {
        List<DisplayEntry> entries = getDisplayEntries(heatData);
        if (entries.isEmpty()) {
            return 0;
        }
        return (int) ((Util.getMillis() / CAROUSEL_MS) % entries.size());
    }

    /**
     * 获取用于轮播展示的 BlockState
     * 根据当前时间轮流返回 HeatData 中包含的所有热源方块。
     * 
     * @param heatData 热量数据
     * @return 当前时间点应该显示的方块状态
     */
    public static BlockState getCarouselDisplayBlockState(HeatData heatData) {
        List<DisplayEntry> entries = getDisplayEntries(heatData);
        if (entries.isEmpty()) {
            return null;
        }
        int index = getCarouselIndex(heatData);
        return entries.get(index).block.defaultBlockState();
    }

    public static List<DisplayEntry> getDisplayEntries(HeatData heatData) {
        if (heatData == null) {
            return List.of();
        }
        List<DisplayEntry> cached = DISPLAY_ENTRY_CACHE.get(heatData);
        if (cached != null) {
            return cached;
        }

        LinkedHashMap<Block, DisplayEntry> result = new LinkedHashMap<>();
        List<HeatSource> heatSources = heatData.getHeatSources();
        if (heatSources != null) {
            for (HeatSource heatSource : heatSources) {
                if (heatSource == null) {
                    continue;
                }
                Component tooltip = heatSource.getInfoTooltip();
                heatSource.forEachDisplayBlock(block -> {
                    if (block == null) {
                        return;
                    }
                    ItemStack stack = toDisplayStack(block);
                    if (stack == null || stack.isEmpty()) {
                        return;
                    }
                    DisplayEntry existing = result.get(block);
                    if (existing == null) {
                        result.put(block, new DisplayEntry(block, stack, tooltip));
                        return;
                    }
                    if (existing.infoTooltip == null && tooltip != null) {
                        result.put(block, new DisplayEntry(block, existing.displayStack, tooltip));
                    }
                });
            }
        }

        List<DisplayEntry> entries = List.copyOf(result.values());
        List<Block> blocks = entries.stream().map(e -> e.block).toList();
        List<ItemStack> stacks = entries.stream().map(e -> e.displayStack).toList();
        DISPLAY_ENTRY_CACHE.put(heatData, entries);
        DISPLAY_BLOCK_CACHE.put(heatData, blocks);
        DISPLAY_STACK_CACHE.put(heatData, stacks);
        return entries;
    }

    @Nullable
    public static DisplayEntry findDisplayEntry(HeatData heatData, BlockState state) {
        if (heatData == null || state == null) {
            return null;
        }
        Block block = state.getBlock();
        if (block == null) {
            return null;
        }
        for (DisplayEntry entry : getDisplayEntries(heatData)) {
            if (entry != null && entry.block == block) {
                return entry;
            }
        }
        return null;
    }

    @Nullable
    public static Component findInfoTooltip(HeatData heatData, BlockState state) {
        DisplayEntry entry = findDisplayEntry(heatData, state);
        return entry != null ? entry.infoTooltip : null;
    }

    @Nullable
    public static DisplayEntry getDisplayEntryAtIndex(HeatData heatData, int index) {
        List<DisplayEntry> entries = getDisplayEntries(heatData);
        if (entries.isEmpty()) {
            return null;
        }
        int clamped = Math.max(0, Math.min(index, entries.size() - 1));
        return entries.get(clamped);
    }

    @Nullable
    public static ItemStack getDisplayStackAtIndex(HeatData heatData, int index) {
        DisplayEntry entry = getDisplayEntryAtIndex(heatData, index);
        return entry != null ? entry.displayStack : null;
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
        getDisplayEntries(heatData);
        cached = DISPLAY_BLOCK_CACHE.get(heatData);
        return cached != null ? cached : List.of();
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

        getDisplayEntries(heatData);
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
        return !getDisplayEntries(heatData).isEmpty();
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
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (!(item instanceof BucketItem bucketItem)) {
                continue;
            }
            Fluid fluid = bucketItem.getFluid();
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
