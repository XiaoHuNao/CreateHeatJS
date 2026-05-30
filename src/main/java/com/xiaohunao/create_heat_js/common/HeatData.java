package com.xiaohunao.create_heat_js.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.create_heat_js.common.mixin.extensions.HeatConditionExpandAccessor;
import com.xiaohunao.create_heat_js.common.mixin.extensions.HeatLevelExpandAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;


public class HeatData {
    private final String name;
    private final int color;
    private final BlazeBurnerBlock.HeatLevel heatLevel;
    private final HeatCondition condition;
    private final List<HeatSource> heatSources;
    private final Set<String> satisfies;
    private final List<SatisfyRule> conditionalSatisfies;
    private ItemStack catalystDisplayItem;
    private ItemStack heatSourceDisplayItem;


    public HeatData(String name, int color, BlazeBurnerBlock.HeatLevel heatLevel, HeatCondition condition, List<HeatSource> heatSources) {
        this(name, color, heatLevel, condition, heatSources, null);
    }

    public HeatData(String name, int color, BlazeBurnerBlock.HeatLevel heatLevel, HeatCondition condition, List<HeatSource> heatSources, Set<String> satisfies) {
        this(name, color, heatLevel, condition, heatSources, satisfies, null);
    }

    public HeatData(String name, int color, BlazeBurnerBlock.HeatLevel heatLevel, HeatCondition condition, List<HeatSource> heatSources, Set<String> satisfies, List<SatisfyRule> conditionalSatisfies) {
        this.name = name;
        this.color = color;
        this.heatLevel = heatLevel;
        this.condition = condition;
        this.heatSources = heatSources != null ? heatSources : new ArrayList<>();
        this.satisfies = satisfies != null ? new HashSet<>(satisfies) : new HashSet<>();
        this.conditionalSatisfies = conditionalSatisfies != null ? new ArrayList<>(conditionalSatisfies) : new ArrayList<>();
        this.catalystDisplayItem = null;
        this.heatSourceDisplayItem = null;
     }


    public static Builder builder(String heatName) {
         return new Builder(heatName);
    }

    public String getName() {
        return this.name;
    }

    public int getColor() {
        return this.color;
    }

    public BlazeBurnerBlock.HeatLevel getHeatLevel() {
        return this.heatLevel;
    }

    public HeatCondition getCondition() {
        return this.condition;
    }

    public List<HeatSource> getHeatSources() {
        return this.heatSources;
    }

    public Set<String> getSatisfies() {
        return this.satisfies;
    }

    public List<SatisfyRule> getConditionalSatisfies() {
        return this.conditionalSatisfies;
    }

    public ItemStack getCatalystDisplayItem() {
        return catalystDisplayItem;
    }

    public HeatData setCatalystDisplayItem(ItemStack catalystDisplayItem) {
        this.catalystDisplayItem = catalystDisplayItem;
        return this;
    }

    public ItemStack getHeatSourceDisplayItem() {
        return heatSourceDisplayItem;
    }

    public HeatData setHeatSourceDisplayItem(ItemStack heatSourceDisplayItem) {
        this.heatSourceDisplayItem = heatSourceDisplayItem;
        return this;
    }

    public HeatData addHeatSource(HeatSource heatSource) {
        this.heatSources.add(heatSource);
        return this;
    }

    public HeatData addHeatSource(Block... blocks) {
        for (Block block : blocks) {
            this.heatSources.add(HeatSource.BlockHeatSource.of(block));
        }
        return this;
    }

    public HeatData addHeatSource(@Nullable Component infoTooltip, Block... blocks) {
        for (Block block : blocks) {
            this.heatSources.add(HeatSource.BlockHeatSource.of(block, infoTooltip));
        }
        return this;
    }

    public HeatData addHeatSource(BlockState... blockStates) {
        this.heatSources.add(HeatSource.BlockStateHeatSource.of(blockStates));
        return this;
    }

    public HeatData addHeatSource(@Nullable Component infoTooltip, BlockState... blockStates) {
        this.heatSources.add(HeatSource.BlockStateHeatSource.of(infoTooltip, blockStates));
        return this;
    }

    public HeatData addHeatSourceBlockTag(TagKey<Block> blockTag) {
        this.heatSources.add(HeatSource.BlockTagHeatSource.of(blockTag));
        return this;
    }

