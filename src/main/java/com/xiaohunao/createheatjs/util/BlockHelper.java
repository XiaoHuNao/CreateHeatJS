package com.xiaohunao.createheatjs.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import dev.latvian.mods.kubejs.util.UtilsJS;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class BlockHelper {
    public static BlockState parseBlockState(String string) {
        if (string.isEmpty()) {
            return Blocks.AIR.defaultBlockState();
        }

        int i = string.indexOf('[');
        boolean hasProperties = i >= 0 && string.indexOf(']') == string.length() - 1;
        String blockName = hasProperties ? string.substring(0, i) : string;
        Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(blockName));
        if (block == null) {
            return Blocks.AIR.defaultBlockState();
        }

        BlockState state = block.defaultBlockState();
        if (hasProperties) {
            String properties = string.substring(i + 1, string.length() - 1);
            for (String s : properties.split(",")) {
                String[] s1 = s.split("=", 2);
                if (s1.length == 2 && !s1[0].isEmpty() && !s1[1].isEmpty()) {
                    Optional<?> o = state.getBlock().getStateDefinition().getProperty(s1[0]).getValue(s1[1]);
                    if (o.isPresent()) {
                        state = state.setValue(state.getBlock().getStateDefinition().getProperty(s1[0]), UtilsJS.cast(o.get()));
                    }
                }
            }
        }
        return state;
    }

    public static boolean hetaSourceRender(GuiGraphics graphics, IDrawable drawable, HeatCondition requiredHeat) {
         HeatData heatData = HeatData.getHeatData(requiredHeat.toString());
        Map.Entry<Pair<String,String>, TriPredicate<Level, BlockPos, BlockState>>[] heatSource = heatData.getHeatSource().entrySet().toArray(new Map.Entry[0]);


        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return false;
        }
        long dayTime = level.getDayTime();

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(drawable.getWidth() / 2 + 3, 55, 200);
        pose.mulPose(Axis.XP.rotationDegrees(-15.5f));
        pose.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 23;

        int itemIndexToShow = (int) ((dayTime / 25) % (heatSource.length));

        Map.Entry<Pair<String, String>,TriPredicate<Level, BlockPos, BlockState>> pair = heatSource[itemIndexToShow];
        if (pair == null) {
            return false;
        }
        BlockState heatBlockState = BlockHelper.parseBlockState(pair.getKey().getSecond());
        if (heatBlockState == null || heatBlockState.getBlock() == AllBlocks.BLAZE_BURNER.get()){
            pose.popPose();
            return false;
        }
        AnimatedKinetics.defaultBlockElement(heatBlockState)
                .atLocal(1, 1.65, 1)
                .rotate(0, 180, 0)
                .scale(scale)
                .render(graphics);
        pose.popPose();
        return true;
//


//        HeatManager manager = CreateHeatJS.Manager;
//        Collection<Pair<String, TriPredicate<Level, BlockPos, BlockState>>> heatSource = manager.getHeatSource(requiredHeat.toString());
//        if (heatSource.isEmpty()) return false;
//
//
//        ClientLevel level = Minecraft.getInstance().level;
//        if (level == null) {
//            return false;
//        }
//        long dayTime = level.getDayTime();
//
//        PoseStack pose = graphics.pose();
//        pose.pushPose();
//        pose.translate(drawable.getWidth() / 2 + 3, 55, 200);
//        pose.mulPose(Axis.XP.rotationDegrees(-15.5f));
//        pose.mulPose(Axis.YP.rotationDegrees(22.5f));
//        int scale = 23;
//
//        int itemIndexToShow = (int) ((dayTime / 25) % (heatSource.size()));
//        Pair<String, Predicate<BlockState>> pair = heatSource.toArray(new Pair[0])[itemIndexToShow];
//        if (pair == null) {
//            return false;
//        }
//        BlockState heatBlockState = BlockHelper.parseBlockState(pair.getFirst());
//        if (heatBlockState == null || heatBlockState.getBlock() == AllBlocks.BLAZE_BURNER.get()){
//            pose.popPose();
//            return false;
//        }
//        AnimatedKinetics.defaultBlockElement(heatBlockState)
//                .atLocal(1, 1.65, 1)
//                .rotate(0, 180, 0)
//                .scale(scale)
//                .render(graphics);
//        pose.popPose();
//        return true;
    }
}
