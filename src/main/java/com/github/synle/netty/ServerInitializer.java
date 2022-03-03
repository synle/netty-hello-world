package com.github.synle.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import java.util.concurrent.ScheduledExecutorService;

public class ServerInitializer extends ChannelInitializer<Channel> {
    private ScheduledExecutorService service;

    public ServerInitializer(ScheduledExecutorService service) {
        this.service = service;
    }

    @Override
    protected void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        pipeline.addLast(new ServerHandler(service));
    }

}
