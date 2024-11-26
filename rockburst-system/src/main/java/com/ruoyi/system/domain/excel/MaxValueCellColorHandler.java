package com.ruoyi.system.domain.excel;

import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import org.apache.poi.ss.usermodel.*;

// 自定义样式处理器
public  class MaxValueCellColorHandler extends AbstractRowWriteHandler {
    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        Row row = context.getRow();
        if (row == null) return;

        double maxValue = Double.MIN_VALUE;
        Cell maxCell = null;

        // 找到最大值的单元格
        for (Cell cell : row) {
            try {
                double value = Double.parseDouble(cell.getStringCellValue());
                if (value > maxValue) {
                    maxValue = value;
                    maxCell = cell;
                }
            } catch (NumberFormatException e) {
                // 忽略非数值单元格
            }
        }

        // 设置最大值单元格为红色
        if (maxCell != null) {
            Workbook workbook = row.getSheet().getWorkbook();
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            maxCell.setCellStyle(cellStyle);
        }
    }
}