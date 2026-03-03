package com.xiaohunao.create_heat_js.common.mixin.classes.category.createmetallurgy;

import com.xiaohunao.create_heat_js.common.utils.CategoryHelper;
import fr.lucreeper74.createmetallurgy.compat.jei.category.FoundryBasinCategory;
import fr.lucreeper74.createmetallurgy.compat.jei.category.MeltingCategory;
import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.FoundryLidElement;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = MeltingCategory.class , remap = false)
public abstract class MeltingCategoryMixin extends FoundryBasinCategory {
    @Shadow
    @Final
    private FoundryLidElement castingtop;

    public MeltingCategoryMixin(Info<FoundryBasinRecipe> info, boolean needsHeating) {
        super(info, needsHeating);
    }

    @Inject(
            method = "draw(Lfr/lucreeper74/createmetallurgy/content/blocks/foundry_basin/FoundryBasinRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lfr/lucreeper74/createmetallurgy/content/blocks/foundry_basin/FoundryBasinRecipe;getRequiredHeat()Lcom/simibubi/create/content/processing/recipe/HeatCondition;"
            ),
            cancellable = true
    )
    public void chj$draw(FoundryBasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY, CallbackInfo ci) {
        if (!CategoryHelper.drawCustomHeatSource(graphics, iRecipeSlotsView, recipe, getBackground().getWidth() / 2 + 3, 55, getBackground().getWidth(), getBackground().getHeight(), mouseX, mouseY)) {
            return;
        }
        castingtop.draw(graphics, getBackground().getWidth() / 2 + 3, 34);
        ci.cancel();
    }
}
