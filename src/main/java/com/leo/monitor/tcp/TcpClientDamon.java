package com.leo.monitor.tcp;

import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

/**
 * tcp client damon
 *
 * @author zhangxinlei
 * @date 2022-11-09
 */
public class TcpClientDamon {

    private final TcpClient tcpClient;
    private final Mono<? extends Connection> connection;

    public TcpClientDamon(String ip, Integer port) {
        tcpClient = TcpClient.create().host(ip).port(port).wiretap(true);
        connection = connect();
    }

    public Mono<? extends Connection> connect() {
        return tcpClient.doOnConnected(System.out::println).handle((in, out) -> out.neverComplete()).connect();
    }

    public Mono<? extends Connection> getConnection() {
        return connection;
    }
}
