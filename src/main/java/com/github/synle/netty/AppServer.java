package com.github.synle.netty;

import com.github.synle.netty.data.TodoList;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class AppServer {

    private static final int HTTP_PORT = 8080;


    public void run() throws Exception {
        System.out.println("Server Started: " + HTTP_PORT);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // set up some mocked data
            TodoList.addTodo("Todo 0", "incomplete");
            TodoList.addTodo("Todo 1", "complete");
            TodoList.addTodo("Todo 2", "complete");
            TodoList.addTodo("Todo 3", "incomplete");

            ServerBootstrap httpBootstrap = new ServerBootstrap();

            httpBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitializer(workerGroup.next()))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture httpChannel = httpBootstrap.bind(HTTP_PORT).sync();

            // Wait until the server socket is closed
            httpChannel.channel().closeFuture().sync();
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new AppServer().run();
    }

}
