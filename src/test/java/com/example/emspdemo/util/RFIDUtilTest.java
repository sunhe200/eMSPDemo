package com.example.emspdemo.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RFIDUtilTest {

    /**
     * 测试 `generateUniqueUID()` 生成的 UID 是否符合 UUID 规范
     */
    @Test
    public void testGenerateUniqueUID() {
        String uid = RFIDUtil.generateUniqueUID();
        assertNotNull(uid, "UID 不能为空");
        assertTrue(uid.matches("^[0-9a-fA-F]{8}[0-9a-fA-F]{4}[0-9a-fA-F]{4}[0-9a-fA-F]{4}[0-9a-fA-F]{12}$"),
                "UID 格式错误: " + uid);
    }

    /**
     * 测试 `generateUniqueVisibleNumber()` 生成的 Visible Number 是否为 10 位数字
     */
    @Test
    public void testGenerateUniqueVisibleNumber() {
        String visibleNumber = RFIDUtil.generateUniqueVisibleNumber();
        assertNotNull(visibleNumber, "Visible Number 不能为空");
        assertTrue(visibleNumber.matches("^\\d{10}$"), "Visible Number 格式错误: " + visibleNumber);
    }

    /**
     * 测试 `check()` 方法 - 应该通过合法的 UID 和 Visible Number
     */
    @Test
    public void testCheckValidInputs() {
        String validUID = "550e8400e29b41d4a716446655440000"; // 合法 UUID
        String validVisibleNumber = "1234567890"; // 10 位数字
        assertTrue(RFIDUtil.check(validUID, validVisibleNumber), "合法 UID 和 Visible Number 校验失败");
    }

    /**
     * 测试 `check()` 方法 - 应该拒绝无效的 UID 和 Visible Number
     */
    @Test
    public void testCheckInvalidInputs() {
        String invalidUID2 = "550e8400-e29b-41d4-a716-XYZXYZXYZXYZ"; // 非十六进制字符
        String invalidUID3 = "12345678-1234-1234-1234-123456789"; // 长度不足
        String invalidVisibleNumber1 = "12345"; // 长度不足
        String invalidVisibleNumber2 = "abcdefghij"; // 不是数字
        String invalidVisibleNumber3 = "12345678901"; // 长度超限
        String nullUID = null;
        String nullVisibleNumber = null;

        assertFalse(RFIDUtil.check(invalidUID2, "1234567890"), "无效 UID 误通过: " + invalidUID2);
        assertFalse(RFIDUtil.check(invalidUID3, "1234567890"), "无效 UID 误通过: " + invalidUID3);
        assertFalse(RFIDUtil.check("550e8400-e29b-41d4-a716-446655440000", invalidVisibleNumber1),
                "无效 Visible Number 误通过: " + invalidVisibleNumber1);
        assertFalse(RFIDUtil.check("550e8400-e29b-41d4-a716-446655440000", invalidVisibleNumber2),
                "无效 Visible Number 误通过: " + invalidVisibleNumber2);
        assertFalse(RFIDUtil.check("550e8400-e29b-41d4-a716-446655440000", invalidVisibleNumber3),
                "无效 Visible Number 误通过: " + invalidVisibleNumber3);
        assertFalse(RFIDUtil.check(nullUID, "1234567890"), "null UID 误通过");
        assertFalse(RFIDUtil.check("550e8400-e29b-41d4-a716-446655440000", nullVisibleNumber), "null Visible Number 误通过");
    }
}
