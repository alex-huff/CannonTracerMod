package phonis.cannontracer.listeners;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import phonis.cannontracer.CannonTracerMod;
import phonis.cannontracer.networking.CTChannel;
import phonis.cannontracer.networking.CTRegister;
import phonis.cannontracer.state.CTLineManager;

import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionLifeCycle {

    private volatile boolean started = false;
    private final AtomicInteger ticks = new AtomicInteger(0);

    @SubscribeEvent
    public void playerJoinEvent(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        this.started = true;

        this.ticks.set(0);
    }

    @SubscribeEvent
    public void playerQuitEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        this.started = false;

        this.ticks.set(0);
        CTLineManager.instance.clearByType(null); // clear all
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (this.started) {
            if (this.ticks.get() == 20) { // delay it a little before handshake, quite horrible a way to do this
                CTChannel.instance.send(new CTRegister(CannonTracerMod.protocolVersion));
                this.ticks.set(0);

                started = false;
            } else {
                this.ticks.getAndIncrement();
            }
        } else {
            CTLineManager.instance.onTick();
        }
    }

}
