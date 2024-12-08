package com.ruoyi.web.controller.projectFill;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.system.domain.excel.ChartData;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.charts.XSSFChartLegend;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/export")
public class CharController {

    @Anonymous
    @ApiOperation("chart")
    @GetMapping("/line-chart")
    public void exportLineChart(HttpServletResponse response) throws IOException {
        // 模拟数据
        String[] categories = {"2m", "3m", "4m", "5m", "6m", "7m", "8m", "9m", "10m", "11m", "12m", "13m", "14m", "15m"};
        double[] warningValues = {3, 3, 3, 3, 3, 4, 4, 4, 6, 6, 6, 6, 8, 8};
        double[] march3Data = {2.1, 2.0, 2.2, 2.1, 2.0, 2.3, 2.1, 2.0, 2.4, 2.5, 2.6, 2.5, 2.7, 2.6};
        double[] march7Data = {2.0, 1.9, 2.0, 2.1, 2.0, 2.2, 2.0, 2.0, 2.3, 2.4, 2.5, 2.4, 2.6, 2.5};

        // 创建 Excel 工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Line Chart");

        // 创建数据表格
//        createDataTable(sheet, categories, warningValues, march3Data, march7Data);

        // 创建折线图
        createLineChart(sheet, categories.length);

        // 设置 HTTP 响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=line-chart.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void createDataTable(Sheet sheet, List<ChartData> datas) {

        Row headerRow = sheet.createRow(0);
        for (int c = 0; c < datas.get(0).getData().size(); c++) {
            headerRow.createCell(c).setCellValue(datas.get(0).getData().get(c));
        }

        for (int i = 1; i < datas.size(); i++) {
            Row row = sheet.createRow(i + 1);
            for (int c = 0; c < datas.get(i).getData().size(); c++) {
                row.createCell(c).setCellValue( datas.get(i).getData().get(c));
            }
        }
    }

