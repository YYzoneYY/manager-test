package com.ruoyi.system.domain.excel;

import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import org.apache.poi.ss.usermodel.*;

public class CustomDataWriteHandler extends AbstractRowWriteHandler {

    private final int startRow; // 起始行
    private final int startColumn; // 起始列

    public CustomDataWriteHandler(int startRow, int startColumn) {
        this.startRow = startRow;
        this.startColumn = startColumn;
    }

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        Row row = context.getRow();
        if (row == null) return;

        // 调整起始行和列
        for (Cell cell : row) {
            int adjustedRowIndex = row.getRowNum() + startRow;
            int adjustedColumnIndex = cell.getColumnIndex() + startColumn;

            // 创建新的单元格
            Cell newCell = cell.getSheet().getRow(adjustedRowIndex).createCell(adjustedColumnIndex);
            newCell.setCellValue(cell.getStringCellValue());
        }
    }
}
