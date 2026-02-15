package com.xiaohunao.create_heat_js.common.event;

import com.mojang.logging.LogUtils;
import com.xiaohunao.create_heat_js.HeatJSKubeJSPlugin;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;


public class CommonEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void onAddReloadListener(AddReloadListenerEvent event) {
        LOGGER.info("CreateHeatJS: Triggering KubeJS heat registration");
        HeatJSKubeJSPlugin.REGISTRY_HEAT.post(new RegisterHeatEvent());
    }


}
