package com.ruoyi.system.domain.excel;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.Cell;

public class CustomColumnOffsetHandler implements CellWriteHandler {

    private final int columnOffset; // 列偏移量

    // 构造器传入列偏移量
    public CustomColumnOffsetHandler(int columnOffset) {
        this.columnOffset = columnOffset;
    }

    @Override
    public void beforeCellCreate(CellWriteHandlerContext context) {
        // 修改列索引，使内容偏移到指定列
        context.getCellDataList().forEach(cellData -> {
            int currentColumnIndex = cellData.getColumnIndex();
            cellData.setColumnIndex(columnOffset);
        });

    }

    @Override
    public void afterCellCreate(CellWriteHandlerContext context) {
        // 在单元格创建后，如果需要更多操作，可以在这里添加逻辑

    }

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // 在单元格数据设置后执行，可以用于样式调整
        Cell cell = context.getCell();
        if (cell != null) {
            System.out.println("Cell written at row " + cell.getRowIndex() + ", column " + cell.getColumnIndex());
        }
    }
}