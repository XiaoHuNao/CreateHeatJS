package com.xiaohunao.create_heat_js.common.mixin.extensions;

import com.simibubi.create.content.processing.recipe.HeatCondition;


public interface HeatConditionExpandAccessor {
    HeatCondition SMOULDERING = chj$create("SMOULDERING", 0x5C93E8);
    HeatCondition FADING = chj$create("FADING", 0x3273C2);

    HeatCondition chj$addEnumValue(String name, int color);

    void chj$removeEnumValue(String name);

    static HeatCondition chj$create(String name, int color) {
        return ((HeatConditionExpandAccessor) (Object) HeatCondition.NONE).chj$addEnumValue(name, color);
    }
}
