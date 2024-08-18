package com.xiaohunao.createheatjs.mixin.category;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.xiaohunao.createheatjs.mixed.DrawableAccessor;
import fr.lucreeper74.createmetallurgy.compat.jei.category.AlloyingCategory;
import fr.lucreeper74.createmetallurgy.compat.jei.category.animations.AnimatedFoundryMixer;
import mezz.jei.api.gui.drawable.IDrawable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AlloyingCategory.class, remap = false)
public abstract class AlloyingCategoryMixin extends BasinCategory implements DrawableAccessor {
    @Shadow @Final private AnimatedFoundryMixer mixer;

    @Override
    public IDrawable createHeatJS$getDrawable() {
        return mixer;
    }
    public AlloyingCategoryMixin(Info<BasinRecipe> info, boolean needsHeating) {
        super(info, needsHeating);
    }
}
