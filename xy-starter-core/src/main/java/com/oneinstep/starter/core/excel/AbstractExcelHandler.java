package com.oneinstep.starter.core.excel;

import com.oneinstep.starter.core.utils.DateTimeUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public abstract class AbstractExcelHandler {

    /**
     * 导出excel
     *
     * @param dataList 数据
     * @param response 响应
     */
    public void export(List<?> dataList, HttpServletResponse response) {
        if (CollectionUtils.isEmpty(dataList)) {
            log.info("data is null or empty");
            return;
        }

        String sheetName = this.sheetName();
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            Cell cell;
            Row headerRow = sheet.createRow(0);
            // 设置标题列
            String[] titleArray = this.titleArray();
            for (int i = 0; i < titleArray.length; i++) {
                cell = headerRow.createCell(i);
                cell.setCellValue(titleArray[i]);
            }

            for (int i = 0; i < dataList.size(); i++) {
                Object data = dataList.get(i);
                Field[] declaredFields = data.getClass().getDeclaredFields();
                Row dataRow = sheet.createRow(i + 1);
                for (Field declaredField : declaredFields) {
                    declaredField.setAccessible(true);
                    ExcelCell annotation = declaredField.getAnnotation(ExcelCell.class);
                    if (annotation != null) {
                        int index = annotation.value();
                        if (index < 0 || index >= titleArray.length) {
                            log.error("Sheet {} index:{} out of range:{}", this.sheetName(), index, titleArray.length);
                            continue;
                        }

                        //先判断值
                        Object value = declaredField.get(data);
                        if (annotation.longToMoney() && value instanceof Long longNumber) {
                            value = convertMoney(longNumber);
                        }
                        if (annotation.decimalToMoney() && value instanceof BigDecimal bd) {
                            value = convertMoney(bd);
                        }
                        if (annotation.isLocalDate() && value instanceof LocalDate date) {
                            sheet.setColumnWidth(index, 256 * 20);
                            value = DateTimeUtil.formatDate_YYYY_MM_DD(date);
                        }
                        if (annotation.isLocalDateTime() && value instanceof LocalDateTime date) {
                            sheet.setColumnWidth(index, 256 * 20);
                            value = DateTimeUtil.formatDateTime_YYYY_MM_DD_HH_MM_SS(date);
                        }
                        if (annotation.isEnum() && annotation.enumClass() != null && value instanceof Integer code) {
                            Class<? extends Enum> statusEnumClass = annotation.enumClass();
                            Method getDescByCode = statusEnumClass.getMethod("getDescByCode", Integer.class);
                            value = getDescByCode.invoke(null, code);
                        }
                        //其他的情况都转化为字符类型
                        dataRow.createCell(index).setCellValue(value == null ? "" : String.valueOf(value));
                    }
                }
            }

            // 设置响应头，告诉浏览器返回的是一个Excel文件
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + sheetName + ".xls");

            // 将工作簿写入响应流中
            workbook.write(response.getOutputStream());

        } catch (Exception e) {
            log.error("exception when generate:{}", sheetName, e);
        }

    }

    protected abstract String[] titleArray();

    protected abstract String sheetName();


    private static String convertMoney(BigDecimal sourceAmount) {
        if (sourceAmount == null) {
            return "";
        }
        return sourceAmount.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).toString();
    }

    private static String convertMoney(Long sourceAmount) {
        if (sourceAmount == null) {
            return "";
        }

        return convertMoney(new BigDecimal(sourceAmount));
    }
}
