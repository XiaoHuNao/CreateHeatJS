package com.xiaohunao.create_heat_js.common.mixin.extensions;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;


public interface HeatLevelExpandAccessor {
    BlazeBurnerBlock.HeatLevel chj$addEnumValue(String name);

    void chj$removeEnumValue(String name);

    static BlazeBurnerBlock.HeatLevel chj$create(String name) {
        return ((HeatLevelExpandAccessor) (Object) BlazeBurnerBlock.HeatLevel.NONE).chj$addEnumValue(name);
    }
}
