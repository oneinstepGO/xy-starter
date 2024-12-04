package com.oneinstep.starter.core.constants;

import lombok.Getter;

/**
 * 通用状态
 **/
@Getter
public enum CommonStatus {

    NORMAL(1, "正常"),
    DISABLE(0, "禁用"),
    SOFT_DELETE(-99, "删除");

    private final int code;
    private final String desc;

    CommonStatus(final int code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CommonStatus of(Integer code) {
        for (CommonStatus status : CommonStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    public static String getDescByCode(Integer code) {
        CommonStatus commonStatus = of(code);
        return commonStatus == null ? null : commonStatus.getDesc();
    }

}
