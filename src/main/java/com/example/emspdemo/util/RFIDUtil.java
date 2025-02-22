package com.example.emspdemo.util;

import cn.hutool.core.util.IdUtil;

import java.util.Random;

public class RFIDUtil {

    private static final Random random = new Random();
    /**
     * 生成一个唯一的 UID，利用 UUID 机制，并在生成后检查全局唯一性
     */
    public static String generateUniqueUID() {
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 生成一个唯一的 Visible Number，这里示例生成一个10位数字的字符串，
     * 并检查全局唯一性（可根据实际需求调整生成策略）
     */
    public static String generateUniqueVisibleNumber() {
        return String.format("%010d", Math.abs(random.nextLong()) % 10000000000L);
    }

    /**
     * 检查给定的 UID 和 Visible Number 是否满足格式要求及唯一性要求。
     * 格式要求：
     *  - UID：必须符合 UUID 的格式，即 8-4-4-4-12 的十六进制字符串
     *  - Visible Number：必须是10位数字字符串
     *
     * @param uid 待检查的 UID
     * @param visibleNumber 待检查的 Visible Number
     * @return 若格式和唯一性均正确，返回 true；否则返回 false
     */
    public static boolean check(String uid, String visibleNumber) {
        if(uid==null || visibleNumber==null) {
            return false;
        }
        // 格式校验：UID必须为标准 UUID 格式
        if (!uid.matches("^[0-9a-fA-F]{8}[0-9a-fA-F]{4}[0-9a-fA-F]{4}[0-9a-fA-F]{4}[0-9a-fA-F]{12}$")) {
            return false;
        }
        // 格式校验：Visible Number 必须为10位数字
        if (!visibleNumber.matches("^\\d{10}$")) {
            return false;
        }
        // 唯一性校验：若任一值已存在集合中则返回 false
        return true;
    }
}
