package com.xiaohunao.create_heat_js.client.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * 通用的方块热源动画类，用于在 JEI 中显示自定义热源方块
 * 可以替换 AnimatedBlazeBurner 来显示任意方块作为热源
 */
@ParametersAreNonnullByDefault
public class AnimatedBlockHeatSource {

    private BlockState blockState;
    private float yOffset = 1.65f;
    @Nullable
    private ItemStack heatSourceSlotItem; // 热源槽物品（默认从 BlockState 获取）
    @Nullable
    private ItemStack catalystSlotItem; // 催化物物品（默认 null，隐藏槽位）


    public AnimatedBlockHeatSource withBlockState(BlockState blockState) {
        this.blockState = blockState;
        return this;
    }

    public AnimatedBlockHeatSource withYOffset(float yOffset) {
        this.yOffset = yOffset;
        return this;
    }

    /**
     * 设置热源槽物品
     * 如果不设置，将默认从 BlockState 获取对应的 ItemStack
     * @param itemStack 热源槽物品，如果为 null 则使用默认值（从 BlockState 获取）
     * @return 返回自身以支持链式调用
     */
    public AnimatedBlockHeatSource withHeatSourceSlotItem(@Nullable ItemStack itemStack) {
        this.heatSourceSlotItem = itemStack;
        return this;
    }

    /**
     * 设置催化物物品
     * 如果不设置或设置为 null，则隐藏催化物槽位
     * @param itemStack 催化物物品，如果为 null 则隐藏槽位
     * @return 返回自身以支持链式调用
     */
    public AnimatedBlockHeatSource withCatalystSlotItem(@Nullable ItemStack itemStack) {
        this.catalystSlotItem = itemStack;
        return this;
    }


    @Nullable
    public ItemStack getHeatSourceSlotItem() {
        if (heatSourceSlotItem != null) {
            return heatSourceSlotItem;
        }
        
        // 如果未设置，从 BlockState 获取
        if (blockState != null) {
            Block block = blockState.getBlock();
            Item item = block.asItem();
            if (item != Items.AIR) {
                return new ItemStack(item);
            }
        }
        
        return null;
    }


    @Nullable
    public ItemStack getCatalystSlotItem() {
        return catalystSlotItem;
    }


    public boolean shouldShowCatalystSlot() {
        return catalystSlotItem != null && !catalystSlotItem.isEmpty();
    }

    public void draw(@Nonnull GuiGraphics graphics, int xOffset, int yOffset) {
        if (blockState == null) {
            return;
        }

        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        GuiGameElement.of(blockState)
                .atLocal(0d, (double) this.yOffset, 0d)
                .scale(23d)
                .render(graphics);

        matrixStack.popPose();
    }
}
