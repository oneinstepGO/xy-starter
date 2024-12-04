package com.oneinstep.starter.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oneinstep.starter.sys.bean.domain.SysConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置
 **/
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 获取所有的系统配置
     *
     * @return 所有的系统配置
     */
    List<SysConfig> listAllWithCache();

    /**
     * 根据key 更新配置
     *
     * @param configMap key-value
     * @return 是否更新成功
     */
    boolean updateConfigByKey(Map<String, String> configMap);

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

    String getValueByKey(String key);

}
