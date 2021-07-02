package phonis.cannontracer.networking;

import net.minecraft.network.INetHandler;
import phonis.cannontracer.state.CTLineManager;
import phonis.cannontracer.state.CTState;
import phonis.cannontracer.CannonTracerMod;

import java.io.*;

public class CTChannel extends PluginChannel {

    public static CTChannel instance;

    public CTChannel(String name) {
        super(name);
    }

    public static void initialize() {
        CTChannel.instance = new CTChannel(CannonTracerMod.channelName);
    }

    @Override
    public void onMessage(byte[] in, INetHandler netHandler) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(in));
            CTPacket packet = (CTPacket) ois.readObject();

            this.handlePacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(CTPacket packet) {
        if (packet instanceof CTUnsupported) {
            System.out.println("Unsupported: " + ((CTUnsupported) packet).protocolVersion);
        } else if (packet instanceof CTSetWorld) {
            CTState.currentWorld = ((CTSetWorld) packet).world;
        } else if (packet instanceof CTNewLines) {
            CTNewLines ctNewLines = (CTNewLines) packet;

            CTLineManager.instance.addLines(ctNewLines.lines);
        } else if (packet instanceof CTClear) {
            CTClear ctClear = (CTClear) packet;

            CTLineManager.instance.clearByType(ctClear.type);
        } else {
            System.out.println("Unrecognised packet.");
        }
    }

    public void send(CTPacket packet) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(packet);
            oos.flush();
            this.sendToServer(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
