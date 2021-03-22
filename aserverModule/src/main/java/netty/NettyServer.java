package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            /*ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1000, Delimiters.lineDelimiter()));*/
//                            ch.pipeline().addLast(new StringDecoder());
//                            ch.pipeline().addLast(new StringEncoder());
//                            ch.pipeline().addLast(new EchoServerHandler());
                            ch.pipeline().addLast(
                                    new StringDecoder(), // in-1
                                    new StringEncoder(), // out-1
                                    new FileHandler() // in-2
                            );
                        }
                    });

            ChannelFuture f = b.bind(1234).sync();
            System.out.println("Starting netty server at " + f.channel().localAddress());

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}