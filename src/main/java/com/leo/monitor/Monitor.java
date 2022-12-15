package com.leo.monitor;

import com.leo.monitor.config.Constants;
import com.leo.monitor.config.Mode;
import com.leo.monitor.handler.LogHandler;
import com.leo.monitor.http.HttpClientDamon;
import com.leo.monitor.http.HttpServerDamon;
import com.leo.monitor.tcp.TcpServerDamon;
import org.apache.commons.cli.*;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * monitor
 *
 * @author zhangxinlei
 * @date 2022-11-04
 */
public class Monitor {
    public static void main(String[] args) {
        String proxyHost;
        int proxyPort;
        String localHost = "127.0.0.1";
        int localPort = 9999;
        try {
            var parser = new DefaultParser();
            var options = new Options();
            options.addOption("m", Constants.MODE, true, "proxy mode tcp or http");
            options.addOption("h", Constants.PROXY_HOST, true, "set proxy host");
            options.addOption("p", Constants.PROXY_PORT, true, "set proxy port");
            options.addOption("lh", Constants.LISTENER_HOST, false, "set local port,default 127.0.0.1");
            options.addOption("lp", Constants.LISTENER_PORT, false, "set local port,default 9999");
            options.addOption("c", Constants.CODE, false, "log decode charset,default utf-8");
            CommandLine cmd = parser.parse(options, args);
            if (!cmd.hasOption(Constants.MODE)) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp("options", options);
                return;
            }
            if (!cmd.hasOption(Constants.PROXY_HOST)) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp("options", options);
                return;
            }
            if (!cmd.hasOption(Constants.PROXY_PORT)) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp("options", options);
                return;
            }
            proxyHost = cmd.getOptionValue(Constants.PROXY_HOST);
            proxyPort = Integer.parseInt(cmd.getOptionValue(Constants.PROXY_PORT));
            if (cmd.hasOption(Constants.LISTENER_HOST)) {
                localHost = cmd.getOptionValue(Constants.LISTENER_HOST);
            }
            if (cmd.hasOption(Constants.LISTENER_PORT)) {
                localPort = Integer.parseInt(cmd.getOptionValue(Constants.LISTENER_PORT));
            }
            if (cmd.hasOption(Constants.CODE)) {
                LogHandler.charset = Charset.forName(cmd.getOptionValue(Constants.CODE));
            }

            if (Objects.equals(cmd.getOptionValue(Constants.MODE), Mode.http.name())) {
                HttpClientDamon clientDamon = new HttpClientDamon(proxyHost, proxyPort);
                HttpServerDamon httpServerDamon = new HttpServerDamon(clientDamon);
                httpServerDamon.start(localHost, localPort);
            } else if (Objects.equals(cmd.getOptionValue(Constants.MODE), Mode.tcp.name())) {
                TcpServerDamon tcpServerDamon = new TcpServerDamon(proxyHost, proxyPort);
                tcpServerDamon.start(localHost, localPort);
            } else {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp("options", options);
            }
        } catch (ParseException e) {
            System.out.println("解析命令行错误");
            e.printStackTrace();
        }
    }
}
