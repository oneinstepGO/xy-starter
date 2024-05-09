package com.oneinstep.starter.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oneinstep.starter.sys.bean.domain.SysConfig;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

/**
 * 系统配置
 **/
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 根据key 更新配置
     *
     * @param configMap key-value
     * @return 是否更新成功
     */
    boolean updateConfigByKey(Map<String, String> configMap);

    /**
     * 根据key获取配置
     *
     * @param key 配置key
     * @return 配置值
     */
    String getConfigValueByKey(@NotEmpty String key);

    /**
     * 获取运行定时任务的ip
     *
     * @return ip
     */
    String getRunJobIp();

    String getGlobalWhiteList();

    /**
     * 是否开启 定时任务
     */
    boolean isEnableTask();

}
