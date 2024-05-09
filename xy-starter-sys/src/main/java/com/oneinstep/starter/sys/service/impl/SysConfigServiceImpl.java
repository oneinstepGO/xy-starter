package com.oneinstep.starter.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oneinstep.starter.core.utils.OneIPUtil;
import com.oneinstep.starter.sys.constants.SysConfigKeyConstant;
import com.oneinstep.starter.sys.bean.domain.SysConfig;
import com.oneinstep.starter.sys.mapper.SysConfigMapper;
import com.oneinstep.starter.sys.service.SysConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 系统配置service实现类
 **/
@Service
@Slf4j
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public boolean updateConfigByKey(Map<String, String> configMap) {
        if (MapUtils.isEmpty(configMap)) {
            log.warn("updateConfigByKey.... configMap is empty");
            return true;
        }

        return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                SysConfig sysConfig = new SysConfig();
                sysConfig.setParamKey(entry.getKey());
                sysConfig.setParamValue(entry.getValue());
                sysConfig.setUpdateTime(LocalDateTime.now());
                boolean ret = this.lambdaUpdate().eq(SysConfig::getParamKey, entry.getKey()).update(sysConfig);
                if (!ret) {
                    log.error("update sys config error, paramKey: {}, paramValue: {}", entry.getKey(), entry.getValue());
                    status.setRollbackOnly();
                    return false;
                }
            }
            return true;
        }));

    }

    @Override
    public String getConfigValueByKey(String key) {
        return this.lambdaQuery().eq(SysConfig::getParamKey, key).one().getParamValue();
    }

    @Override
    public String getRunJobIp() {
        return getConfigValueByKey(SysConfigKeyConstant.RUN_JOB_IP);
    }

    @Override
    public String getGlobalWhiteList() {
        return getConfigValueByKey(SysConfigKeyConstant.GLOBAL_LOGIN_WHITE_LIST);
    }

    @Override
    public boolean isEnableTask() {
        String runJobIp = getRunJobIp();
        log.info("runJobIp >>>>>>> {}", runJobIp);
        if (StringUtils.isBlank(runJobIp)) {
            log.error("runJobIp is blank");
            return false;
        }

        String localIp = OneIPUtil.getLocalIPAddress();
        log.info("localIp >>>>>>> {}", localIp);
        if (StringUtils.isBlank(localIp)) {
            log.error("localIp is blank");
            return false;
        }

        // 以英文逗号分隔 runJobIp
        String[] runJobIps = runJobIp.split(",");

        // 如果包含本地ip，则返回true
        for (String ip : runJobIps) {
            if (StringUtils.equals(ip.trim(), localIp)) {
                log.info("runJobIp contains localIp, runJobIp:{}, localIp:{}", runJobIp, localIp);
                return true;
            }
        }

        log.info("runJobIp not contains localIp, runJobIp:{}, localIp:{}", runJobIp, localIp);


        return false;
    }

}
