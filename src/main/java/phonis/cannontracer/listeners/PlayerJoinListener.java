package phonis.cannontracer.listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import phonis.cannontracer.CannonTracerMod;
import phonis.cannontracer.networking.CTChannel;
import phonis.cannontracer.networking.CTRegister;

// adapted from hoxosse's WorldEditCUI-Forge-Edition

public class PlayerJoinListener {

    private static int handShakeTask = 0;
    private static int handShakeTimer = 0;

    private WorldClient lastWorld = null;
    private boolean handShake = false;

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if(event.entity == null) return;

        if(!(event.entity instanceof EntityPlayer)) return;

        if(lastWorld != Minecraft.getMinecraft().theWorld)
            this.onWorldChange();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if(handShakeTask == 1) {
            handShakeTimer++;

            if(handShakeTimer == 20) {
                this.lastWorld = Minecraft.getMinecraft().theWorld;

                CTChannel.instance.send(new CTRegister(CannonTracerMod.protocolVersion));

                handShakeTask = 0;
                handShakeTimer = 0;
            }
        }
    }

    private void onWorldChange() {
        PlayerJoinListener.handShakeTask = 1;
        PlayerJoinListener.handShakeTimer = 0;
    }

}
