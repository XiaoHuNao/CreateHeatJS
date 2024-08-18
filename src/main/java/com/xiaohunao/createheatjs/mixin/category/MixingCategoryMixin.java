package com.xiaohunao.createheatjs.mixin.category;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.MixingCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.xiaohunao.createheatjs.mixed.DrawableAccessor;
import mezz.jei.api.gui.drawable.IDrawable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MixingCategory.class, remap = false)
public abstract class MixingCategoryMixin extends BasinCategory  implements DrawableAccessor {
    @Shadow @Final private AnimatedMixer mixer;

    public MixingCategoryMixin(Info<BasinRecipe> info, boolean needsHeating) {
        super(info, needsHeating);
    }

    @Override
    public IDrawable createHeatJS$getDrawable() {
        return mixer;
    }
}
