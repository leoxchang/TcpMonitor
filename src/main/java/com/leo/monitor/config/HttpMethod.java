package com.leo.monitor.config;

public enum HttpMethod {
    GET("GET"), POST("POST"), PUT("PUT"), PATCH("PATCH"), DELETE("DELETE"),HEAD("HEAD");
    private String value;

    HttpMethod(String value) {
        this.value = value;
    }
}
