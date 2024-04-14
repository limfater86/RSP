package org.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.core.IBattleService;
import org.example.net.AcceptConnectionHandler;
import org.example.net.Decoder;
import org.example.net.Encoder;

import java.net.Inet4Address;
import java.util.concurrent.TimeUnit;

import static org.example.Config.*;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class Server {
    private boolean isReady = false;

    public void run(IBattleService battleService) throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup(BOS_THREADS);
        EventLoopGroup workerGroup = new NioEventLoopGroup(WORKER_THREADS);

        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup);
            boot.channel(NioServerSocketChannel.class);

            boot.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("acceptor", new AcceptConnectionHandler(Server.this));
                    ch.pipeline().addLast("idleState", new IdleStateHandler(CONNECTION_IDLE_TIMEOUT_SECONDS, 0, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast("encoder", new Encoder());
                    ch.pipeline().addLast("decoder", new Decoder(new ServerProcessor(battleService)));
                }
            });

            boot.childOption(ChannelOption.SO_KEEPALIVE, true);
            boot.childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture future = boot.bind(Inet4Address.getByName(SERVER_ADDRESS), SERVER_PORT);

            setReady(true);

            future.sync();

            while (isReady){

            }

            future.channel().closeFuture();

            setReady(false);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully(1, 5, TimeUnit.MILLISECONDS).sync();
            bossGroup.shutdownGracefully(1, 5, TimeUnit.SECONDS).sync();
        }
    }

    /**
     * Установить флаг готовности сервера принимать соединения
     *
     * @param isReady
     */
    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    /**
     * Готов ли сервер принимать соединения, прошла ли инициализацию
     *
     * @return
     */
    public boolean isReady() {
        return isReady;
    }
}
