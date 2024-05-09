package com.oneinstep.starter.core.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCell {

    int value();

    boolean longToMoney() default false;

    boolean decimalToMoney() default false;

    boolean isLocalDateTime() default false;

    boolean isLocalDate() default false;

    /**
     * 是否是枚举
     */
    boolean isEnum() default false;

    /**
     * 枚举类
     */
    Class<? extends Enum> enumClass() default Enum.class;
}
