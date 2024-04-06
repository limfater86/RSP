package org.example.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import org.example.Server;

import java.net.InetSocketAddress;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class AcceptConnectionHandler extends AbstractRemoteAddressFilter<InetSocketAddress> {

    private Server server;

    public AcceptConnectionHandler(Server server) {
        this.server = server;
    }

    @Override
    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
        return server.isReady();
    }
}
