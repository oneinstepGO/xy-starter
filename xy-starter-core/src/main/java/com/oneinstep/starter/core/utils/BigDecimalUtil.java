package com.oneinstep.starter.core.utils;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Optional;

/**
 * BigDecimal 工具类
 **/
@UtilityClass
public class BigDecimalUtil {

    /**
     * 默认保留小数位数 2
     */
    private static final int DEFAULT_SCALE = 2;

    /**
     * 默认四舍五入模式
     */
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 加法运算
     * NULL 当做0计算
     *
     * @param value1 加数
     * @param value2 被加数
     * @return 和
     */
    public static BigDecimal add(BigDecimal value1, BigDecimal value2) {
        value1 = Optional.ofNullable(value1).orElse(BigDecimal.ZERO);
        value2 = Optional.ofNullable(value2).orElse(BigDecimal.ZERO);
        return value1.add(value2);
    }

    /**
     * 减法运算
     * NULL 当做0计算
     *
     * @param value1 减数
     * @param value2 被减数
     * @return 差
     */
    public static BigDecimal subtract(BigDecimal value1, BigDecimal value2) {
        value1 = Optional.ofNullable(value1).orElse(BigDecimal.ZERO);
        value2 = Optional.ofNullable(value2).orElse(BigDecimal.ZERO);
        return value1.subtract(value2);
    }

    /**
     * 乘法运算
     *
     * @param value1 乘数
     * @param value2 被乘数
     * @return 积
     */
    public static BigDecimal multiply(BigDecimal value1, BigDecimal value2) {
        return multiply(value1, value2, DEFAULT_SCALE);
    }

