package com.xiaohunao.create_heat_js.common.utils;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.xiaohunao.create_heat_js.client.compat.jei.animation.AnimatedBlockHeatSource;
import com.xiaohunao.create_heat_js.common.HeatData;
import com.xiaohunao.create_heat_js.common.HeatManager;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;


public class CategoryHelper {
    /**
     * 绘制自定义热源
     * 如果配方需要特定的热量条件，此方法将尝试渲染满足该条件的热源。
     * 支持静态展示和轮播展示。
     *
     * @param graphics GUI 图形上下文
     * @param recipeSlotsView 配方槽位视图
     * @param recipe 处理配方
     * @param x 绘制位置 X 坐标
     * @param y 绘制位置 Y 坐标
     * @return 如果成功绘制了自定义热源返回 true，否则返回 false
     */
    public static boolean drawCustomHeatSource(GuiGraphics graphics, IRecipeSlotsView recipeSlotsView, ProcessingRecipe recipe, int x, int y) {
        HeatCondition heatCondition = recipe.getRequiredHeat();

        if (heatCondition == HeatCondition.NONE) {
            return false;
        }

        HeatManager heatManager = HeatManager.getInstance();
        if (!heatManager.hasHeatCondition(heatCondition)) {
            return false;
        }

        BlazeBurnerBlock.HeatLevel heatLevel = heatManager.getHeatLevel(heatCondition);
        if (heatLevel == null) {
            return false;
        }

        HeatData heatData = heatManager.getHeatData(heatLevel);
        if (heatData == null) {
            return false;
        }

        if (!HeatSourceRendererHelper.hasRenderableHeatSource(heatData)) {
            return false;
        }

        BlockState displayState = null;
        if (recipeSlotsView != null) {
            displayState = recipeSlotsView.findSlotByName("createheatjs:heat_source")
                    .flatMap(IRecipeSlotView::getDisplayedItemStack)
                    .map(HeatSourceRendererHelper::getDisplayStateFromHeatSourceSlotItem)
                    .orElse(null);
        }
        if (displayState == null) {
            displayState = HeatSourceRendererHelper.getCarouselDisplayBlockState(heatData);
        }
        if (displayState == null) {
            return false;
        }

        if (displayState.getBlock() == AllBlocks.BLAZE_BURNER.get()) {
            new AnimatedBlazeBurner()
                    .withHeat(heatLevel)
                    .draw(graphics, x, y);
        } else {
            AnimatedBlockHeatSource customHeatSource = new AnimatedBlockHeatSource()
                    .withBlockState(displayState)
                    .withYOffset(1.65f);

            customHeatSource.draw(graphics, x, y);
        }
        return true;
    }

    /**
     * 设置自定义热源配方布局
     * 为 JEI 配方布局添加热源槽位，展示满足配方热量条件的热源物品。
     *
     * @param builder 配方布局构建器
     * @param recipe 处理配方
     * @return 如果成功设置了热源槽位返回 true，否则返回 false
     */
    public static boolean setCustomHeatSourceRecipe(IRecipeLayoutBuilder builder, ProcessingRecipe recipe) {
        HeatCondition requiredHeat = recipe.getRequiredHeat();
        HeatManager heatManager = HeatManager.getInstance();
        if (!heatManager.hasHeatCondition(requiredHeat)) {
            return false;
        }

        if (requiredHeat == HeatCondition.NONE) {
            return false;
        }

        BlazeBurnerBlock.HeatLevel heatLevel = heatManager.getHeatLevel(requiredHeat);
        if (heatLevel == null) {
            return false;
        }

        HeatData heatData = heatManager.getHeatData(heatLevel);
        if (heatData == null) {
            return false;
        }

        List<ItemStack> heatSourceStacks;
        ItemStack configuredHeatSource = heatData.getHeatSourceDisplayItem();
        if (configuredHeatSource != null && !configuredHeatSource.isEmpty()) {
            heatSourceStacks = List.of(configuredHeatSource);
        } else {
            heatSourceStacks = HeatSourceRendererHelper.getHeatSourceSlotItemStacks(heatData);
        }

        BlockState displayState = HeatSourceRendererHelper.getDisplayBlockState(heatData);
        if (heatSourceStacks.isEmpty() && displayState == null) {
            return false;
        }

        if (!heatSourceStacks.isEmpty()) {
            if (heatSourceStacks.size() == 1) {
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81)
                        .setSlotName("createheatjs:heat_source")
                        .addItemStack(heatSourceStacks.get(0));
            } else {
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81)
                        .setSlotName("createheatjs:heat_source")
                        .addItemStacks(heatSourceStacks);
            }
        } else {
            AnimatedBlockHeatSource animatedHeatSource = new AnimatedBlockHeatSource().withBlockState(displayState);
            ItemStack heatSourceSlotItem = animatedHeatSource.getHeatSourceSlotItem();
            if (heatSourceSlotItem != null && !heatSourceSlotItem.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81)
                        .setSlotName("createheatjs:heat_source")
                        .addItemStack(heatSourceSlotItem);
            }
        }

        ItemStack catalystItem = null;
        ItemStack configuredCatalyst = heatData.getCatalystDisplayItem();
        if (configuredCatalyst != null && !configuredCatalyst.isEmpty()) {
            catalystItem = configuredCatalyst;
        } else if (heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING) {
            catalystItem = AllItems.BLAZE_CAKE.asStack();
        }

        if (catalystItem != null && !catalystItem.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 153, 81).addItemStack(catalystItem);
        }
        return true;
    }
}
