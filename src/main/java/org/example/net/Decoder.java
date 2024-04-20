package org.example.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.ExceptionMessage.*;
import static org.example.Messages.ON_CONNECT_MESSAGE;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class Decoder extends ChannelInboundHandlerAdapter {
    private final IDecoderProcessor processor;
    private ByteBuf buffer = null;
    private final Pattern pattern;


    public Decoder(IDecoderProcessor processor) {
        this.processor = processor;
        pattern = Pattern.compile("[^a-zA-Z0-9_.]");
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
        if (ctx == null) throw new IllegalArgumentException(DECODER_READ_CTX_ERROR_MESSAGE);
        if (msg == null) throw new IllegalArgumentException(DECODER_READ_MSG_ERROR_MESSAGE);
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
        if (ctx == null) throw new IllegalArgumentException(DECODER_REGISTER_CTX_ERROR_MESSAGE);

        if (buffer.isReadable()) {
            boolean result = parseData(ctx);
            if (result) buffer.clear();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (ctx == null) throw new IllegalArgumentException(DECODER_READ_COMPLETE_CTX_ERROR_MESSAGE);
        this.processor.registerChannel(ctx.channel());
        this.processor.sendMessage(ON_CONNECT_MESSAGE);
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

            String message = new String(buf.array(), StandardCharsets.UTF_8);
            buf.release();

            Matcher matcher = pattern.matcher(message);
            String result = matcher.replaceAll("");

            if (result.isEmpty()) return true;

            processor.process(result, ctx.channel());

        }
        return true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.processor.release();
    }
}
