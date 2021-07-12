package phonis.cannontracer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import phonis.cannontracer.listeners.ClientTracerTick;
import phonis.cannontracer.listeners.ConnectionLifeCycle;
import phonis.cannontracer.networking.CTChannel;
import phonis.cannontracer.render.CTWorldRenderer;

@Mod(modid = CannonTracerMod.MODID, version = CannonTracerMod.VERSION)
public class CannonTracerMod {

    public static final String MODID = "cannontracermod";
    public static final String VERSION = "1.0";
    public static final int protocolVersion = 1;
    public static final String channelName = "cannontracer:main";
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        CTChannel.initialize();
        MinecraftForge.EVENT_BUS.register(new CTWorldRenderer());
        MinecraftForge.EVENT_BUS.register(new ConnectionLifeCycle());
        MinecraftForge.EVENT_BUS.register(new ClientTracerTick());
    }

}