    /**
     * 乘法运算
     *
     * @param value1 乘数
     * @param value2 被乘数
     * @param scale  保留小数位数
     * @return 积
     */
    public static BigDecimal multiply(BigDecimal value1, BigDecimal value2, int scale) {
        value1 = Optional.ofNullable(value1).orElse(BigDecimal.ZERO);
        value2 = Optional.ofNullable(value2).orElse(BigDecimal.ZERO);
        return value1.multiply(value2).setScale(scale, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 乘法运算
     *
     * @param value1       乘数
     * @param value2       被乘数
     * @param roundingMode 保留小数模式
     * @return 积
     */
    public static BigDecimal multiply(BigDecimal value1, BigDecimal value2, RoundingMode roundingMode) {
        value1 = Optional.ofNullable(value1).orElse(BigDecimal.ZERO);
        value2 = Optional.ofNullable(value2).orElse(BigDecimal.ZERO);
        return multiply(value1, value2, DEFAULT_SCALE, roundingMode);
    }

    /**
     * 乘法运算
     *
     * @param value1       乘数
     * @param value2       被乘数
     * @param roundingMode 保留小数模式
     * @return 积
     */
    public static BigDecimal multiply(BigDecimal value1, BigDecimal value2, int scale, RoundingMode roundingMode) {
        value1 = Optional.ofNullable(value1).orElse(BigDecimal.ZERO);
        value2 = Optional.ofNullable(value2).orElse(BigDecimal.ZERO);
        return value1.multiply(value2).setScale(scale, roundingMode);
    }


    /**
     * 除法运算
     *
     * @param value1 除数
     * @param value2 被除数
     * @return 商
     */
    public static BigDecimal divide(@NonNull BigDecimal value1, @NonNull BigDecimal value2) {
        return divide(value1, value2, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 除法运算
     *
     * @param value1 除数
     * @param value2 被除数
     * @return 商
     */
    public static BigDecimal divide(@NonNull BigDecimal value1, @NonNull BigDecimal value2, @NotNull RoundingMode roundingMode) {
        return divide(value1, value2, DEFAULT_SCALE, roundingMode);
    }

    /**
     * 除法运算
     *
     * @param value1       除数
     * @param value2       被除数
     * @param scale        保留小数位数
     * @param roundingMode 保留小数模式
     * @return 商
     */
    public static BigDecimal divide(BigDecimal value1, BigDecimal value2, int scale, RoundingMode roundingMode) {
        if (value2.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("被除数不能为0");
        }
        return value1.divide(value2, scale, roundingMode);
    }

    /**
     * 计算百分比
     *
     * @param value1 分子
     * @param value2 分母
     * @return 百分比结果
     */
    public static BigDecimal percentage(BigDecimal value1, BigDecimal value2) {
        return percentage(value1, value2, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 计算百分比
     *
     * @param value1       分子
     * @param value2       分母
     * @param scale        保留小数位数
     * @param roundingMode 保留小数模式
     * @return 百分比结果
     */
    public static BigDecimal percentage(BigDecimal value1, BigDecimal value2, int scale, RoundingMode roundingMode) {
        if (value2.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("分母不能为0");
        }
        BigDecimal result = value1.divide(value2, scale + 2, roundingMode);
        return result.multiply(new BigDecimal("100")).setScale(scale, roundingMode);
    }

    /**
     * 格式化输出
     *
     * @param value 要格式化的数值
     * @return 格式化后的字符串
     */
    public static String format(BigDecimal value) {
        return format(value, "#,##0.00");
    }

    /**
     * 格式化输出
     *
     * @param value  要格式化的数值
     * @param format 格式字符串
     * @return 格式化后的字符串
     */
    public static String format(BigDecimal value, String format) {
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(value);
    }

    /**
     * 开平方运算（使用牛顿法）
     *
     * @param value 要开平方的数值
     * @return 平方根结果
     */
    public static BigDecimal sqrt(BigDecimal value) {
        return sqrt(value, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 开平方运算（使用牛顿法）
     *
     * @param value        要开平方的数值
     * @param scale        保留小数位数
     * @param roundingMode 保留小数模式
     * @return 平方根结果
     */
    public static BigDecimal sqrt(BigDecimal value, int scale, RoundingMode roundingMode) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("开平方的数值不能为负数");
        }
        if (value.compareTo(BigDecimal.ZERO) == 0 || value.compareTo(BigDecimal.ONE) == 0) {
            return value;
        }
        BigDecimal two = new BigDecimal("2");
        BigDecimal x0 = BigDecimal.ZERO;
        BigDecimal x1 = value.divide(two);
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = x0.add(value.divide(x0, scale, roundingMode)).divide(two, scale, roundingMode);
        }
        return x1;
    }

    /**
     * 求最小值
     *
     * @param values 一组数值
     * @return 最小值
     */
    public static BigDecimal min(BigDecimal... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("参数不能为空");
        }
        BigDecimal minValue = values[0];
        for (int i = 1; i < values.length; i++) {
            minValue = minValue.min(values[i]);
        }
        return minValue;
    }

    /**
     * 求最大值
     *
     * @param values 一组数值
     * @return 最大值
     */
    public static BigDecimal max(BigDecimal... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("参数不能为空");
        }
        BigDecimal maxValue = values[0];
        for (int i = 1; i < values.length; i++) {
            maxValue = maxValue.max(values[i]);
        }
        return maxValue;
    }

    /**
     * 是否大于等于 0
     *
     * @param chgBalanceAmount 变更余额
     * @return 是否大于等于 0
     */
    public static boolean largeThanOrEqualZero(@NonNull BigDecimal chgBalanceAmount) {
        return chgBalanceAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * 将BigDecimal 按四舍五入保留两位小数，并且 乘以100，转换成 long类型整数，如果是 null, 则返回 0
     *
     * @param amount 金额，元
     * @return 分
     */
    public static long multiply100ToLong(@NotNull BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).longValue();
    }

    /**
     * 比较两个BigDecimal 大小，最多考虑2位小数
     *
     * @param b1 b1
     * @param b2 b2
     * @return b1 > b2 返回 1， b1 < b2 返回 -1， b1 == b2 返回 0
     */
    public static int compareIgnoreScaleMoreThan2(BigDecimal b1, BigDecimal b2) {
        // copy b1 and b2 to copyB1 and copyB2
        BigDecimal copyB1 = new BigDecimal(b1.toString());
        BigDecimal copyB2 = new BigDecimal(b2.toString());
        long l = multiply100ToLong(copyB1);
        long l1 = multiply100ToLong(copyB2);
        return Long.compare(l, l1);
    }

    public static BigDecimal abs(BigDecimal chgBalanceAmount) {
        return chgBalanceAmount.abs();
    }

}
