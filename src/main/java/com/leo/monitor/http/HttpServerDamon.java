package com.leo.monitor.http;

import com.leo.monitor.Damon;
import com.leo.monitor.config.HttpMethod;
import com.leo.monitor.handler.LogHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.HttpHeaderNames;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;

import java.util.Objects;

/**
 * http server
 *
 * @author zhangxinlei
 * @date 2022-11-04
 */
public class HttpServerDamon implements Damon {

    private final HttpClientDamon httpClientDamon;

    public HttpServerDamon(HttpClientDamon httpClientDamon) {
        this.httpClientDamon = httpClientDamon;
    }

    @Override
    public void start(String ip, int port) {
        var server = HttpServer.create()
                .host(ip)
                .port(port)
                .doOnConnection(connection -> System.out.println(connection.channel()))
                .handle((request, response) -> {
                    HttpClient.ResponseReceiver<?> receiver =
                            httpClientDamon.send(HttpMethod.valueOf(request.method().name()), request.uri(), request);
                    if (!Objects.isNull(receiver)) {
                        return receiver.response((httpClientResponse, byteBufFlux) -> response.status(httpClientResponse.status()).headers(httpClientResponse.responseHeaders()).send(
                                byteBufFlux.retain().buffer().map(byteBufList -> {
                                    int length = response.responseHeaders().getInt(HttpHeaderNames.CONTENT_LENGTH);
                                    ByteBuf byteBuf = new PooledByteBufAllocator().buffer(length);
                                    for (ByteBuf value : byteBufList) {
                                        byte[] buf = new byte[value.capacity()];
                                        value.readBytes(buf);
                                        byteBuf.writeBytes(buf);
                                    }
                                    LogHandler.logResponse(httpClientResponse, byteBuf);
                                    return byteBuf;
                                })
                        ));
                    }
                    return response.sendString(Mono.just("error"));
                });
        System.out.println("????????????");
        server.bindNow()
                .onDispose()
                .block();
    }
}
