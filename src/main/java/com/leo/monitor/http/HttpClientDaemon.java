package com.leo.monitor.http;

import com.leo.monitor.config.HttpMethod;
import com.leo.monitor.handler.LogHandler;
import lombok.extern.java.Log;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServerRequest;

import java.util.Objects;

/**
 * http client damon
 *
 * @author zhangxinlei
 * @date 2022-11-07
 */
@Log
public class HttpClientDaemon {

    private final HttpClient httpClient;

    private String proxy;

    public HttpClientDaemon(String proxyHost, Integer proxyPort) {
        this.httpClient = HttpClient.create()
                .doOnConnected(connection -> log.info(connection.channel().toString()));
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
            return sender.uri(proxy + url).send(request.receive().retain().map(byteBuf -> {
                LogHandler.logRequest(request, byteBuf);
                return byteBuf;
            }));
        }

        return null;
    }
}
