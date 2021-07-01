package phonis.cannontracer.networking;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.apache.logging.log4j.Level;

// adapted from sirati97

@ChannelHandler.Sharable
public class PluginChannelInboundHandler extends SimpleChannelInboundHandler<FMLProxyPacket> {

    private final PluginChannel pluginChannel;

    public PluginChannelInboundHandler(PluginChannel pluginChannel) {
        this.pluginChannel = pluginChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FMLProxyPacket msg) {
        INetHandler iNetHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        byte[] out = pluginChannel.onMessage(pluginChannel.decode(msg), iNetHandler);

        if (out != null) {
            FMLProxyPacket result = pluginChannel.encode(out);

            ctx.channel().attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.REPLY);
            ctx.writeAndFlush(result).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FMLLog.log(Level.ERROR, cause, "PluginChannelInboundHandler exception");
        super.exceptionCaught(ctx, cause);
    }

}