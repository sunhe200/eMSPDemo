package com.example.emspdemo.component;

import com.example.emspdemo.util.TraceHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TraceAspectTest {

    private final TraceAspect traceAspect = new TraceAspect();

    @Test
    public void testTraceAround() throws Throwable {
        // 模拟 ProceedingJoinPoint
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        // 当调用 proceed() 时，验证 TraceHolder 中 trace 不为空，然后返回一个结果
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            String trace = TraceHolder.get();
            assertNotNull(trace, "在方法执行期间，TraceHolder 中的 trace 应不为空");
            return "proceedResult";
        });

        // 调用切面方法
        Object result = traceAspect.traceAround(joinPoint);
        // 验证返回结果
        assertEquals("proceedResult", result, "返回结果应与 joinPoint.proceed() 返回一致");
        // 执行结束后，TraceHolder 中的 trace 应已清除
        assertNull(TraceHolder.get(), "方法执行结束后，TraceHolder 中的 trace 应被清除");
    }
}
