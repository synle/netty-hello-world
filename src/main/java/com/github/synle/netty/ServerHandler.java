package com.github.synle.netty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.github.synle.netty.data.ToDo;
import com.github.synle.netty.data.TodoList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.FastThreadLocal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// source: https://github.com/kamatama41/netty-sample-http/blob/master/src/main/java/com/kamatama41/netty/sample/http/HelloServerHandler.java
public class ServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final FastThreadLocal<DateFormat> FORMAT = new FastThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        }
    };

    private static final ByteBuf CONTENT_BUFFER = Unpooled.unreleasableBuffer(Unpooled.directBuffer().writeBytes("Hello, World!".getBytes(CharsetUtil.UTF_8)));
    private static final CharSequence contentLength = HttpHeaders.newEntity(String.valueOf(CONTENT_BUFFER.readableBytes()));

    private static final CharSequence TYPE_PLAIN = HttpHeaders.newEntity("text/plain; charset=UTF-8");
    private static final CharSequence TYPE_JSON = HttpHeaders.newEntity("application/json; charset=UTF-8");
    private static final CharSequence SERVER_NAME = HttpHeaders.newEntity("Netty");
    private static final CharSequence CONTENT_TYPE_ENTITY = HttpHeaders.newEntity(HttpHeaders.Names.CONTENT_TYPE);
    private static final CharSequence DATE_ENTITY = HttpHeaders.newEntity(HttpHeaders.Names.DATE);
    private static final CharSequence CONTENT_LENGTH_ENTITY = HttpHeaders.newEntity(HttpHeaders.Names.CONTENT_LENGTH);
    private static final CharSequence SERVER_ENTITY = HttpHeaders.newEntity(HttpHeaders.Names.SERVER);
    private static final ObjectMapper MAPPER;


    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new AfterburnerModule());
    }

    private volatile CharSequence date = HttpHeaders.newEntity(FORMAT.get().format(new Date()));

    ServerHandler(ScheduledExecutorService service) {
        service.scheduleWithFixedDelay(new Runnable() {
            private final DateFormat format = FORMAT.get();

            @Override
            public void run() {
                date = HttpHeaders.newEntity(format.format(new Date()));
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);

    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof HttpRequest) {
                FullHttpRequest request = (FullHttpRequest) msg;
                final String uri = request.uri();
                final HttpMethod method = request.method();

                byte[] json;

                switch (uri) {
                    case "/":
                        writeResponse(ctx, request, CONTENT_BUFFER.duplicate(), TYPE_PLAIN, contentLength);
                        return;

                    case "/todos":
                        json = MAPPER.writeValueAsBytes(TodoList.getAll());
                        writeResponse(ctx, request, Unpooled.wrappedBuffer(json), TYPE_JSON, String.valueOf(json.length));
                        return;

                    case "/todo":
                        if (method == HttpMethod.PUT) {
                            // create
                            ToDo newTodo = MAPPER.readValue(request.content().toString(CharsetUtil.UTF_8), ToDo.class);
                            json = MAPPER.writeValueAsBytes(newTodo);
                            writeResponse(ctx, request, Unpooled.wrappedBuffer(json), TYPE_JSON, String.valueOf(json.length));
                            return;
                        }
                        break;

                    default:
                        if (uri.startsWith("/todo/")) {
                            int todoIndex = Integer.parseInt(uri.replace("/todo/", ""));
                            if (todoIndex > 0) {
                                ToDo targetToDo = TodoList.getTodo(todoIndex);
                                if (method == HttpMethod.GET) {
                                    // get by id
                                    json = MAPPER.writeValueAsBytes(targetToDo);
                                    writeResponse(ctx, request, Unpooled.wrappedBuffer(json), TYPE_JSON, String.valueOf(json.length));
                                    return;
                                }

                                if (method == HttpMethod.POST) {
                                    // update
                                    ToDo newTodo = MAPPER.readValue(request.content().toString(CharsetUtil.UTF_8), ToDo.class);
                                    targetToDo.setMessage(newTodo.getMessage());
                                    targetToDo.setStatus(newTodo.getStatus());
                                    TodoList.setTodo(todoIndex, targetToDo);
                                    json = MAPPER.writeValueAsBytes(targetToDo);
                                    writeResponse(ctx, request, Unpooled.wrappedBuffer(json), TYPE_JSON, String.valueOf(json.length));
                                    return;
                                }
                            }
                        }
                        break;
                }
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, Unpooled.EMPTY_BUFFER, false);
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeResponse(ChannelHandlerContext ctx, HttpRequest request, ByteBuf buf, CharSequence contentType, CharSequence contentLength) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf, false);
        HttpHeaders headers = response.headers();
        headers.set(CONTENT_TYPE_ENTITY, contentType);
        headers.set(SERVER_ENTITY, SERVER_NAME);
        headers.set(DATE_ENTITY, date);
        headers.set(CONTENT_LENGTH_ENTITY, contentLength);

        // Close the non-keep-alive connection after the write operation is
        // done.
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.write(response, ctx.voidPromise());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
