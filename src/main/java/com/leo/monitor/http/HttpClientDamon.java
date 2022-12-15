package com.leo.monitor.http;

import com.leo.monitor.config.HttpMethod;
import com.leo.monitor.handler.LogHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.HttpHeaderNames;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServerRequest;

import java.util.Objects;

/**
 * http client damon
 *
 * @author zhangxinlei
 * @date 2022-11-07
 */
public class HttpClientDamon {

    private final HttpClient httpClient;

    private String proxy;

    public HttpClientDamon(String proxyHost, Integer proxyPort) {
        this.httpClient = HttpClient.create().doOnConnected(connection -> System.out.println(connection.channel()));
        proxy = proxyHost;
        if (!Objects.isNull(proxyPort)) {
            proxy += ":" + proxyPort;
        }
    }

    public HttpClient.ResponseReceiver<?> send(HttpMethod method, String url, HttpServerRequest request) {
        HttpClient.RequestSender sender = null;
        HttpClient newHttpClient = httpClient.headers(h -> h.add(request.requestHeaders()));
        switch (method) {
            case GET:
                LogHandler.logRequest(request, null);
                return newHttpClient.get().uri(proxy + url);
            case HEAD:
                LogHandler.logRequest(request, null);
                return newHttpClient.head().uri(proxy + url);
            case POST:
                sender = newHttpClient.post();
                break;
            case PUT:
                sender = newHttpClient.put();
                break;
            case PATCH:
                sender = newHttpClient.patch();
                break;
            case DELETE:
                sender = newHttpClient.delete();
                break;
            default:
                break;
        }
        if (!Objects.isNull(sender)) {
            return sender.uri(proxy + url).send(request.receive().retain().buffer().map(byteBufList -> {
                int length = request.requestHeaders().getInt(HttpHeaderNames.CONTENT_LENGTH);
                ByteBuf byteBuf = new PooledByteBufAllocator().buffer(length);
                for (ByteBuf value : byteBufList) {
                    byte[] buf = new byte[value.capacity()];
                    value.readBytes(buf);
                    byteBuf.writeBytes(buf);
                }
                LogHandler.logRequest(request, byteBuf);
                return byteBuf;
            }));
        }

        return null;
    }
}
