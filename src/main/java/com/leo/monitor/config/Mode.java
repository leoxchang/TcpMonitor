package com.leo.monitor.config;

import lombok.Getter;

/**
 * @author qiezi999
 */

public enum Mode {
    /**
     * tcp
     */
    TCP("tcp"),
    /**
     * http
     */
    HTTP("http"),
    /**
     * udp
     */
    UDP("udp");
    @Getter
    private final String value;

    Mode(String value) {
        this.value = value;
    }
}
