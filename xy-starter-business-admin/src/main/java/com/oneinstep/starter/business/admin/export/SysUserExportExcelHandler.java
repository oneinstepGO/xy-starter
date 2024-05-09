package com.oneinstep.starter.business.admin.export;

import com.oneinstep.starter.core.excel.AbstractExcelHandler;
import org.springframework.stereotype.Component;

/**
 * 后台用户导出Excel处理器
 */
@Component
public class SysUserExportExcelHandler extends AbstractExcelHandler {

    @Override
    protected String[] titleArray() {
        return new String[]{
                "用户ID",
                "用户名",
                "昵称",
                "最后登录时间",
                "最后登录IP",
                "登录白名单",
                "创建者ID",
                "备注",
                "状态",
                "创建时间",
                "修改时间"
        };
    }

    @Override
    protected String sheetName() {
        return "sys_user";
    }

}
