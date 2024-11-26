package com.ruoyi.system.domain.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * 自定义 CellWriteHandler 用于控制单元格样式。
 */
public class CustomCellWriteHandler implements CellWriteHandler {


    private final int num;

    public CustomCellWriteHandler(int num) {
        this.num = num;
    }

    @Override
    public void beforeCellCreate(CellWriteHandlerContext context) {
//        CellWriteHandler.super.beforeCellCreate(context);

        Sheet sheet = context.getWriteSheetHolder().getSheet();
        Row row = context.getRow();

        if (context.getRowIndex() == 0) {
            Head head = context.getHeadData();
            List<String> headData = head.getHeadNameList();
            // 合并表头单元格
            if (headData != null && !headData.isEmpty()) {
                if(context.getColumnIndex() == 0){
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (context.getColumnIndex()+1) * num-1));  // 合并第0行的0到14列（A到O列）
                    Cell cell = row.createCell(0); // 在第0列创建单元格
                    System.out.println("headData.get(0) = " + headData.get(0));
                    cell.setCellValue(headData.get(0));
                }else {
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, context.getColumnIndex()*num, (context.getColumnIndex()+1) * num-1));  // 合并第0行的0到14列（A到O列）
                    Cell cell = row.createCell(context.getColumnIndex()*num); // 在第0列创建单元格
                    System.out.println("headData.get(0) = " + headData.get(0));
                    cell.setCellValue(headData.get(0));
                }

            }
        }


    }


    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row,
                                 Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {


        // 该方法在单元格创建之前调用，可以进行一些初始化设置，通常样式设置放在 afterCellCreate 中
    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell,
                                Head head, Integer relativeRowIndex, Boolean isHead) {
        Sheet sheet = writeSheetHolder.getSheet();




        // 该方法在单元格创建之后调用，可以在此设置单元格样式
        Workbook workbook = sheet.getWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();

        // 设置单元格边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        // 设置单元格内容居中（水平和垂直居中）
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 设置单元格背景颜色（浅黄色）
//        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 应用样式到单元格
        cell.setCellStyle(cellStyle);
        Row row = cell.getRow();

        // 可选：设置行高
        if (row.getHeight() < 400) {
            row.setHeight((short) 400);  // 如果当前行高小于 400，设置为 400
        }

        // 可选：设置字体样式（例如：加粗）
        Font font = workbook.createFont();
//        font.setBold(true);
        cellStyle.setFont(font);
    }

    @Override
    public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                       WriteCellData<?> cellData, Cell cell, Head head, Integer relativeRowIndex,
                                       Boolean isHead) {
        // 该方法在单元格数据转换之后调用
        // 可以在此进一步修改单元格的内容或格式
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                 List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex,
                                 Boolean isHead) {
        // 该方法在单元格所有操作完成后调用
        // 可用于做一些最终的处理
    }
}
