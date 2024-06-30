package com.xiaohunao.createheatjs.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.MixingCategory;
import com.simibubi.create.compat.jei.category.PackingCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.createheatjs.util.BlockHelper;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {MixingCategory.class, PackingCategory.class}, remap = false)
public abstract class MixingAndPackingCategoryMixin extends BasinCategory {
    public MixingAndPackingCategoryMixin(Info<BasinRecipe> info, boolean needsHeating) {
        super(info, needsHeating);
    }

    @WrapOperation(
            method = "draw(Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/compat/jei/category/animations/AnimatedBlazeBurner;draw(Lnet/minecraft/client/gui/GuiGraphics;II)V"
            )
    )
    private void createheatjs$replaceDrawCallIfCustom(AnimatedBlazeBurner instance, GuiGraphics graphics, int xOffset, int yOffset, Operation<Void> original, @Local HeatCondition requiredHeat) {
        if (!BlockHelper.hetaSourceRender(graphics, getBackground(), requiredHeat)) {
            original.call(instance, graphics, xOffset, yOffset);
        }
    }

}