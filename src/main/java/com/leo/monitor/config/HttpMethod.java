package com.leo.monitor.config;

/**
 * @author zhangxinlei
 */
public enum HttpMethod {
    /**
     * GET
     */
    GET(),
    /**
     * POST
     */
    POST(),
    /**
     * PUT
     */
    PUT(),
    /**
     * PATCH
     */
    PATCH(),
    /**
     * DELETE
     */
    DELETE(),
    /**
     * HEAD
     */
    HEAD();

    HttpMethod() {
    }
}
