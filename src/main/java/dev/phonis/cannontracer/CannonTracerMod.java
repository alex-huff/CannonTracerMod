package dev.phonis.cannontracer;

import dev.phonis.cannontracer.networking.CTLineType;
import dev.phonis.cannontracer.networking.CTPacket;
import dev.phonis.cannontracer.networking.CTReceiver;
import dev.phonis.cannontracer.networking.CTRegister;
import dev.phonis.cannontracer.state.CTLineManager;
import dev.phonis.cannontracer.state.CTState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public
class CannonTracerMod implements ClientModInitializer
{

    public static final int        protocolVersion = 1;
    public static final Identifier cTIdentifier    = new Identifier("cannontracer:main");


    @Override
    public
    void onInitializeClient()
    {
        ClientPlayNetworking.registerGlobalReceiver(CannonTracerMod.cTIdentifier, CTReceiver.INSTANCE);
        C2SPlayChannelEvents.REGISTER.register((clientPlayNetworkHandler, packetSender, minecraftClient, ids) ->
        {
            for (Identifier id : ids)
            {
                if (id.equals(CannonTracerMod.cTIdentifier))
                {
                    try
                    {
                        if (Thread.currentThread().getName().equals("Render thread"))
                        {
                            clientPlayNetworkHandler.sendPacket(ClientPlayNetworking.createC2SPacket(CannonTracerMod.cTIdentifier,
                                CannonTracerMod.packetToByteBuf(new CTRegister(CannonTracerMod.protocolVersion))));
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) ->
        {
            CTLineManager.instance.clearByType(CTLineType.ALL);

            CTState.currentWorld = null;
        });
    }

    public static
    void sendPacket(CTPacket packet)
    {
        try
        {
            ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();

            if (handler == null)
            {
                return;
            }

            handler.sendPacket(
                ClientPlayNetworking.createC2SPacket(CannonTracerMod.cTIdentifier, CannonTracerMod.packetToByteBuf(packet)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static
    PacketByteBuf packetToByteBuf(CTPacket packet) throws IOException
    {
        ByteArrayOutputStream baos         = new ByteArrayOutputStream();
        DataOutputStream      das          = new DataOutputStream(baos);
        PacketByteBuf         packetBuffer = PacketByteBufs.create();

        das.writeByte(packet.packetID());
        packet.toBytes(das);
        das.close();
        packetBuffer.writeBytes(baos.toByteArray());

        return packetBuffer;
    }

}