    public HeatData addHeatSourceBlockTag(TagKey<Block> blockTag, @Nullable Component infoTooltip) {
        this.heatSources.add(HeatSource.BlockTagHeatSource.of(blockTag, infoTooltip));
        return this;
    }

    public HeatData addHeatSource(Fluid fluid) {
        this.heatSources.add(HeatSource.FluidHeatSource.of(fluid));
        return this;
    }

    public HeatData addHeatSource(Fluid fluid, @Nullable Component infoTooltip) {
        this.heatSources.add(HeatSource.FluidHeatSource.of(fluid, infoTooltip));
        return this;
    }

    public HeatData addHeatSourceFluidTag(TagKey<Fluid> fluidTag) {
        this.heatSources.add(HeatSource.FluidTagHeatSource.of(fluidTag));
        return this;
    }

    public HeatData addHeatSourceFluidTag(TagKey<Fluid> fluidTag, @Nullable Component infoTooltip) {
        this.heatSources.add(HeatSource.FluidTagHeatSource.of(fluidTag, infoTooltip));
        return this;
    }

    public HeatData addHeatSourceIf(BiPredicate<Level, BlockPos> function) {
        this.heatSources.add(HeatSource.FunctionalHeatSource.of(function));
        return this;
    }

    public HeatData addHeatSourceIf(BiPredicate<Level, BlockPos> function, Block block) {
        this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, HeatSource.BlockHeatSource.of(block)));
        return this;
    }

    public HeatData addHeatSourceIf(BiPredicate<Level, BlockPos> function, Fluid fluid) {
        this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, HeatSource.FluidHeatSource.of(fluid)));
        return this;
    }

    public HeatData addHeatSourceIf(BiPredicate<Level, BlockPos> function, TagKey<?> tag) {
        if (tag == null) {
            this.heatSources.add(HeatSource.FunctionalHeatSource.of(function));
            return this;
        }
        if (Registries.BLOCK.equals(tag.registry())) {
            @SuppressWarnings("unchecked")
            TagKey<Block> blockTag = (TagKey<Block>) tag;
            this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, HeatSource.BlockTagHeatSource.of(blockTag)));
            return this;
        }
        if (Registries.FLUID.equals(tag.registry())) {
            @SuppressWarnings("unchecked")
            TagKey<Fluid> fluidTag = (TagKey<Fluid>) tag;
            this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, HeatSource.FluidTagHeatSource.of(fluidTag)));
            return this;
        }
        this.heatSources.add(HeatSource.FunctionalHeatSource.of(function));
        return this;
    }

    public HeatData addHeatSourceIf(BiPredicate<Level, BlockPos> function, @Nullable Component infoTooltip) {
        this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, infoTooltip));
        return this;
    }

    public HeatData addHeatSourceIf(BiPredicate<Level, BlockPos> function, Block block, @Nullable Component infoTooltip) {
        this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, HeatSource.BlockHeatSource.of(block), infoTooltip));
        return this;
    }

    public HeatData addHeatSourceIf(BiPredicate<Level, BlockPos> function, Fluid fluid, @Nullable Component infoTooltip) {
        this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, HeatSource.FluidHeatSource.of(fluid), infoTooltip));
        return this;
    }

    public HeatData addHeatSourceIf(BiPredicate<Level, BlockPos> function, TagKey<?> tag, @Nullable Component infoTooltip) {
        if (tag == null) {
            this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, infoTooltip));
            return this;
        }
        if (Registries.BLOCK.equals(tag.registry())) {
            @SuppressWarnings("unchecked")
            TagKey<Block> blockTag = (TagKey<Block>) tag;
            this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, HeatSource.BlockTagHeatSource.of(blockTag), infoTooltip));
            return this;
        }
        if (Registries.FLUID.equals(tag.registry())) {
            @SuppressWarnings("unchecked")
            TagKey<Fluid> fluidTag = (TagKey<Fluid>) tag;
            this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, HeatSource.FluidTagHeatSource.of(fluidTag), infoTooltip));
            return this;
        }
        this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, infoTooltip));
        return this;
    }

    public HeatData satisfies(String... requirements) {
        if (requirements == null) {
            return this;
        }
        for (String requirement : requirements) {
            if (requirement != null) {
                this.satisfies.add(requirement);
            }
        }
        return this;
    }

    public HeatData satisfiesIf(String requirement, Predicate<HeatRecipeContext> predicate) {
        if (requirement == null || predicate == null) {
            return this;
        }
        this.conditionalSatisfies.add(new SatisfyRule(requirement, predicate));
        return this;
    }

    public boolean matchesHeatSource(Level level, BlockPos pos, BlockState state) {
        if (level == null || pos == null || state == null) {
            return false;
        }
        if (state.isAir()) {
            return false;
        }
        for (HeatSource heatSource : heatSources) {
            if (heatSource != null && heatSource.matches(level, pos, state)) {
                return true;
            }
        }
        return false;
    }

    public static class Builder {
        private String name;
        private int color = 0xffffff;
        private final List<HeatSource> heatSources = new ArrayList<>();
        private final Set<String> satisfies = new HashSet<>();
        private final List<SatisfyRule> conditionalSatisfies = new ArrayList<>();
        private ItemStack catalystDisplayItem;
        private ItemStack heatSourceDisplayItem;

        private BlazeBurnerBlock.HeatLevel heatLevel;
        private HeatCondition heatCondition;

        public Builder(String name) {
            this.name = name;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder catalyst(ItemStack catalystDisplayItem) {
            this.catalystDisplayItem = catalystDisplayItem;
            return this;
        }

        public Builder heatSourceSlotItem(ItemStack heatSourceDisplayItem) {
            this.heatSourceDisplayItem = heatSourceDisplayItem;
            return this;
        }

        public Builder addHeatSource(HeatSource heatSource) {
            this.heatSources.add(heatSource);
            return this;
        }

        public Builder addHeatSource(HeatSource heatSource, @Nullable Component infoTooltip) {
            if (heatSource != null) {
                this.heatSources.add(heatSource.withInfoTooltip(infoTooltip));
            }
            return this;
        }

        /**
         * 添加热源（支持字符串格式）
         * 支持的格式：
         * - blocktag:namespace:path (方块标签)
         * - fluidtag:namespace:path (流体标签)
         * - block:namespace:path (方块 ID)
         * - fluid:namespace:path (流体 ID)
         * - namespace:path[prop=value] (方块状态)
         * - #namespace:path (尝试匹配方块或流体标签)
         *
         * @param heatSource 热源字符串描述
         * @return Builder
         */
        public Builder addHeatSource(String heatSource) {
            HeatSource parsed = createHeatSourceFromString(heatSource);
            if (parsed != null) {
                this.heatSources.add(parsed);
            }
            return this;
        }

        public Builder addHeatSource(String heatSource, @Nullable Component infoTooltip) {
            HeatSource parsed = createHeatSourceFromString(heatSource);
            if (parsed != null) {
                this.heatSources.add(parsed.withInfoTooltip(infoTooltip));
            }
            return this;
        }


        public Builder addFunctionalHeatSource(BiPredicate<Level, BlockPos> function) {
            this.heatSources.add(HeatSource.FunctionalHeatSource.of(function));
            return this;
        }

        public Builder addFunctionalHeatSource(BiPredicate<Level, BlockPos> function, @Nullable Component infoTooltip) {
            this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, infoTooltip));
            return this;
        }

        public Builder addHeatSourceIf(BiPredicate<Level, BlockPos> function, String heatSource) {
            HeatSource display = createHeatSourceFromString(heatSource);
            this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, display));
            return this;
        }

        public Builder addHeatSourceIf(BiPredicate<Level, BlockPos> function, String heatSource, @Nullable Component infoTooltip) {
            HeatSource display = createHeatSourceFromString(heatSource);
            this.heatSources.add(HeatSource.FunctionalHeatSource.of(function, display, infoTooltip));
            return this;
        }


        public Builder addBlockHeatSource(Block... blocks) {
            for (Block block : blocks) {
                this.heatSources.add(HeatSource.BlockHeatSource.of(block));
            }
            return this;
        }

        public Builder addBlockHeatSource(@Nullable Component infoTooltip, Block... blocks) {
            for (Block block : blocks) {
                this.heatSources.add(HeatSource.BlockHeatSource.of(block, infoTooltip));
            }
            return this;
        }

        public Builder addBlockStateHeatSource(BlockState... blockStates) {
            this.heatSources.add(HeatSource.BlockStateHeatSource.of(blockStates));
            return this;
        }

        public Builder addBlockStateHeatSource(@Nullable Component infoTooltip, BlockState... blockStates) {
            this.heatSources.add(HeatSource.BlockStateHeatSource.of(infoTooltip, blockStates));
            return this;
        }

        public Builder addBlockTagHeatSource(TagKey<Block> blockTag) {
            this.heatSources.add(HeatSource.BlockTagHeatSource.of(blockTag));
            return this;
        }

        public Builder addBlockTagHeatSource(TagKey<Block> blockTag, @Nullable Component infoTooltip) {
            this.heatSources.add(HeatSource.BlockTagHeatSource.of(blockTag, infoTooltip));
            return this;
        }

        public Builder addFluidHeatSource(Fluid fluid) {
            this.heatSources.add(HeatSource.FluidHeatSource.of(fluid));
            return this;
        }

        public Builder addFluidHeatSource(Fluid fluid, @Nullable Component infoTooltip) {
            this.heatSources.add(HeatSource.FluidHeatSource.of(fluid, infoTooltip));
            return this;
        }

        public Builder addFluidTagHeatSource(TagKey<Fluid> fluidTag) {
            this.heatSources.add(HeatSource.FluidTagHeatSource.of(fluidTag));
            return this;
        }

        public Builder addFluidTagHeatSource(TagKey<Fluid> fluidTag, @Nullable Component infoTooltip) {
            this.heatSources.add(HeatSource.FluidTagHeatSource.of(fluidTag, infoTooltip));
            return this;
        }

        private HeatSource createHeatSourceFromString(String heatSource) {
            if (heatSource == null || heatSource.isBlank()) {
                return null;
            }
            String raw = heatSource.trim();
            String lower = raw.toLowerCase(Locale.ROOT);

            if (lower.startsWith("blocktag:") || lower.startsWith("block_tag:")) {
                String tagId = raw.substring(raw.indexOf(':') + 1).trim();
                if (!tagId.startsWith("#")) {
                    tagId = "#" + tagId;
                }
                TagKey<Block> tag = createBlockTag(tagId);
                if (tag != null) {
                    return HeatSource.BlockTagHeatSource.of(tag);
                }
                return null;
            }

            if (lower.startsWith("fluidtag:") || lower.startsWith("fluid_tag:")) {
                String tagId = raw.substring(raw.indexOf(':') + 1).trim();
                if (!tagId.startsWith("#")) {
                    tagId = "#" + tagId;
                }
                TagKey<Fluid> tag = createFluidTag(tagId);
                if (tag != null) {
                    return HeatSource.FluidTagHeatSource.of(tag);
                }
                return null;
            }

            if (lower.startsWith("block:")) {
                String idStr = raw.substring("block:".length()).trim();
                ResourceLocation id = parseId(idStr);
                if (id != null) {
                    Block block = BuiltInRegistries.BLOCK.get(id);
                    if (block != null && !block.defaultBlockState().isAir()) {
                        return HeatSource.BlockHeatSource.of(block);
                    }
                }
                return null;
            }

            if (lower.startsWith("fluid:")) {
                String idStr = raw.substring("fluid:".length()).trim();
                ResourceLocation id = parseId(idStr);
                if (id != null) {
                    Fluid fluid = BuiltInRegistries.FLUID.get(id);
                    if (fluid != null) {
                        return HeatSource.FluidHeatSource.of(fluid);
                    }
                }
                return null;
            }

            BlockState parsedState = parseBlockState(raw);
            if (parsedState != null) {
                return HeatSource.BlockStateHeatSource.of(parsedState);
            }

            if (raw.startsWith("#")) {
                TagKey<Block> blockTag = createBlockTag(raw);
                TagKey<Fluid> fluidTag = createFluidTag(raw);

                boolean anyBlock = blockTag != null && BuiltInRegistries.BLOCK.stream()
                    .anyMatch(block -> block.builtInRegistryHolder().is(blockTag));
                boolean anyFluid = fluidTag != null && BuiltInRegistries.FLUID.stream()
                    .anyMatch(fluid -> fluid.builtInRegistryHolder().is(fluidTag));

                if (anyFluid && !anyBlock) {
                    return HeatSource.FluidTagHeatSource.of(fluidTag);
                } else if (blockTag != null) {
                    return HeatSource.BlockTagHeatSource.of(blockTag);
                } else if (fluidTag != null) {
                    return HeatSource.FluidTagHeatSource.of(fluidTag);
                }
                return null;
            }

            ResourceLocation id = parseId(raw);
            if (id == null) {
                return null;
            }

            Block block = BuiltInRegistries.BLOCK.get(id);
            if (block != null && !block.defaultBlockState().isAir()) {
                return HeatSource.BlockHeatSource.of(block);
            }

            Fluid fluid = BuiltInRegistries.FLUID.get(id);
            if (fluid != null && fluid != net.minecraft.world.level.material.Fluids.EMPTY) {
                return HeatSource.FluidHeatSource.of(fluid);
            }
            return null;
        }

        public Builder satisfies(String... requirements) {
            if (requirements == null) {
                return this;
            }
            for (String requirement : requirements) {
                if (requirement != null) {
                    this.satisfies.add(requirement);
                }
            }
            return this;
        }

        public Builder satisfiesIf(String requirement, Predicate<HeatRecipeContext> predicate) {
            if (requirement == null || predicate == null) {
                return this;
            }
            this.conditionalSatisfies.add(new SatisfyRule(requirement, predicate));
            return this;
        }


        public HeatData build() {
            this.heatLevel = HeatLevelExpandAccessor.chj$create(name);
            this.heatCondition = HeatConditionExpandAccessor.chj$create(name, color);

            HeatData heatData = new HeatData(name, color, heatLevel, heatCondition, heatSources, satisfies, conditionalSatisfies);
            heatData.setCatalystDisplayItem(catalystDisplayItem);
            heatData.setHeatSourceDisplayItem(heatSourceDisplayItem);
            return heatData;
        }

        private static ResourceLocation parseId(String id) {
            if (id == null || id.isBlank()) {
                return null;
            }
            String trimmed = id.trim();
            if (trimmed.startsWith("#")) {
                trimmed = trimmed.substring(1);
            }
            return ResourceLocation.tryParse(trimmed);
        }

        private static TagKey<Block> createBlockTag(String tagId) {
            ResourceLocation id = parseId(tagId);
            if (id == null) {
                return null;
            }
            return TagKey.create(Registries.BLOCK, id);
        }

        private static TagKey<Fluid> createFluidTag(String tagId) {
            ResourceLocation id = parseId(tagId);
            if (id == null) {
                return null;
            }
            return TagKey.create(Registries.FLUID, id);
        }

        private static BlockState parseBlockState(String text) {
            if (text == null) {
                return null;
            }
            int start = text.indexOf('[');
            if (start <= 0 || !text.endsWith("]")) {
                return null;
            }

            String idPart = text.substring(0, start).trim();
            ResourceLocation id = parseId(idPart);
            if (id == null) {
                return null;
            }

            Block block = BuiltInRegistries.BLOCK.get(id);
            if (block == null) {
                return null;
            }

            BlockState state = block.defaultBlockState();
            String props = text.substring(start + 1, text.length() - 1).trim();
            if (props.isEmpty()) {
                return state;
            }

            String[] pairs = props.split(",");
            for (String pair : pairs) {
                if (pair == null) {
                    continue;
                }
                String p = pair.trim();
                if (p.isEmpty()) {
                    continue;
                }
                int eq = p.indexOf('=');
                if (eq <= 0 || eq == p.length() - 1) {
                    continue;
                }
                String key = p.substring(0, eq).trim();
                String value = p.substring(eq + 1).trim();
                if (key.isEmpty() || value.isEmpty()) {
                    continue;
                }
                Property<?> property = block.getStateDefinition().getProperty(key);
                if (property == null) {
                    continue;
                }
                state = applyProperty(state, property, value);
            }
            return state;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static BlockState applyProperty(BlockState state, Property property, String value) {
            Optional parsed = property.getValue(value);
            if (parsed.isEmpty()) {
                return state;
            }
            return state.setValue(property, (Comparable) parsed.get());
        }

    }


    public static class SatisfyRule {
        private final String requirement;
        private final Predicate<HeatRecipeContext> predicate;


        public SatisfyRule(String requirement, Predicate<HeatRecipeContext> predicate) {
            this.requirement = requirement;
            this.predicate = predicate;
        }

        public String getRequirement() {
            return requirement;
        }

        public Predicate<HeatRecipeContext> getPredicate() {
            return predicate;
        }
    }
}
