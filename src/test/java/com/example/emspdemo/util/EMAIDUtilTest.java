package com.example.emspdemo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EMAIDUtilTest {

    /**
     * 测试 `generateEMAID()` 生成的 EMAID 是否符合格式
     */
    @Test
    public void testGenerateEMAID() {
        String emaid = EMAIDUtil.generateEMAID();
        assertNotNull(emaid, "EMAID 不能为空");
        assertTrue(emaid.matches("^[A-Z]{2}[\\dA-Z]{3}[\\dA-Z]{9}[\\dA-Z]$"), "EMAID 格式错误: " + emaid);
    }

    /**
     * 测试 `checkEMAID()` 方法 - 应该通过合法的 EMAID
     */
    @Test
    public void testCheckValidEMAID() {
        String validEMAID = "CNXYZ123456789A";
        assertTrue(EMAIDUtil.checkEMAID(validEMAID), "合法 EMAID 校验失败: " + validEMAID);

        String validEMAIDWithHyphens = "CN-XYZ-123456789-0";
        assertTrue(EMAIDUtil.checkEMAID(validEMAIDWithHyphens), "合法 EMAID (带 `-`) 校验失败: " + validEMAIDWithHyphens);
    }

    /**
     * 测试 `checkEMAID()` 方法 - 应该拒绝无效的 EMAID
     */
    @Test
    public void testCheckInvalidEMAID() {
        String invalidEMAID1 = "INVALID123"; // 格式错误
        String invalidEMAID3 = "CNXYZ1234"; // 长度不足
        String invalidEMAID4 = "CN-XY-123456789-0"; // 提供商 ID 长度错误
        String invalidEMAID5 = null; // null 值

        assertFalse(EMAIDUtil.checkEMAID(invalidEMAID1), "无效 EMAID 误通过: " + invalidEMAID1);
        assertFalse(EMAIDUtil.checkEMAID(invalidEMAID3), "无效 EMAID 误通过: " + invalidEMAID3);
        assertFalse(EMAIDUtil.checkEMAID(invalidEMAID4), "无效 EMAID 误通过: " + invalidEMAID4);
        assertFalse(EMAIDUtil.checkEMAID(invalidEMAID5), "无效 EMAID 误通过: " + invalidEMAID5);
    }

    /**
     * 测试 `normalizeEMAID()` 方法 - 去除 `-` 并转换大写
     */
    @Test
    public void testNormalizeEMAID() {
        assertEquals("CNXYZ1234567890", EMAIDUtil.normalizeEMAID("cn-xyz-123456789-0"), "EMAID 规范化失败");
        assertEquals("CNXYZ1234567890", EMAIDUtil.normalizeEMAID("CNXYZ1234567890"), "EMAID 规范化失败");
    }
}
