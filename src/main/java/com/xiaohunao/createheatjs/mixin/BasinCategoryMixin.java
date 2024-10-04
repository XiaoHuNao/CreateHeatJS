package com.xiaohunao.createheatjs.mixin;

import com.negodya1.vintageimprovements.compat.jei.category.PressurizingCategory;
import com.negodya1.vintageimprovements.compat.jei.category.VacuumizingCategory;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import fr.lucreeper74.createmetallurgy.compat.jei.category.AlloyingCategory;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.fluids.FluidStack;
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
        if (recipe.getRequiredHeat().name().equals("LOWHEATED")) {
            int size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
            int i = 0;

            for (ProcessingOutput result : recipe.getRollableResults()) {
                int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
                int yPosition = -19 * (i / 2) + 51;

                builder
                        .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                        .setBackground(getRenderedSlot(result), -1, -1)
                        .addItemStack(result.getStack())
                        .addTooltipCallback(addStochasticTooltip(result));
                i++;
            }

            for (FluidStack fluidResult : recipe.getFluidResults()) {
                int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
                int yPosition = -19 * (i / 2) + 51;

                builder
                        .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                        .setBackground(getRenderedSlot(), -1, -1)
                        .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(fluidResult))
                        .addTooltipCallback(addFluidTooltip(fluidResult.getAmount()));
                i++;
            }
            ci.cancel();
        }
    }
}
