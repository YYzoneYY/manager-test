package com.ruoyi.web.controller.projectFill;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class MultiChartExample  {

    public static void main(String[] args) throws IOException {
        // 创建一个 XSSFWorkbook 工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 创建一个工作表
        XSSFSheet sheet = workbook.createSheet("Charts");

        // 添加数据
        addSampleData(sheet);

        // 创建图表
        XSSFDrawing drawing = sheet.createDrawingPatriarch();

        // 创建第一个图表
        createChart(sheet, drawing, 0, 15, 0, 10, "Chart 1");

        // 创建第二个图表
        createChart(sheet, drawing, 0, 15, 15, 25, "Chart 2");

        // 保存到文件
        try (FileOutputStream fileOut = new FileOutputStream("LineCharts.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();
        System.out.println("Excel 文件生成完成！");
    }

    // 添加样本数据
    private static void addSampleData(XSSFSheet sheet) {
        String[] headers = {"Category", "Series 1", "Series 2"};
        String[] categories = {"A", "B", "C", "D", "E"};
        double[] series1Values = {10, 20, 30, 40, 50};
        double[] series2Values = {50, 40, 30, 20, 10};

        // 写入标题行
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 写入数据行
        for (int i = 0; i < categories.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(categories[i]);      // 分类
            row.createCell(1).setCellValue(series1Values[i]);   // 系列 1
            row.createCell(2).setCellValue(series2Values[i]);   // 系列 2
        }
    }

    // 创建折线图
    private static void createChart(XSSFSheet sheet, XSSFDrawing drawing,
                                    int dataStartRow, int dataEndRow,
                                    int startCol, int endCol,
                                    String chartTitle) {
        // 图表的位置
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, startCol, dataEndRow + 2, endCol, dataEndRow + 12);

        // 创建图表对象
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText(chartTitle);
        chart.setTitleOverlay(false);

        // 设置轴
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Category Axis");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Value Axis");

        // 定义数据源
        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(dataStartRow + 1, dataEndRow, 0, 0));
        XDDFNumericalDataSource<Double> values1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(dataStartRow + 1, dataEndRow, 1, 1));
        XDDFNumericalDataSource<Double> values2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(dataStartRow + 1, dataEndRow, 2, 2));

        // 创建折线图数据
        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

        // 添加系列到数据
        XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) data.addSeries(categories, values1);
        series1.setTitle("Series 1", null);
        series1.setSmooth(false);
        series1.setMarkerStyle(MarkerStyle.CIRCLE);

        XDDFLineChartData.Series series2 = (XDDFLineChartData.Series) data.addSeries(categories, values2);
        series2.setTitle("Series 2", null);
        series2.setSmooth(true);
        series2.setMarkerStyle(MarkerStyle.SQUARE);

        // 绘制数据
        chart.plot(data);

        // 设置图例位置
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);
    }
}
