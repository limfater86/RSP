package org.example.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class Decoder extends ChannelInboundHandlerAdapter {
    public static final String TAG = Decoder.class.getSimpleName();
    private final IDecoderProcessor processor;
    private ByteBuf buffer = null;
    private final Pattern pattern;


    public Decoder(IDecoderProcessor processor) {
        this.processor = processor;
        pattern = Pattern.compile("^[a-zA-Z0-9_.]{1,30}$");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buffer = Unpooled.buffer();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buffer.release();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx == null) throw new IllegalArgumentException("Decoder: channelRead: argument ctx was null");
        if (msg == null) throw new IllegalArgumentException("Decoder: channelRead: msg ctx was null");
        if (msg instanceof ByteBuf) {
            ByteBuf message = (ByteBuf) msg;
            buffer.writeBytes(message);
            message.release();
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (ctx == null) throw new IllegalArgumentException("Decoder: channelReadComplete: argument ctx was null");

        if (buffer.isReadable()) {
            boolean result = parseData(ctx);
            if (result) buffer.clear();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (ctx == null) throw new IllegalArgumentException("Decoder: channelRegistered: argument ctx was null");
        this.processor.registerChannel(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        this.processor.release();
    }

    /**
     * Производит разбор полученных данных
     *
     * @param ctx
     */
    private boolean parseData(ChannelHandlerContext ctx) {
        while (buffer.isReadable()) {
            int size = buffer.readableBytes();
            ByteBuf buf = Unpooled.buffer(size);
            buf.writeBytes(buffer, size);

            String message = buf.toString();
            Matcher matcher = pattern.matcher(message);
            String result = matcher.replaceAll("");

            processor.process(result, ctx.channel());

        }
        return true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.processor.release();
    }
}
