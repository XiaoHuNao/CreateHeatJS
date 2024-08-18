package com.xiaohunao.createheatjs.mixin.category;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.xiaohunao.createheatjs.mixed.DrawableAccessor;
import fr.lucreeper74.createmetallurgy.compat.jei.category.MeltingCategory;
import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.FoundryTopElement;
import mezz.jei.api.gui.drawable.IDrawable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MeltingCategory.class, remap = false)
public abstract class MeltingCategoryMixin extends BasinCategory implements DrawableAccessor {
    @Shadow @Final private FoundryTopElement castingtop;

    public MeltingCategoryMixin(Info<BasinRecipe> info, boolean needsHeating) {
        super(info, needsHeating);
    }

    @Override
    public IDrawable createHeatJS$getDrawable() {
        return castingtop;
    }
}
