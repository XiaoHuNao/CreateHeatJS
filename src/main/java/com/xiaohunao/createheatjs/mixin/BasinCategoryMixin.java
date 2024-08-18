package com.xiaohunao.createheatjs.mixin;

import com.negodya1.vintageimprovements.compat.jei.category.PressurizingCategory;
import com.negodya1.vintageimprovements.compat.jei.category.VacuumizingCategory;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {BasinCategory.class, PressurizingCategory.class, VacuumizingCategory.class}, remap = false)
public abstract class BasinCategoryMixin extends CreateRecipeCategory<BasinRecipe> {
    public BasinCategoryMixin(Info<BasinRecipe> info) {
        super(info);
    }

    @Inject(
            method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At(
                    value = "INVOKE",
                    ordinal = 0,
                    target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;getRequiredHeat()Lcom/simibubi/create/content/processing/recipe/HeatCondition;"),
            cancellable = true
    )
    private void createheatjs$cancelHeatLevelIngredients(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(
            method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At(
                    value = "INVOKE",
                    ordinal = 0,
                    target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;getRollableResults()Ljava/util/List;"),
            cancellable = true
    )
    private void createheatjs$cancelLOWHeatLevelIngredients(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
        if (CreateHeatJS.LOW_ACTIVE) {
            ci.cancel();
        }
    }

    @Inject(method = "draw(Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
            at = @At(value = "RETURN")
    )
    private void createheatjs$drawJEITip(BasinRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY, CallbackInfo ci) {
        HeatCondition heatCondition = recipe.getRequiredHeat();
        BlazeBurnerBlock.HeatLevel orDefault = CreateHeatJS.heatMap.inverse().getOrDefault(heatCondition, null);
        if (orDefault != null && orDefault != BlazeBurnerBlock.HeatLevel.NONE) {
            HeatData heatData = CreateHeatJS.heatDataMapByLevel.get(orDefault);
            if (heatData.hasJeiTip()) {
                graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.create.jei.category.basin.heat." + heatCondition.getTranslationKey() + ".title"),
                        9, 0, heatData.getColor(), false);
            }
        }
    }
}
