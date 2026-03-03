package com.xiaohunao.create_heat_js.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.create_heat_js.CreateHeatJS;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;


public class HeatManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final HeatManager INSTANCE = new HeatManager();

    private final Map<Block, Map<BlockState, BlazeBurnerBlock.HeatLevel>> heatLevels = new HashMap<>();
    private final Map<BlazeBurnerBlock.HeatLevel,HeatData> heatDatas = new HashMap<>();
    private final BiMap<BlazeBurnerBlock.HeatLevel,HeatCondition>  heatConditions = HashBiMap.create();
    private final HeatRelations relations = new HeatRelations();
    private final Map<Block, List<HeatData>> heatDatasByBlock = new HashMap<>();
    private final List<HeatData> functionalHeatDatas = new ArrayList<>();
    private final Map<HeatData, Set<Block>> indexedBlocksByHeatData = new HashMap<>();
    private final Set<HeatData> indexedAsFunctional = new HashSet<>();

    private HeatManager() {
    }

    public static HeatManager getInstance() {
        return INSTANCE;
    }

    public HeatRelations getRelations() {
        return relations;
    }

    public boolean matchesHeatRequirement(String providerConditionName, String requirementConditionName, HeatRecipeContext context) {
        if (providerConditionName == null || requirementConditionName == null) {
            return false;
        }

        return relations.matches(providerConditionName, requirementConditionName, context);
    }

    public void onFMLCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("CreateHeatJS: HeatManager onFMLCommonSetup");

        for (Block block : BuiltInRegistries.BLOCK) {
            block.getStateDefinition().getPossibleStates().forEach(blockState -> {
                if (blockState.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
                    BlazeBurnerBlock.HeatLevel level = blockState.getValue(BlazeBurnerBlock.HEAT_LEVEL);

                    if (heatDatas.containsKey(level)) {
                        Map<BlockState, BlazeBurnerBlock.HeatLevel> stateMap = heatLevels.computeIfAbsent(block, k -> new HashMap<>());
                        stateMap.put(blockState, level);
                    }
                }
            });
        }

