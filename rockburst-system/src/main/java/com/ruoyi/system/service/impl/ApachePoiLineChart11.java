package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.excel.ChartDataAll;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApachePoiLineChart11 {
    public XSSFWorkbook sssssss(XSSFWorkbook wb, String sheetName, List<String> titles, List<ChartDataAll> data1) {
        if (wb == null) {
            wb = new XSSFWorkbook(); // 只有第一次调用时才创建 Workbook
        }

        XSSFSheet sheet = wb.createSheet(sheetName); // 创建新的 Sheet 页

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
            Row titleRow = sheet.getRow(0);
            if (titleRow == null) {
                titleRow = sheet.createRow(0);
            }
            Cell titleCell = titleRow.createCell(i * titles.size() + i);

            sheet.addMergedRegion(new CellRangeAddress(0, 0, i * titles.size() + i, (i + 1) * titles.size() + i));

            titleCell.setCellStyle(getTitleStyle(wb));
            titleCell.setCellValue(data1.get(i).getTitle());

            Row row = sheet.getRow(1);
            if (row == null) {
                row = sheet.createRow(1);
            }
            Cell cell = row.createCell(i * titles.size() + i);
            cell.setCellValue("日期");
            for (int j = 0; j < titles.size(); j++) {
                cell = row.createCell(i * titles.size() + i + j + 1);
                cell.setCellValue(titles.get(j));
            }

            for (int k = 0; k < data1.get(i).getChartDataList().size(); k++) {
                Row r = sheet.getRow(k + 2);
                if (r == null) {
                    r = sheet.createRow(k + 2);
                }
                Cell c = r.createCell(i * titles.size() + i);
                c.setCellValue(data1.get(i).getChartDataList().get(k).getTitle());

                for (int j = 0; j < data1.get(i).getChartDataList().get(k).getData().size(); j++) {
                    c = r.createCell(i * titles.size() + i + j + 1);
                    c.setCellValue(data1.get(i).getChartDataList().get(k).getData().get(j));
                }
            }
        }

        // 处理最大值单元格
        for (Row row : sheet) {
            double maxValue = Double.MIN_VALUE;
            Cell maxCell = null;

            for (Cell cell : row) {
                cell.setCellStyle(borderCellStyle);
                if (cell.getCellType() == CellType.NUMERIC) {
                    double cellValue = cell.getNumericCellValue();
                    if (cellValue > maxValue) {
                        maxValue = cellValue;
                        maxCell = cell;
                    }
                }
            }

            if (maxCell != null) {
                maxCell.setCellStyle(redCellStyle);
            }
        }

        // 生成折线图
        for (int k = 0; k < data1.size(); k++) {
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5 + k * 30, 7, 26 + k * 30);
            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText(data1.get(k).getTitle());
            chart.setTitleOverlay(false);
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);
            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            XDDFDataSource<String> countries = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, 1, 1 + k * titles.size() + k, k * titles.size() + k + titles.size()));
            XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

            for (int i = 0; i < data1.get(k).getChartDataList().size(); i++) {
                XDDFNumericalDataSource<Double> area = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(i + 2, i + 2, 1 + k * titles.size() + k, k * titles.size() + k + titles.size()));
                XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) data.addSeries(countries, area);
                series1.setTitle(data1.get(k).getChartDataList().get(i).getTitle() + "值", null);
                series1.setSmooth(false);
                series1.setMarkerSize((short) 6);
                series1.setMarkerStyle(MarkerStyle.SQUARE);
            }
            chart.plot(data);
        }

        return wb; // 返回 Workbook，以便后续继续添加 Sheet
    }

    private static CellStyle getTitleStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
//        style.setAlignment(HorizontalAlignment.CENTER); // 水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 确保字体样式也应用
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
