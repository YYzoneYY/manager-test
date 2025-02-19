package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.excel.ChartDataAll;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 工程填报记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
public class ApachePoiLineChart
{
    public void sssssss(XSSFWorkbook wb , String sheetName, List<String> titles, List<ChartDataAll> data1, HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=example.xlsx");
        FileOutputStream fileOut = null;
//        XSSFWorkbook wb = new XSSFWorkbook();
        if (wb == null) {
            wb = new XSSFWorkbook(); // 只有第一次调用时才创建 Workbook
        }

//        String sheetName = "Sheet1";
        XSSFSheet sheet = wb.createSheet(sheetName);


        // 创建红色背景的单元格样式
        CellStyle redCellStyle = wb.createCellStyle();
        redCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        redCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 创建黑色边框的单元格样式
        CellStyle borderCellStyle = wb.createCellStyle();
        borderCellStyle.setBorderTop(BorderStyle.THIN);
        borderCellStyle.setBorderBottom(BorderStyle.THIN);
        borderCellStyle.setBorderLeft(BorderStyle.THIN);
        borderCellStyle.setBorderRight(BorderStyle.THIN);




        for (int i = 0; i < data1.size(); i++) {
            Row titleRow = null;
            if(i == 0){
                titleRow = sheet.createRow(0);
            }else {
                titleRow = sheet.getRow(0);
            }
            Cell titleCell = titleRow.createCell(i*titles.size()+i);
            titleCell.setCellValue(data1.get(i).getTitle());
            sheet.addMergedRegion(new CellRangeAddress(0, 0, i*titles.size()+i, (i+1)*titles.size()+i));
            titleCell.setCellStyle(getTitleStyle(wb));
            Row row = null;
            if(i == 0){
                row = sheet.createRow(1);
            }else {
                row = sheet.getRow(1);
            }
            Cell cell = row.createCell(i*titles.size()+i);
            cell.setCellValue("日期");
            for (int j = 0; j < titles.size(); j++) {
                cell = row.createCell(i*titles.size()+i+j+1);
                cell.setCellValue(titles.get(j));
            }

            System.out.println("i = " + i);
            for (int k = 0; k < data1.get(i).getChartDataList().size(); k++) {
                Row r = null;
                Cell c = null;
                if(i == 0){
                    r = sheet.createRow(k + 2);
                    c = r.createCell(i*titles.size()+i);
                }else {
                    r = sheet.getRow(k+2);
                    if (r == null) {
                        r = sheet.createRow(k + 2);
                    }
                    c = r.createCell(i*titles.size()+i);
                }
                c.setCellValue(data1.get(i).getChartDataList().get(k).getTitle());
                for (int j = 0; j < data1.get(i).getChartDataList().get(k).getData().size(); j++) {
                    c = r.createCell(i*titles.size()+i+j+1);
                    c.setCellValue(data1.get(i).getChartDataList().get(k).getData().get(j));
                }
            }
        }

        // 创建一个单元格样式，用于标记最大值单元格（红色背景）
        CellStyle maxValueStyle = wb.createCellStyle();
        maxValueStyle.setFillForegroundColor(IndexedColors.RED.getIndex());  // 设置红色
        maxValueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);  // 设置填充样式为实心

        // 遍历每一行
        for (Row row : sheet) {
            double maxValue = Double.MIN_VALUE;  // 用于存储该行的最大值
            Cell maxCell = null;  // 用于存储最大值单元格的引用

            // 遍历每行中的单元格，找到最大值
            for (Cell cell : row) {
                cell.setCellStyle(borderCellStyle);

                if (cell.getCellType() == CellType.NUMERIC) {
                    double cellValue = cell.getNumericCellValue();
                    if (cellValue > maxValue) {
                        maxValue = cellValue;
                        maxCell = cell;  // 更新最大值单元格
                    }
                }
            }

            // 如果找到最大值单元格，将其背景色设为红色
            if (maxCell != null) {
                maxCell.setCellStyle(redCellStyle);
            }
        }


        for (int k = 0; k < data1.size(); k++) {
// 创建一个画布
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            // 前四个默认0，[0,5]：从0列5行开始;[7,26]:到7列26行结束
            // 默认宽度(14-8)*12
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5+k*30, 7, 26+k*30);
            // 创建一个chart对象
            XSSFChart chart = drawing.createChart(anchor);
            // 标题
            chart.setTitleText(data1.get(k).getTitle());
            // 标题覆盖
            chart.setTitleOverlay(false);
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);
            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            XDDFDataSource<String> countries = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, 1, 1+k*titles.size()+k, k*titles.size()+k+titles.size()));
            XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

            for (int i = 0; i < data1.get(k).getChartDataList().size(); i++) {
                XDDFNumericalDataSource<Double> area = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(i+2, i+2, 1+k*titles.size()+k, k*titles.size()+k+titles.size()));
                XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) data.addSeries(countries, area);
                // 折线图例标题
                String title = data1.get(k).getChartDataList().get(i).getTitle();
                System.out.println("title = " + title);
                series1.setTitle(String.valueOf(title)+"值", null);
                // 直线
                series1.setSmooth(false);
                // 设置标记大小
                series1.setMarkerSize((short) 6);
                // 设置标记样式，星星
                series1.setMarkerStyle(MarkerStyle.SQUARE);
            }
            chart.plot(data);
        }
        // 写入到响应流，而不是保存到本地文件
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            wb.write(byteArrayOutputStream);
            byte[] excelData = byteArrayOutputStream.toByteArray();

            // 设置响应内容长度
            response.setContentLength(excelData.length);

            // 将生成的 Excel 文件通过响应流发送到客户端
            response.getOutputStream().write(excelData);
        }

        // 关闭工作簿
        wb.close();
    }


    private static CellStyle getTitleStyle(XSSFWorkbook wb){
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}






