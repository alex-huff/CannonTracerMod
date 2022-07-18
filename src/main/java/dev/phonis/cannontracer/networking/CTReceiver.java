package dev.phonis.cannontracer.networking;

import dev.phonis.cannontracer.state.CTLineManager;
import dev.phonis.cannontracer.state.CTState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.io.*;

public
class CTReceiver implements ClientPlayNetworking.PlayChannelHandler
{

    public static CTReceiver INSTANCE = new CTReceiver();

    @Override
    public
    void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
                 PacketSender responseSender)
    {

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buf.array()));

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

}
