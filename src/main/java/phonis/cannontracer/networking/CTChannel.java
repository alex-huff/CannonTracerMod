package phonis.cannontracer.networking;

import net.minecraft.network.INetHandler;
import phonis.cannontracer.CannonTracerMod;
import phonis.cannontracer.state.CTLineManager;
import phonis.cannontracer.state.CTState;

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
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(in));

        try {
            byte packetId = dis.readByte();

            switch (packetId) {
                case Packets.registerID:
                    this.handlePacket(CTRegister.fromBytes(dis));

                    break;
                case Packets.unsupportedID:
                    this.handlePacket(CTUnsupported.fromBytes(dis));

                    break;
                case Packets.newLinesID:
                    this.handlePacket(CTNewLines.fromBytes(dis));

                    break;
                case Packets.newArtifactsID:
                    this.handlePacket(CTNewArtifacts.fromBytes(dis));

                    break;
                case Packets.clearID:
                    this.handlePacket(CTClear.fromBytes(dis));

                    break;
                case Packets.setWorldID:
                    this.handlePacket(CTSetWorld.fromBytes(dis));

                    break;
                default:
                    System.out.println("Unrecognised packet.");

                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(CTPacket packet) {
        if (packet instanceof CTUnsupported) {
            System.out.println("Unsupported: " + ((CTUnsupported) packet).protocolVersion);
        } else if (packet instanceof CTSetWorld) {
            CTState.currentWorld = ((CTSetWorld) packet).world;
        } else if (packet instanceof CTNewLines) {
            CTNewLines ctNewLine = (CTNewLines) packet;

            CTLineManager.instance.addLines(ctNewLine);
        } else if (packet instanceof CTNewArtifacts) {
            CTNewArtifacts ctNewArtifacts = (CTNewArtifacts) packet;

            CTLineManager.instance.addArtifacts(ctNewArtifacts);
        } else if (packet instanceof CTClear) {
            CTClear ctClear = (CTClear) packet;

            CTLineManager.instance.clearByType(ctClear.type);
        } else {
            System.out.println("Unrecognised packet.");
        }
    }

    public void send(CTPacket packet) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeByte(packet.packetID());
            packet.toBytes(dos);
            this.sendToServer(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
