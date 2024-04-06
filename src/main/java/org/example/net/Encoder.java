package org.example.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class Encoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (ctx == null) throw new IllegalArgumentException("Encoder: write: argument ctx was null");
        if (msg == null) throw new IllegalArgumentException("Encoder: write: argument msg was null");

        ctx.writeAndFlush(msg);
    }

}
