package com.leo.monitor.handler;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import io.netty.buffer.ByteBuf;
import lombok.extern.java.Log;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServerRequest;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;


/**
 * log handler
 *
 * @author zhangxinlei
 * @date 2022-11-07
 */
@Log
public class LogHandler {

    private LogHandler() {
    }

    private static Charset charset = StandardCharsets.UTF_8;

    public static void setCharset(Charset charset) {
        LogHandler.charset = charset;
    }

    public static void logRequest(HttpServerRequest request, ByteBuf byteBuf) {
        StringBuilder builder = new StringBuilder();
        builder.append(getFormatDate());
        builder.append(" request------> ");
        StringBuilder jsonBuilder = new StringBuilder("{");
        jsonBuilder.append("\"method\":").append("\"").append(request.method()).append("\",");
        jsonBuilder.append("\"header\":{");
        for (Iterator<Map.Entry<String, String>> it = request.requestHeaders().iteratorAsString(); it.hasNext(); ) {
            Map.Entry<String, String> entry = it.next();
            jsonBuilder.append("\"").append(entry.getKey()).append("\":").append("\"").append(entry.getValue()).append(
                    "\",");
        }
        jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        jsonBuilder.append("}");
        if (!Objects.isNull(byteBuf)) {
            jsonBuilder.append(",\"body\":");
            jsonBuilder.append(byteBuf.toString(charset));
        }
        jsonBuilder.append(",\"uri\":").append("\"").append(request.uri()).append("\"");
        jsonBuilder.append(",\"url\":").append("\"").append(request.path()).append("\"").append("}");
        log.log(Level.INFO,
                builder.append(JSONObject.parseObject(jsonBuilder.toString()).toJSONString(JSONWriter.Feature.PrettyFormat)).toString());
    }

    public static void logRequest(ByteBuf byteBuf) {
        StringBuilder builder = new StringBuilder();
        builder.append(getFormatDate());
        builder.append(" request------> ");
        StringBuilder jsonBuilder = new StringBuilder("{");
        if (!Objects.isNull(byteBuf)) {
            jsonBuilder.append("body\":\"");
            jsonBuilder.append(byteBuf.toString(charset));
        }
        jsonBuilder.append("\"}");
        log.log(Level.INFO, builder.append(jsonBuilder).toString());
    }

    public static void logResponse(HttpClientResponse response, ByteBuf byteBuf) {
        StringBuilder builder = new StringBuilder();
        builder.append(getFormatDate());
        builder.append(" response------> ");
        StringBuilder jsonBuilder = new StringBuilder("{");
        jsonBuilder.append("\"header\":{");
        for (Iterator<Map.Entry<String, String>> it = response.responseHeaders().iteratorAsString(); it.hasNext(); ) {
            Map.Entry<String, String> entry = it.next();
            jsonBuilder.append("\"").append(entry.getKey()).append("\":").append("\"").append(entry.getValue().replace("\"", "\\\"")).append(
                    "\",");
        }
        jsonBuilder.append("},\"body\":");
        jsonBuilder.append(byteBuf.toString(Charset.defaultCharset())).append("}");
        log.log(Level.INFO,
                builder.append(JSONObject.parseObject(jsonBuilder.toString()).toJSONString(JSONWriter.Feature.PrettyFormat)).toString());
    }

    public static void logResponse(ByteBuf byteBuf) {
        StringBuilder builder = new StringBuilder();
        builder.append(getFormatDate());
        builder.append(" response------> ");
        String jsonBuilder = "{" + "\"body\":\"" +
                byteBuf.toString(charset) + "\"}";
        log.log(Level.INFO, builder.append(jsonBuilder).toString());
    }

    private static String getFormatDate() {
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
}
