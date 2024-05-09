package com.oneinstep.starter.sys.constants;

/**
 * 系统配置key常量
 **/
public class SysConfigKeyConstant {

    private SysConfigKeyConstant() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 运行定时任务的ip
     */
    public static final String RUN_JOB_IP = "runJobIp";
    /**
     * 全局白名单
     */
    public static final String GLOBAL_LOGIN_WHITE_LIST = "globalLoginWhiteList";

}
