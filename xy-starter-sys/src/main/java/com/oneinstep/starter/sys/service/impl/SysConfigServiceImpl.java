package com.oneinstep.starter.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oneinstep.starter.common.cache.CacheConstants;
import com.oneinstep.starter.core.utils.IPUtil;
import com.oneinstep.starter.sys.bean.domain.SysConfig;
import com.oneinstep.starter.sys.constants.SysConfigKeyConstant;
import com.oneinstep.starter.sys.mapper.SysConfigMapper;
import com.oneinstep.starter.sys.service.SysConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 系统配置service实现类
 **/
@Service
@Slf4j
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private SysConfigService self;

    @Resource
    private CacheManager cacheManager;

    @Override
    @Cacheable(cacheNames = CacheConstants.SYS_CONFIG, key = "'listAllWithCache'", sync = true)
    public List<SysConfig> listAllWithCache() {
        return this.lambdaQuery().list();
    }

    @Override
    @CacheEvict(cacheNames = CacheConstants.SYS_CONFIG, allEntries = true, condition = "#result == true")
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
                boolean b = this.lambdaUpdate().eq(SysConfig::getParamKey, entry.getKey()).update(sysConfig);
                if (!b) {
                    log.error("update sys config error, paramKey: {}, paramValue: {}", entry.getKey(), entry.getValue());
                    status.setRollbackOnly();
                    return false;
                }
            }
            return true;
        }));

    }

    @Override
    public String getRunJobIp() {
        return self.getValueByKey(SysConfigKeyConstant.RUN_JOB_IP);
    }

    @Override
    public String getGlobalWhiteList() {
        return self.getValueByKey(SysConfigKeyConstant.GLOBAL_LOGIN_WHITE_LIST);
    }

    @Override
    public boolean isEnableTask() {
        String runJobIp = getRunJobIp();
        log.info("runJobIp >>>>>>> {}", runJobIp);
        if (StringUtils.isBlank(runJobIp)) {
            log.error("runJobIp is blank");
            return false;
        }

        String localIp = IPUtil.getLocalIPAddress();
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

    @Override
    @Cacheable(cacheNames = CacheConstants.SYS_CONFIG, key = "#key", sync = true)
    public String getValueByKey(String key) {
        String paramValue = this.lambdaQuery().eq(SysConfig::getParamKey, key).one().getParamValue();
        log.info("getValueByKey, key: {}, value: {}", key, paramValue);
        return paramValue;
    }

}
