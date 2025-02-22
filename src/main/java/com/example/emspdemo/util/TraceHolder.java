package com.example.emspdemo.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 记录调用 Trace，便于查询
 *
 */
@Slf4j
public class TraceHolder {

    private static final ThreadLocal<String> TRACE_TTL_HOLDER = new ThreadLocal<>();

    public static void set(String traceId) {
        try {
            TRACE_TTL_HOLDER.set(traceId);
        } catch (Exception e) {
            log.error("set trace id error", e);
        }
    }

    public static String get() {
        try {
            return TRACE_TTL_HOLDER.get();
        } catch (Exception e) {
            log.error("get trace id error: ", e);
        }
        return null;
    }

    public static void remove() {
        TRACE_TTL_HOLDER.remove();
    }
}
