package com.oneinstep.starter.admin.controller.export;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneinstep.starter.business.admin.export.SysUserExportExcelHandler;
import com.oneinstep.starter.core.dao.PageConverter;
import com.oneinstep.starter.core.dao.PageData;
import com.oneinstep.starter.sys.bean.domain.SysUser;
import com.oneinstep.starter.sys.option.QueryAdminUserOption;
import com.oneinstep.starter.sys.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 管理员管理
 **/
@Controller
@RequestMapping("/admin/sys/adminUser")
@Slf4j
@Validated
public class SysUserExportController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysUserExportExcelHandler sysUserExportExcelHandler;

    /**
     * 导出管理员列表
     *
     * @param option   查询参数
     * @param response 响应
     */
    @GetMapping("export")
    public void export(QueryAdminUserOption option, HttpServletResponse response) {
        PageData<?> pageData = option.toPageData();
        pageData.setPageNo(1);
        pageData.setPageSize(60000);
        Page<SysUser> page = PageConverter.convert(pageData);

        QueryWrapper<SysUser> queryWrapper = option.toQueryWrapper();

        Page<SysUser> doPage = sysUserService.page(page, queryWrapper);

        List<SysUser> records = doPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        sysUserExportExcelHandler.export(records, response);
    }
}