//        CreateHeatJS.heatProvider.addDefaultHeatSources();
    }

    public Map<BlockState, BlazeBurnerBlock.HeatLevel> getHeatLevels(Block block) {
        return heatLevels.get(block);
    }

    public BlazeBurnerBlock.HeatLevel getHeatLevel(BlockState blockState) {
        Block block = blockState.getBlock();
        Map<BlockState, BlazeBurnerBlock.HeatLevel> stateMap = heatLevels.get(block);
        return stateMap != null ? stateMap.get(blockState) : null;
    }

    public HeatData getHeatData(BlazeBurnerBlock.HeatLevel level) {
        return heatDatas.get(level);
    }

    public Collection<HeatData> getAllHeatDatas() {
        return Collections.unmodifiableCollection(heatDatas.values());
    }


    public HeatData getHeatData(String name) {
        if (name == null) {
            return null;
        }

        // 先按 HeatData.name 精确匹配
        for (HeatData data : heatDatas.values()) {
            if (name.equals(data.getName())) {
                return data;
            }
        }

        // 再尝试按 HeatLevel 枚举名匹配（例如 "KINDLED"、"SEETHING" 等）
        try {
            BlazeBurnerBlock.HeatLevel level = BlazeBurnerBlock.HeatLevel.valueOf(name);
            return heatDatas.get(level);
        } catch (IllegalArgumentException ignored) {
        }

        return null;
    }


    public Map<Block, Map<BlockState, BlazeBurnerBlock.HeatLevel>> getAllHeatLevels() {
        return heatLevels;
    }


    public void registerHeatData(HeatData heatData) {
        heatDatas.put(heatData.getHeatLevel(), heatData);
        heatConditions.put(heatData.getHeatLevel(),heatData.getCondition());
        relations.addLinks(heatData.getCondition().name(), heatData.getSatisfies());
        for (HeatData.SatisfyRule rule : heatData.getConditionalSatisfies()) {
            relations.addConditionalLink(heatData.getCondition().name(), rule.getRequirement(), rule.getPredicate());
        }
        reindexHeatData(heatData);
    }


    public void reindexHeatData(HeatData heatData) {
        if (heatData == null) {
            return;
        }

        Set<Block> oldBlocks = indexedBlocksByHeatData.remove(heatData);
        if (oldBlocks != null) {
            for (Block block : oldBlocks) {
                List<HeatData> list = heatDatasByBlock.get(block);
                if (list != null) {
                    list.remove(heatData);
                    if (list.isEmpty()) {
                        heatDatasByBlock.remove(block);
                    }
                }
            }
        }

        if (indexedAsFunctional.remove(heatData)) {
            functionalHeatDatas.remove(heatData);
        }

        Set<Block> newBlocks = new HashSet<>();
        boolean hasFunctional = false;
        for (HeatSource heatSource : heatData.getHeatSources()) {
            if (heatSource instanceof HeatSource.BlockHeatSource blockHeatSource) {
                newBlocks.add(blockHeatSource.block);
            } else if (heatSource instanceof HeatSource.BlockStateHeatSource blockStateHeatSource) {
                for (BlockState blockState : blockStateHeatSource.blockStates) {
                    if (blockState != null) {
                        newBlocks.add(blockState.getBlock());
                    }
                }
            } else if (heatSource instanceof HeatSource.BlockTagHeatSource blockTagHeatSource) {
                if (blockTagHeatSource.tag != null) {
                    BuiltInRegistries.BLOCK.forEach(block -> {
                        if (block.builtInRegistryHolder().is(blockTagHeatSource.tag)) {
                            newBlocks.add(block);
                        }
                    });
                }
            } else if (heatSource instanceof HeatSource.FluidHeatSource fluidHeatSource) {
                Fluid fluid = fluidHeatSource.fluid;
                if (fluid != null) {
                    BlockState legacy = fluid.defaultFluidState().createLegacyBlock();
                    if (legacy != null) {
                        newBlocks.add(legacy.getBlock());
                    }
                }
            } else if (heatSource instanceof HeatSource.FluidTagHeatSource fluidTagHeatSource) {
                if (fluidTagHeatSource.tag != null) {
                    BuiltInRegistries.FLUID.forEach(fluid -> {
                        if (fluid.builtInRegistryHolder().is(fluidTagHeatSource.tag)) {
                            BlockState legacy = fluid.defaultFluidState().createLegacyBlock();
                            if (legacy != null) {
                                newBlocks.add(legacy.getBlock());
                            }
                        }
                    });
                }
            } else if (heatSource instanceof HeatSource.FunctionalHeatSource) {
                hasFunctional = true;
            }
        }

        if (!newBlocks.isEmpty()) {
            indexedBlocksByHeatData.put(heatData, newBlocks);
            for (Block block : newBlocks) {
                heatDatasByBlock.computeIfAbsent(block, k -> new ArrayList<>()).add(heatData);
            }
        }

        if (hasFunctional) {
            indexedAsFunctional.add(heatData);
            functionalHeatDatas.add(heatData);
        }
    }


    public List<HeatData> getCandidateHeatDatas(BlockState state) {
        if (state == null) {
            return new ArrayList<>(getAllHeatDatas());
        }

        List<HeatData> candidates = new ArrayList<>();
        List<HeatData> byBlock = heatDatasByBlock.get(state.getBlock());
        if (byBlock != null) {
            candidates.addAll(byBlock);
        }
        if (!functionalHeatDatas.isEmpty()) {
            candidates.addAll(functionalHeatDatas);
        }
        return candidates;
    }

    public BlazeBurnerBlock.HeatLevel getHeatLevel(HeatCondition heatCondition) {
        return heatConditions.inverse().get(heatCondition);
    }

    public HeatCondition getHeatCondition (BlazeBurnerBlock.HeatLevel heatLevel) {
        return heatConditions.get(heatLevel);
    }

    public boolean hasHeatCondition(HeatCondition heatCondition) {
        return heatConditions.inverse().containsKey(heatCondition);
    }

    public boolean hasHeatLevel(BlazeBurnerBlock.HeatLevel heatLevel) {
        return heatDatas.containsKey(heatLevel);
    }

}