    private void createLineChart(XSSFSheet sheet, int categoryCount) {
        Drawing<?> drawing = sheet.createDrawingPatriarch();
//        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 0, 20, 20);
        ClientAnchor anchor = sheet.getWorkbook().getCreationHelper().createClientAnchor();
        anchor.setCol1(0);  // 左上角列号
        anchor.setRow1(0); // 左上角行号
        anchor.setCol2(10); // 右下角列号
        anchor.setRow2(15); // 右下角行号
        XSSFChart chart = ((XSSFDrawing) drawing).createChart(anchor);
        chart.setTitleText("轨顺距离面");
        chart.setTitleOverlay(false);

        XSSFChartLegend legend = chart.getOrCreateLegend();
//        legend.setPosition(LegendPosition.BOTTOM); // 设置图例位置为底部

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("距离");

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("值");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // 数据范围
        XDDFDataSource<String> distances = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(1, categoryCount, 0, 0));
        XDDFNumericalDataSource<Double> warningData = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(1, categoryCount, 1, 1));
        XDDFNumericalDataSource<Double> march3Data = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(1, categoryCount, 2, 2));
        XDDFNumericalDataSource<Double> march7Data = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(1, categoryCount, 3, 3));

        // 添加系列
        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

        data.addSeries(distances, warningData).setTitle("预警值", null);
        data.addSeries(distances, march3Data).setTitle("11111", null);
        data.addSeries(distances, march7Data).setTitle("2222", null);

        chart.plot(data);

        // 设置样式
        XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) data.getSeries().get(0);
        series1.setSmooth(false); // 折线不平滑
        series1.setMarkerStyle(MarkerStyle.CIRCLE);

        XDDFLineChartData.Series series2 = (XDDFLineChartData.Series) data.getSeries().get(1);
        series2.setSmooth(false);
        series2.setMarkerStyle(MarkerStyle.TRIANGLE);

        XDDFLineChartData.Series series3 = (XDDFLineChartData.Series) data.getSeries().get(2);
        series3.setSmooth(false);
        series3.setMarkerStyle(MarkerStyle.SQUARE);
    }


    @Anonymous
    @ApiOperation("chart")
    @GetMapping("/line-sssssssss")
    public void sssssssss(HttpServletResponse response) throws IOException {
        // 模拟数据
        String[] categories = {"2m", "3m", "4m", "5m", "6m", "7m", "8m", "9m", "10m", "11m", "12m", "13m", "14m", "15m"};
        Double[] warningValues = {3.1, 3.1, 3.1, 3.1, 3.1, 4.1, 4.1, 4.1, 6.1, 6.1, 6.1, 6.1, 8.1, 8.1};
        Double[] march3Data = {2.1, 2.0, 2.2, 2.1, 2.0, 2.3, 2.1, 2.0, 2.4, 2.5, 2.6, 2.5, 2.7, 2.6};
        Double[] march4Data = {2.0, 19.0, 2.0, 2.1, 2.0, 2.2, 2.0, 2.0, 2.3, 2.4, 2.5, 2.4, 2.6, 2.5};
        Double[] march5Data = {2.0, 1.9, 2.0, 21.0, 2.0, 2.2, 2.0, 2.0, 2.3, 2.4, 2.5, 2.4, 2.6, 2.5};
        Double[] march6Data = {2.0, 1.9, 2.0, 2.1, 2.0, 22.0, 2.0, 2.0, 2.3, 2.4, 2.5, 2.4, 2.6, 2.5};
        Double[] march7Data = {2.0, 1.9, 2.0, 2.1, 2.0, 2.2, 2.0, 2.0, 23.0, 2.4, 2.5, 2.4, 2.6, 2.5};

        ChartData chartData1 = new ChartData();chartData1.setData(Arrays.asList(warningValues));chartData1.setTitle("warningValues");
        ChartData chartData3 = new ChartData();chartData3.setData(Arrays.asList(march3Data));chartData3.setTitle("march3Data");
        ChartData chartData4 = new ChartData();chartData4.setData(Arrays.asList(march4Data));chartData4.setTitle("march4Data");
        ChartData chartData5 = new ChartData();chartData5.setData(Arrays.asList(march5Data));chartData5.setTitle("march5Data");
        ChartData chartData6 = new ChartData();chartData6.setData(Arrays.asList(march6Data));chartData6.setTitle("march6Data");
        ChartData chartData7 = new ChartData();chartData7.setData(Arrays.asList(march7Data));chartData7.setTitle("march7Data");
        List<ChartData> chartDatas = new ArrayList<>();
        chartDatas.add(chartData1);
        chartDatas.add(chartData3);
        chartDatas.add(chartData4);
        chartDatas.add(chartData5);
        chartDatas.add(chartData6);
        chartDatas.add(chartData7);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Line Chart");
        createDataTable(sheet,chartDatas);
        sss(sheet,chartDatas,warningValues.length,response);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=line-chart.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    void sss(XSSFSheet sheet,List<ChartData> datas,int categoryCount,HttpServletResponse response) throws IOException {


        Drawing<?> drawing = sheet.createDrawingPatriarch();
//        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 0, 20, 20);
        ClientAnchor anchor = sheet.getWorkbook().getCreationHelper().createClientAnchor();
        anchor.setCol1(0);  // 左上角列号
        anchor.setRow1(0); // 左上角行号
        anchor.setCol2(10); // 右下角列号
        anchor.setRow2(15); // 右下角行号
        XSSFChart chart = ((XSSFDrawing) drawing).createChart(anchor);
        chart.setTitleText("轨顺距离面");
        chart.setTitleOverlay(false);
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("距离");

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("值");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        // 添加系列
        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        XDDFDataSource<String> distances = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(1, categoryCount,0, 0));
        for (int i = 1; i < datas.size(); i++) {
            XDDFNumericalDataSource<Double> dataSource = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                    new CellRangeAddress(1, categoryCount, i, i));
            data.addSeries(distances, dataSource).setTitle(datas.get(i).getTitle(), null);
        }
        chart.plot(data);


    }
}
