package com.xiaohunao.createheatjs.mixin.category;


import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.PackingCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.xiaohunao.createheatjs.mixed.DrawableAccessor;
import mezz.jei.api.gui.drawable.IDrawable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PackingCategory.class, remap = false)
public abstract class PackingCategoryMixin extends BasinCategory implements DrawableAccessor {
    @Shadow @Final private AnimatedPress press;

    public PackingCategoryMixin(Info<BasinRecipe> info, boolean needsHeating) {
        super(info, needsHeating);
    }

    @Override
    public IDrawable createHeatJS$getDrawable() {
        return press;
    }
}
