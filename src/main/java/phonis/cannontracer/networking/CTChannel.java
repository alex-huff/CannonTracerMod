package phonis.cannontracer.networking;

import net.minecraft.network.INetHandler;
import phonis.cannontracer.CannonTracerMod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class CTChannel extends PluginChannel {

    public static final CTChannel instance = new CTChannel(CannonTracerMod.channelName);

    public CTChannel(String name) {
        super(name);
    }

    @Override
    public byte[] onMessage(byte[] in, INetHandler netHandler) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(in));
            CTPacket packet = (CTPacket) ois.readObject();

            this.handlePacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void handlePacket(CTPacket packet) {
        if (packet instanceof CTUnsupported) {
            CTUnsupported rtUnsupported = (CTUnsupported) packet;

            System.out.println("Unsupported: " + rtUnsupported.protocolVersion);
        } else {
            System.out.println("Unrecognised packet.");
        }
    }

}
