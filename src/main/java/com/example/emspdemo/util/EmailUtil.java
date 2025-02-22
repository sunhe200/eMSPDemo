package com.example.emspdemo.util;


import cn.hutool.core.lang.Assert;

public class EmailUtil {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static void checkEmailFormat(String email) {
        Assert.notNull(email, "Email must not be null");
        Assert.isTrue(email.matches(EMAIL_REGEX), "Email format is invalid");
    }
}
