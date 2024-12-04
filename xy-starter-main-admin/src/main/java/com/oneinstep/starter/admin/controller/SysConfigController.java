package com.oneinstep.starter.admin.controller;

import com.oneinstep.starter.core.log.annotition.Logging;
import com.oneinstep.starter.core.response.Result;
import com.oneinstep.starter.sys.bean.domain.SysConfig;
import com.oneinstep.starter.sys.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统参数
 **/
@Slf4j
@RestController
@RequestMapping("/admin/sys/config")
@Tag(name = "系统参数 管理")
public class SysConfigController {

    @Resource
    private SysConfigService sysConfigService;

    /**
     * 获取所有的系统配置
     *
     * @return 所有的系统配置
     */
    @GetMapping("listAll")
    @Operation(summary = "系统配置列表", description = "获取所有的系统配置，无分页")
    @Logging(printArgs = true, printError = true)
    public Result<Map<String, String>> listAll() {
        List<SysConfig> boList = sysConfigService.listAllWithCache();
        if (CollectionUtils.isEmpty(boList)) {
            return Result.ok(new HashMap<>());
        }

        Map<String, String> map = new HashMap<>(boList.size());
        boList.forEach(bo -> map.put(bo.getParamKey(), bo.getParamValue()));

        return Result.ok(map);
    }

    /**
     * 更新系统配置
     *
     * @param configMap 配置
     * @return 是否更新成功
     */
    @PostMapping("update")
    @Operation(summary = "更新系统配置", description = "更新系统配置")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<Void> update(@Valid @NotEmpty(message = "配置不能为空") @RequestBody Map<String, String> configMap) {
        return sysConfigService.updateConfigByKey(configMap) ? Result.ok() : Result.error();
    }

}
