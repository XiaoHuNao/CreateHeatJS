package com.xiaohunao.createheatjs.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.xiaohunao.createheatjs.HeatData;
import com.xiaohunao.createheatjs.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BasinRecipe.class, remap = false)
public class BasinRecipeMixin {

    @Inject(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z"
            , at = @At(value = "INVOKE"
            , target = "Lcom/simibubi/create/content/processing/recipe/HeatCondition;testBlazeBurner(Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;)Z"
            , shift = At.Shift.AFTER)
            , cancellable = true
    )
    private static void createheatjs$checkForCustomHeatLevels(BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir) {
        Level level = basin.getLevel();
        if (level != null) {
            BlockPos blockPos = basin.getBlockPos().below(1);
            BlockState blockState = level.getBlockState(blockPos);

            if (recipe instanceof BasinRecipe basinRecipe){
                String heatCondition = basinRecipe.getRequiredHeat().toString();
                HeatData heatData = HeatData.getHeatData(heatCondition);

                heatData.getHeatSource().forEach((stack, predicate) -> {
                    String stackFirst = stack.getFirst();
                    TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(stackFirst));
                    if(stackFirst.equals("*") || blockState.is(blockTagKey) || blockState.is(BlockHelper.parseBlockState(stackFirst).getBlock())){
                        if (predicate.test(level, blockPos, blockState)) {
                            return;
                        }
                    }
                    cir.setReturnValue(false);
                });
            }
        }
    }
}
