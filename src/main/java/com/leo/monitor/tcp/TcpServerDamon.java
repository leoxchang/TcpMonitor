package com.leo.monitor.tcp;

import com.leo.monitor.Damon;
import com.leo.monitor.handler.LogHandler;
import io.netty.buffer.ByteBuf;
import reactor.netty.tcp.TcpServer;


/**
 * tcp server
 *
 * @author zhangxinlei
 * @date 2022-11-04
 */
public class TcpServerDamon implements Damon {

    private TcpClientDamon tcpClientDamon;

    private final String proxyHost;

    private final Integer proxyPort;

    public TcpServerDamon(String proxyHost, Integer proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    @Override
    public void start(String ip, int port) {
        TcpServer server =
                TcpServer.create().host(ip).port(port).doOnConnection(connection -> {
                    tcpClientDamon = new TcpClientDamon(proxyHost, proxyPort);
                    System.out.println(connection);
                }).handle((inbound, outbound) -> outbound.send(tcpClientDamon.getConnection().repeat(0L).flatMap(connection -> {
                    connection.outbound().send(inbound.receive().retain().map(byteBuf -> {
                        LogHandler.logRequest(byteBuf);
                        return byteBuf;
                    })).neverComplete().subscribe();
                    return connection.inbound().receive().retain().map(byteBuf -> {
                        LogHandler.logResponse(byteBuf);
                        return byteBuf;
                    });
                }).map(ByteBuf::asByteBuf).doOnError(throwable -> System.out.println(throwable.getMessage()))).neverComplete()).wiretap(true);
        System.out.println("启动完成");
        server.bindNow().onDispose().log().block();
    }
}
