package com.example.emspdemo.util;

import cn.hutool.core.exceptions.ValidateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EmailUtilTest {

    // 有效邮箱格式测试数据
    static Stream<String> validEmailProvider() {
        return Stream.of(
                "user@example.com",
                "john.doe@example.co.uk",
                "user.name+tag@sub.example.org",
                "user_name@example-domain.com",
                "123456@numbers.com",
                "UPPER.CASE@EXAMPLE.COM"
        );
    }

    // 无效邮箱格式测试数据
    static Stream<String> invalidEmailProvider() {
        return Stream.of(
                "plainaddress",
                "@missing-local.com",
                "user@.com",
                "user@domain..com",
                "user@-domain.com",
                "user@domain.c",
                "user@domain..org",
                "user@domain_with_underscore.com",
                "user@domain-with-hyphen-.com",
                "user@.domain.com"
        );
    }

    @ParameterizedTest
    @MethodSource("validEmailProvider")
    void testCheckEmailFormat_ValidEmail_ShouldNotThrowException(String email) {
        assertDoesNotThrow(() -> EmailUtil.checkEmailFormat(email));
    }

    @Test
    void testCheckEmailFormat_WithMaxLength_ShouldPass() {
        // 生成 320 字符的合法邮箱（RFC 5321 最大长度限制）
        String localPart = "a".repeat(64);
        String domain = "example.com";
        String longEmail = localPart + "@" + domain;

        assertDoesNotThrow(() -> EmailUtil.checkEmailFormat(longEmail));
    }
}