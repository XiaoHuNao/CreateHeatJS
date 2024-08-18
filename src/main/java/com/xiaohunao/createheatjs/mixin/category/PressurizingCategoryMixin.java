package com.xiaohunao.createheatjs.mixin.category;

import com.negodya1.vintageimprovements.compat.jei.category.PressurizingCategory;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedVacuumChamber;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.xiaohunao.createheatjs.mixed.DrawableAccessor;
import mezz.jei.api.gui.drawable.IDrawable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PressurizingCategory.class, remap = false)
public abstract class PressurizingCategoryMixin extends BasinCategory implements DrawableAccessor {
    @Shadow @Final private AnimatedVacuumChamber vacuum;

    public PressurizingCategoryMixin(Info<BasinRecipe> info, boolean needsHeating) {
        super(info, needsHeating);
    }

    @Override
    public IDrawable createHeatJS$getDrawable() {
        return vacuum;
    }
}
