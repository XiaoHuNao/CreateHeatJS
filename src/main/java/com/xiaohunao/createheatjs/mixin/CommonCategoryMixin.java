package com.xiaohunao.createheatjs.mixin;

import com.negodya1.vintageimprovements.compat.jei.category.PressurizingCategory;
import com.negodya1.vintageimprovements.compat.jei.category.VacuumizingCategory;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.MixingCategory;
import com.simibubi.create.compat.jei.category.PackingCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.createheatjs.AnimatedHeatSourceBlock;
import com.xiaohunao.createheatjs.CreateHeatJS;
import com.xiaohunao.createheatjs.HeatData;
import com.xiaohunao.createheatjs.mixed.DrawableAccessor;
import fr.lucreeper74.createmetallurgy.compat.jei.category.AlloyingCategory;
import fr.lucreeper74.createmetallurgy.compat.jei.category.MeltingCategory;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = {MixingCategory.class, PackingCategory.class, AlloyingCategory.class, MeltingCategory.class, PressurizingCategory.class,PressurizingCategory.class, VacuumizingCategory.class}, remap = false)
public abstract class CommonCategoryMixin extends BasinCategory {
    public CommonCategoryMixin(Info<BasinRecipe> info, boolean needsHeating) {
        super(info, needsHeating);
    }


    @Unique
    private static final AnimatedHeatSourceBlock animatedHeatSourceBlock = new AnimatedHeatSourceBlock();

    @Inject(
            method = "draw(Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;getRequiredHeat()Lcom/simibubi/create/content/processing/recipe/HeatCondition;"
            ),
            cancellable = true
    )
    private void createheatjs$replaceDrawCallIfCustom(BasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY, CallbackInfo ci) {
        HeatCondition heatCondition = recipe.getRequiredHeat();

        BlazeBurnerBlock.HeatLevel orDefault = CreateHeatJS.heatMap.inverse().getOrDefault(heatCondition, null);
        if (orDefault != null && orDefault != BlazeBurnerBlock.HeatLevel.NONE) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) {
                return;
            }
            long dayTime = level.getDayTime();

            HeatData heatData = CreateHeatJS.heatDataMapByLevel.get(orDefault);
            List<Block> heatSourceBlocks = heatData.getHeatSourceBlocks();
            if (heatSourceBlocks.size() == 0){
                return;
            }
            int itemIndexToShow = (int) ((dayTime / 25) % (heatSourceBlocks.size()));
            Block block = heatSourceBlocks.get(itemIndexToShow);
            animatedHeatSourceBlock.init(orDefault,block).draw(graphics, getBackground().getWidth() / 2 + 3, 55);
            DrawableAccessor drawableAccessor = (DrawableAccessor) this;
            drawableAccessor.createHeatJS$getDrawable().draw(graphics, getBackground().getWidth() / 2 + 3, 34);
            ci.cancel();
        }

    }

    @Inject(method = "draw(Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V"
            ,at = @At(value = "HEAD")
    )
    private void createheatjs$drawJEITip(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY, CallbackInfo ci) {
        HeatCondition requiredHeat = recipe.getRequiredHeat();
        HeatData heatData = CreateHeatJS.heatDataMapByLevel.get(CreateHeatJS.heatMap.inverse().get(requiredHeat));
        if (heatData.canShowJeiTip()) {
            graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.create.jei.category.basin.heat." + requiredHeat.serialize() + ".title"),
                    9, 0, heatData.getColor(), false);
        }
    }

}