package com.xiaohunao.create_heat_js.common.event;

import com.mojang.logging.LogUtils;
import com.xiaohunao.create_heat_js.common.HeatManager;
import com.xiaohunao.create_heat_js.HeatJSKubeJSPlugin;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;


@Mod.EventBusSubscriber
public class CommonEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND) {
            return;
        }

    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        LOGGER.info("CreateHeatJS: Triggering KubeJS heat registration");
        HeatJSKubeJSPlugin.REGISTRY_HEAT.post(new RegisterHeatEvent());
    }


}
