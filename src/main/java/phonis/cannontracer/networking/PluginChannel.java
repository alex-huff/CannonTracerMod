package phonis.cannontracer.networking;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.EnumMap;

// adapted from sirati97

public abstract class PluginChannel {

    private final String name;
    private final EnumMap<Side, FMLEmbeddedChannel> channels;

    public PluginChannel(String name) {
        this.name = name;
        channels = NetworkRegistry.INSTANCE.newChannel(name, new PluginChannelInboundHandler(this));
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

    public void sendToServer(byte[] out) {
        FMLProxyPacket message = encode(out);

        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public String getName() {
        return name;
    }

    public abstract byte[] onMessage(byte[] in, INetHandler netHandler);

    public FMLProxyPacket encode(byte[] out) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

        buffer.writeBytes(out);

        return new FMLProxyPacket(buffer, this.getName());
    }

    public byte[] decode(FMLProxyPacket msg) {
        ByteBuf buf = msg.payload().copy();
        byte[] stream = new byte[buf.array().length-buf.arrayOffset()];

        buf.readBytes(stream);

        return stream;
    }

}