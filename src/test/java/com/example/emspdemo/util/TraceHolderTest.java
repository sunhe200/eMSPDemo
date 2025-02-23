package com.example.emspdemo.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraceHolderTest {

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<ILoggingEvent> logCaptor;

    @BeforeEach
    void setupLogger() {
        Logger logger = (Logger) LoggerFactory.getLogger(TraceHolder.class);
        logger.addAppender(mockAppender);
    }

    @AfterEach
    void cleanup() {
        TraceHolder.remove(); // 确保每个测试后清理状态
    }

    @Test
    void shouldSetAndGetTraceId() {
        // Given
        String expectedTraceId = "trace-123";

        // When
        TraceHolder.set(expectedTraceId);
        String actualTraceId = TraceHolder.get();

        // Then
        assertEquals(expectedTraceId, actualTraceId);
    }

    @Test
    void shouldReturnNullWhenNotSet() {
        assertNull(TraceHolder.get());
    }

    @Test
    void shouldHandleNullTraceId() {
        // When
        TraceHolder.set(null);

        // Then
        assertNull(TraceHolder.get());
    }

    @Test
    void shouldRemoveTraceId() {
        // Given
        TraceHolder.set("trace-456");

        // When
        TraceHolder.remove();

        // Then
        assertNull(TraceHolder.get());
    }

    @Test
    void shouldMaintainThreadIsolation() throws InterruptedException, ExecutionException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(2);
        String mainThreadTraceId = "main-trace";
        String workerThreadTraceId = "worker-trace";

        // When
        TraceHolder.set(mainThreadTraceId);

        executor.submit(() -> {
            TraceHolder.set(workerThreadTraceId);
            assertEquals(workerThreadTraceId, TraceHolder.get());
        }).get();

        // Then
        assertEquals(mainThreadTraceId, TraceHolder.get());

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.SECONDS));
    }

    // 通过反射注入自定义 ThreadLocal
    private void injectMockThreadLocal(ThreadLocal<String> mock) throws Exception {
        java.lang.reflect.Field field = TraceHolder.class.getDeclaredField("TRACE_TTL_HOLDER");
        field.setAccessible(true);
        field.set(null, mock);
    }

    // 压测
    @RepeatedTest(100)
    void shouldHandleConcurrentAccess() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(20);

        for (int i = 0; i < 100; i++) {
            final int index = i;
            executor.submit(() -> {
                String traceId = "trace-" + index;
                TraceHolder.set(traceId);
                assertEquals(traceId, TraceHolder.get());
                TraceHolder.remove();
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
}