package com.example.emspdemo.util;

import java.util.Random;
import java.util.regex.Pattern;

public class EMAIDUtil {
    private static final String COUNTRY_CODE = "CN"; // **国家代码**
    private static final String PROVIDER_ID = "XYZ"; // **示例充电服务提供商 ID，可修改**
    private static final Random random = new Random();
    // 正则表达式：标准化格式（无分隔符）
    private static final Pattern NORMALIZED_PATTERN = Pattern.compile("^[A-Z]{2}[\\dA-Z]{3}[\\dA-Z]{9}[\\dA-Z]$");
    // 正则表达式：允许可选分隔符 "-"
    private static final Pattern OPTIONAL_HYPHEN_PATTERN = Pattern.compile("^[a-z]{2}(-?)[\\da-z]{3}\\1[\\da-z]{9}(\\1[\\da-z])?$", Pattern.CASE_INSENSITIVE);


    /**
     * 规范化 EMAID：去除 "-" 并转换为大写
     *
     * @param emaid 原始 EMAID
     * @return 规范化后的 EMAID
     */
    public static String normalizeEMAID(String emaid) {
        return emaid.replaceAll("-", "").toUpperCase();
    }

    /**
     * 生成指定长度的随机字母和数字字符串
     *
     * @param length 需要生成的字符串长度
     * @return 随机生成的字母数字字符串
     */
    private static String randomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 计算 EMAID 的校验位（简单示例：使用 ASCII 累加和的模 36 运算）
     *
     * @param baseEMAID 不含校验位的 EMAID（如 "CNXYZ1234567890"）
     * @return 计算出的校验字符（0-9 或 A-Z）
     */
    private static char generateCheckDigit(String baseEMAID) {
        int sum = 0;
        for (char c : baseEMAID.toCharArray()) {
            sum += c;
        }
        int checkValue = sum % 36;
        return (char) (checkValue < 10 ? '0' + checkValue : 'A' + checkValue - 10);
    }

    /**
     * 校验 EMAID 是否符合格式要求
     *
     * @param emaid 待校验的 EMAID
     * @return true：符合格式且唯一，false：格式错误或已存在
     */
    public static boolean checkEMAID(String emaid) {
        if (emaid == null) {
            return false;
        }

        // 先检查是否符合允许的格式（支持可选的 "-"）
        if (!OPTIONAL_HYPHEN_PATTERN.matcher(emaid).matches()) {
            return false;
        }

        // 规范化 EMAID：移除 "-" 并转换为大写
        String normalizedEMAID = normalizeEMAID(emaid);

        // 最终校验标准格式
        return NORMALIZED_PATTERN.matcher(normalizedEMAID).matches();
    }

    /**
     * 生成符合 ISO 15118 和 Hubject 规则的唯一 EMAID。
     * 格式示例（无分隔符）：CNXYZ12345678901
     *
     * @return 生成的唯一 EMAID
     */
    public static String generateEMAID() {
        String emaInstance = randomAlphanumeric(9); // 9 位数字/字母
        char checkDigit = generateCheckDigit(COUNTRY_CODE + PROVIDER_ID + emaInstance); // 计算校验位
        return COUNTRY_CODE + PROVIDER_ID + emaInstance + checkDigit;
    }
}
