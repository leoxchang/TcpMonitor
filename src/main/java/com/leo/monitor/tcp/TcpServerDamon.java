package com.leo.monitor.tcp;

import com.leo.monitor.Damon;
import com.leo.monitor.handler.LogHandler;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

/**
 * tcp server
 *
 * @author zhangxinlei
 * @date 2022-11-04
 */
@Log
public class TcpServerDamon implements Damon {

    private final String proxyHost;

    private final Integer proxyPort;

    public TcpServerDamon(String proxyHost, Integer proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    @Override
    public void start(String ip, int port) {
        TcpServer server =
                TcpServer.create().host(ip).port(port)
                        .doOnConnection(connection -> log.info(connection.toString()))
                        .handle((inbound, outbound) -> TcpClient.create()
                                .host(proxyHost)
                                .port(proxyPort)
                                .doOnConnected(conn -> log.info(conn.toString()))
                                .connect().flatMap(targetConnection -> {
                                    //双向数据转发
                                    Mono<Void> clientToTarget = targetConnection.outbound()
                                            .send(inbound.receive().retain().doOnNext(LogHandler::logRequest))
                                            .then();

                                    Mono<Void> targetToClient = outbound
                                            .send(targetConnection.inbound().receive().retain().doOnNext(LogHandler::logResponse))
                                            .then();

                                    // 同时执行双向转发，任一方向结束时整体结束
                                    return Mono.when(clientToTarget, targetToClient)
                                            .doFinally(signalType -> targetConnection.dispose());
                                })).wiretap(true);
        log.info("启动完成");
        server.bindNow().onDispose().log().block();
    }
}
