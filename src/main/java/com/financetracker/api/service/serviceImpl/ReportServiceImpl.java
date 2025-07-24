package com.financetracker.api.service.serviceImpl;

import com.financetracker.api.dto.*;
import com.financetracker.api.repository.SummaryRepository;
import com.financetracker.api.security.Jwt.util.JwtTokenUtil;
import com.financetracker.api.service.ReportService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final SummaryRepository summaryRepository;

    @Autowired
    public ReportServiceImpl(SummaryRepository summaryRepository){
        this.summaryRepository = summaryRepository;
    }

    public static long getUserID(){
        return JwtTokenUtil.getCurrentUserId();
    }

    public static String formatMonth(int monthNumber) {
        return Month.of(monthNumber).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    // hàm chuyển tháng số sang tháng chữ
    public static String getMonthName(int monthNum){
        String monthStr = monthNum > 0 && monthNum <= 12
                ? Month.of(monthNum).getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase()
                : "N/A";
        return monthStr;
    }

    // định dạng tháng, năm   .return April 2025
    public static String getMonthYearName(int month, int year) {
        return Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year;
    }

    @Override
    public MonthlyReportDTO getMonthlyReportbyYear( int year ,int monthtxt){
        long userId = getUserID();
        List<ChartItemDTO> chartItems = summaryRepository.getMonthlyReport(userId, year).stream()
                .map(cols -> {

                    int monthNum = cols[0] != null ? ((Number) cols[0]).intValue() : 0;
                    Double income = cols[1] != null ? ((Number) cols[1]).doubleValue() : 0.0;
                    Double expense = cols[2] != null ? ((Number) cols[2]).doubleValue() : 0.0;

                    return new ChartItemDTO(getMonthName(monthNum), income, expense);
                })
                .toList();

        ChartItemResponseDTO chartItemDTORepon = chartItems.stream()
                .filter(item -> item.getMonth().equals(getMonthName(monthtxt)))
                .findFirst()
                .map(item -> new ChartItemResponseDTO(item.getMonth(), item.getIncome(), item.getExpense()))
                .orElse(new ChartItemResponseDTO(getMonthName(monthtxt), 0.0, 0.0)); // fallback nếu không có dữ liệu



        chartItemDTORepon.setMonth(getMonthYearName(monthtxt,year));

        return new MonthlyReportDTO(chartItems,chartItemDTORepon);
    }

    @Override
    public SummaryReportDTO getMonthlyReportAndTop3Expenses(int year ,int monthtxt){
        long userId = getUserID();

        //get top 3
        List<TopCategoryExpenses> topCategoryExpensesList = summaryRepository.getTopCategoryExpenses(userId, monthtxt, year).stream()
                .limit(3)
                .toList();

        ChartItemDTO chartItemDTO = summaryRepository.getMonthlyReport(userId, year).stream()
                .map(cols -> {

                    int monthNum = cols[0] != null ? ((Number) cols[0]).intValue() : 0;
                    Double income = cols[1] != null ? ((Number) cols[1]).doubleValue() : 0.0;
                    Double expense = cols[2] != null ? ((Number) cols[2]).doubleValue() : 0.0;

                    return new ChartItemDTO(formatMonth(monthNum), income, expense);
                })
                .filter(item -> item.getMonth().equals(formatMonth(monthtxt)))
                .findFirst()
                .orElse(new ChartItemDTO(getMonthYearName(monthtxt, year), 0.0, 0.0));

        return new SummaryReportDTO(chartItemDTO.getMonth(), year, chartItemDTO.getIncome(), chartItemDTO.getExpense(), topCategoryExpensesList);

    }


    @Override
    public String exportPDF(ReportExportRequest request){
        long userid = getUserID();
        switch (request.getReportType()){
            case SUMMARY ->
            {
                //get top 3
                List<TopCategoryExpenses> topCategoryExpensesList = summaryRepository.getTopCategoryExpenses(userid, request.getMonth(), request.getYear()).stream()
                        .toList();

                // get chartItemDTOnow
                List<ChartItemDTO> chartItems = summaryRepository.getMonthlyReport(userid, request.getYear()).stream()
                        .map(cols -> {

                            int monthNum = cols[0] != null ? ((Number) cols[0]).intValue() : 0;
                            Double income = cols[1] != null ? ((Number) cols[1]).doubleValue() : 0.0;
                            Double expense = cols[2] != null ? ((Number) cols[2]).doubleValue() : 0.0;

                            return new ChartItemDTO(getMonthName(monthNum), income, expense);
                        })
                        .toList();

                // xử lý tháng năm
                int currentMonth = request.getMonth();
                int currentYear = request.getYear();

                int previousMonth = currentMonth == 1 ? 12 : currentMonth - 1;
                int previousYear = currentMonth == 1 ? currentYear - 1 : currentYear;

                ChartItemDTO chartItemDTOnow = chartItems.stream()
                        .filter(item -> item.getMonth().equals(getMonthName(currentMonth)))
                        .findFirst()
                        .orElse(new ChartItemDTO(getMonthYearName(currentMonth, currentYear), 0.0, 0.0));
                chartItemDTOnow.setMonth(getMonthYearName(currentMonth,currentYear));

                ChartItemDTO chartItemDTObefore =chartItems.stream()
                        .filter(item -> item.getMonth().equals(getMonthName(previousMonth)))
                        .findFirst()
                        .orElse(new ChartItemDTO(getMonthYearName(previousMonth, previousYear), 0.0, 0.0));
                chartItemDTObefore.setMonth(getMonthYearName(previousMonth,previousYear));
// Tạo tên file PDF
                String baseFileName = "user" + userid + "_summary_" + request.getMonth() + "_" + request.getYear() + ".pdf";

// Tạo thư mục lưu file ở ngoài static
                String folderPath = new File("uploads/reports").getAbsolutePath();
                File folder = new File(folderPath);
                if (!folder.exists()) folder.mkdirs();

// Xử lý trùng tên file: thêm (1), (2), ...
                String fullPath = getAvailableFilePath(folderPath, baseFileName);

                try {
                    generatePdfWithCharts(chartItemDTOnow, topCategoryExpensesList, chartItemDTObefore, request, fullPath);
                } catch (Exception e) {
                    throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
                }

// Trả về đường dẫn API để tải file
                String fileNamePublic = new File(fullPath).getName();
                String publicURL = "/api/reports/download/" + fileNamePublic;

                return "http://localhost:8080"+ publicURL;
            }

            case MONTHLY -> {
                MonthlyReportDTO monthlyReportDTO = getMonthlyReportbyYear(request.getYear(),request.getMonth());
                // Tạo tên file PDF
                String baseFileName = "user" + userid + "_monthly_" + request.getMonth() + "_" + request.getYear() + ".pdf";

// Tạo thư mục lưu file ở ngoài static
                String folderPath = new File("uploads/reports").getAbsolutePath();
                File folder = new File(folderPath);
                if (!folder.exists()) folder.mkdirs();

// Xử lý trùng tên file: thêm (1), (2), ...
                String fullPath = getAvailableFilePath(folderPath, baseFileName);

                try {
                    generatePdfWithCharttypeMONTHLY(monthlyReportDTO,request,fullPath);
                } catch (Exception e) {
                    throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
                }

// Trả về đường dẫn API để tải file
                String fileNamePublic = new File(fullPath).getName();
                String publicURL = "/api/reports/download/" + fileNamePublic;

                return "http://localhost:8080"+ publicURL;

            }


            case CATEGORY -> {
                List<TopCategoryIncome> income = summaryRepository.getTopCategoryImcome(userid, request.getMonth(), request.getYear());
                List<TopCategoryExpenses> expenses = summaryRepository.getTopCategoryExpenses(userid, request.getMonth(), request.getYear());

                // Tạo tên file PDF
                String baseFileName = "user" + userid + "_category_" + request.getMonth() + "_" + request.getYear() + ".pdf";

// Tạo thư mục lưu file ở ngoài static
                String folderPath = new File("uploads/reports").getAbsolutePath();
                File folder = new File(folderPath);
                if (!folder.exists()) folder.mkdirs();

// Xử lý trùng tên file: thêm (1), (2), ...
                String fullPath = getAvailableFilePath(folderPath, baseFileName);

                try {
                    generatePdfWithCharttypeCATEGORY(expenses,income,request,fullPath);
                } catch (Exception e) {
                    throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
                }

// Trả về đường dẫn API để tải file
                String fileNamePublic = new File(fullPath).getName();
                String publicURL = "/api/reports/download/" + fileNamePublic;

                return "http://localhost:8080"+ publicURL;
            }

            default -> throw new IllegalArgumentException("Unsupported report type: " + request.getReportType());

        }



    }

    private String getAvailableFilePath(String folderPath, String baseFileName) {
        File file = new File(folderPath, baseFileName);
        String name = baseFileName;
        String fileNameWithoutExt = name.substring(0, name.lastIndexOf("."));
        String ext = name.substring(name.lastIndexOf("."));
        int count = 1;

        while (file.exists()) {
            name = fileNameWithoutExt + "(" + count + ")" + ext;
            file = new File(folderPath, name);
            count++;
        }

        return file.getAbsolutePath();
    }

    //tạo PDF type SUMMARY
    public static void generatePdfWithCharts(
            ChartItemDTO expensemonth,
            List<TopCategoryExpenses> allExpenses  ,
            ChartItemDTO Expenseslastmonth,
            ReportExportRequest request,
            String outputPath
    )throws Exception {
        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("📊 Financial Summary Report - " + '"' + expensemonth.getMonth() + '"')
                .setFontSize(28).setBold().setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Budget Summary Report").setFontSize(18).setBold());
        // Tổng hợp
        float[] columnWidths = {100F, 400F};
        Table table = new Table(columnWidths);

        Cell header1 = new Cell().add(new Paragraph("Indicator").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Cell header2 = new Cell().add(new Paragraph("Value ($)").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);

        table.addHeaderCell(header1);
        table.addHeaderCell(header2);
        table.addCell("Income");
        table.addCell(formatUSD(expensemonth.getIncome()));

        table.addCell("Expenses");
        table.addCell(formatUSD(expensemonth.getExpense()));

        table.addCell("Balance");
        table.addCell(formatUSD((expensemonth.getIncome()-expensemonth.getExpense())));

// Trạng thái ngân sách
        String status = (expensemonth.getIncome()-expensemonth.getExpense()) >= 0 ? "Surplus" : "Deficit"; //Dư/Thiếu
        table.addCell("Status");
        table.addCell(status);

        document.add(table);
        document.add(new Paragraph(" ")); // dòng trống
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // top 3 categories
        // check res top 3
        if(request.getIncludeTopExpenses()){
            document.add(new Paragraph("Top 3 Expenses").setFontSize(18).setBold());
            // Tổng hợp
            float[] columnWidthsTop = {300F ,100F, 400F};
            Table tableTop3Expenses = new Table(columnWidthsTop);

            Cell headerCategory = new Cell().add(new Paragraph("Category").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Cell headerAmount = new Cell().add(new Paragraph("Amount").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Cell headerPercentage = new Cell().add(new Paragraph("Percentage").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            tableTop3Expenses.addHeaderCell(headerCategory);
            tableTop3Expenses.addHeaderCell(headerAmount);
            tableTop3Expenses.addHeaderCell(headerPercentage);


            // Tính tổng amount cua list
            double totalAmount = allExpenses.stream()
                    .mapToDouble(TopCategoryExpenses::getAmount)
                    .sum();

            // lấy top 3
            List<TopCategoryExpenses> top3 = allExpenses.stream()
                    .sorted((a, b) -> Double.compare(b.getAmount(), a.getAmount()))
                    .limit(3)
                    .toList();

             // tính % và addcell
            for (TopCategoryExpenses item : top3) {
                double percent = (item.getAmount() / totalAmount) * 100;
                tableTop3Expenses.addCell( item.getCategory() );
                tableTop3Expenses.addCell(formatUSD(item.getAmount()));
                tableTop3Expenses.addCell(String.format("%.2f%%\n", percent));

            }


            document.add(tableTop3Expenses);
            document.add(new Paragraph(" ")); // dòng trống
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

        }
        //so sanh thang truoc
        if(Expenseslastmonth == null ){
            document.add(new Paragraph(" tháng trước rỗng"));
        }
        else {
            float[] columnWidthss = {200F, 100F,100F,400F};
            Table tabless = new Table(columnWidthss);
            document.add(new Paragraph("Compare last month's spending").setFontSize(18).setBold());

            Cell header1ss = new Cell().add(new Paragraph("Month").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Cell header2ss = new Cell().add(new Paragraph("Income").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Cell header3ss = new Cell().add(new Paragraph("Expenses").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Cell header4ss = new Cell().add(new Paragraph("Balance").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            tabless.addHeaderCell(header1ss);
            tabless.addHeaderCell(header2ss);
            tabless.addHeaderCell(header3ss);
            tabless.addHeaderCell(header4ss);

// add tháng trước
            tabless.addCell(Expenseslastmonth.getMonth());
            tabless.addCell(formatUSD(Expenseslastmonth.getIncome()));
            tabless.addCell(formatUSD(Expenseslastmonth.getExpense()));
            tabless.addCell(formatUSD(Expenseslastmonth.getIncome()-Expenseslastmonth.getExpense()));

            // add tháng này
//            tabless.addCell(Expenseslastmonth.getMonth());
            tabless.addCell(expensemonth.getMonth());
            tabless.addCell(formatUSD(expensemonth.getIncome()));
            tabless.addCell(formatUSD(expensemonth.getExpense()));
            tabless.addCell(formatUSD(expensemonth.getIncome() - expensemonth.getExpense()));
            document.add(tabless);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));


        }
        // kết thúc trang, qua trang mới
        document.add(new AreaBreak());

        // check res vẽ cột
        if(request.getIncludeChart()) {
            // vẽ biểu đồ cột
            // Biểu đồ cột
            document.add(new Paragraph("BarChart").setFontSize(18).setBold());
            BufferedImage barChart = createBarChart(expensemonth.getIncome(), expensemonth.getExpense());
            ByteArrayOutputStream chartStream = new ByteArrayOutputStream();
            ImageIO.write(barChart, "png", chartStream);
            ImageData chartData = ImageDataFactory.create(chartStream.toByteArray());
            document.add(new Image(chartData));


            // vẽ biểu đồ ngang

            document.add(new Paragraph("HorizontalBarChart").setFontSize(18).setBold());
            BufferedImage barChartn = createHorizontalBarChart(allExpenses);
            ByteArrayOutputStream chartStreamn = new ByteArrayOutputStream();
            ImageIO.write(barChartn, "png", chartStreamn);
            ImageData chartDatan = ImageDataFactory.create(chartStreamn.toByteArray());
            document.add(new Image(chartDatan));

            document.add(new Paragraph(" "));

        }
        document.close();

    }

    //PDF type MONTHLY
    public static void generatePdfWithCharttypeMONTHLY(
            MonthlyReportDTO monthly  ,
            ReportExportRequest request,
            String outputPath
    )throws Exception {
        List<ChartItemDTO> list = monthly.getCharts();

        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("📊 Financial MONTHLY Report "+ '"' + request.getYear() + '"')
                .setFontSize(28).setBold().setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Monthly Report").setFontSize(18).setBold());
        // Tổng hợp
        float[] columnWidths = {100F, 200F,200F};
        Table table = new Table(columnWidths);

        Cell header1 = new Cell().add(new Paragraph("Month").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Cell header2 = new Cell().add(new Paragraph("Income").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Cell header3 = new Cell().add(new Paragraph("Expense").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        table.addHeaderCell(header1);
        table.addHeaderCell(header2);
        table.addHeaderCell(header3);

        for (ChartItemDTO item : list) {
            table.addCell( item.getMonth() );
            table.addCell(formatUSD(item.getIncome()));
            table.addCell(formatUSD(item.getExpense()));

        }
        document.add(table);
        // vẽ biểu đồ
        if(request.getIncludeChart()){
            document.add(new Paragraph("BarChart").setFontSize(18).setBold());
            BufferedImage barChart = createBarChartFor12Months(list);
            ByteArrayOutputStream chartStream = new ByteArrayOutputStream();
            ImageIO.write(barChart, "png", chartStream);
            ImageData chartData = ImageDataFactory.create(chartStream.toByteArray());
            document.add(new Image(chartData));
        }
        document.close();

    }


    public static void generatePdfWithCharttypeCATEGORY(
            List<TopCategoryExpenses> categoryExpenses  ,
            List<TopCategoryIncome> categoryIncome,
            ReportExportRequest request,
            String outputPath
    )throws Exception {
        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("📊 Financial CATEGORY Report "+ '"' + getMonthYearName(request.getMonth(), request.getYear()) + '"')
            .setFontSize(28).setBold().setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Report EXPENSE").setFontSize(18).setBold());
        if(request.getIncludeChart()){
            document.add(new Paragraph("Pie Chart").setFontSize(18).setBold());
            Map<String, Double> topExpensesMap = categoryExpenses.stream()
                    .collect(Collectors.toMap(
                            TopCategoryExpenses::getCategory,
                            TopCategoryExpenses::getAmount
                    ));

            BufferedImage Chart = createPieChart(topExpensesMap);
            ByteArrayOutputStream chartStream = new ByteArrayOutputStream();
            ImageIO.write(Chart, "png", chartStream);
            ImageData chartData = ImageDataFactory.create(chartStream.toByteArray());
            document.add(new Image(chartData));
        }
        document.add(new AreaBreak() );
        document.add(new Paragraph("Report INCOME").setFontSize(18).setBold());


        if(request.getIncludeChart()){
            document.add(new Paragraph("Pie Chart").setFontSize(18).setBold());

            Map<String, Double> topExpensesMap = categoryIncome.stream()
                    .collect(Collectors.toMap(
                            TopCategoryIncome::getCategory,
                            TopCategoryIncome::getAmount
                    ));

            BufferedImage Chart = createPieChart(topExpensesMap);
            ByteArrayOutputStream chartStream = new ByteArrayOutputStream();
            ImageIO.write(Chart, "png", chartStream);
            ImageData chartData = ImageDataFactory.create(chartStream.toByteArray());
            document.add(new Image(chartData));
        }

        document.close();


    }


    //bieu do cot
    public static BufferedImage createBarChart(double income, double expense) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(income, "Amount", "Income");
        dataset.addValue(expense, "Amount", "Expense");

        JFreeChart chart = ChartFactory.createBarChart(
                "Income vs Expense",      // Chart title
                "",                       // X-axis label
                "Value ($)",              // Y-axis label
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        // === Tùy chỉnh biểu đồ ===
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        // Tăng độ rộng cột
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.3); // bạn có thể tăng lên 0.4 nếu muốn cột to hơn
        renderer.setSeriesPaint(0, new Color(91, 155, 213)); // màu cột

        // Nghiêng nhãn dưới trục X (tùy thích)
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        // Font cho tiêu đề
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));

        // Xuất thành ảnh
        return chart.createBufferedImage(500, 300); // tăng kích thước cho rõ
    }

    public static BufferedImage createBarChartFor12Months(List<ChartItemDTO> monthlyDataList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (ChartItemDTO data : monthlyDataList) {
            dataset.addValue(data.getIncome(), "Income", data.getMonth());
            dataset.addValue(data.getExpense(), "Expense", data.getMonth());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Income vs Expense",   // chart title
                "Month",                       // X-axis label
                "Amount ($)",                  // Y-axis label
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false); // show legend, tooltips, urls

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        // Customize bar colors
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(91, 155, 213)); // Income
        renderer.setSeriesPaint(1, new Color(237, 125, 49)); // Expense
        renderer.setMaximumBarWidth(0.1);

        // Rotate category labels for better readability
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        // Set title font
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));

        return chart.createBufferedImage(800, 400);
    }






//bieu do tron
    public static BufferedImage createPieChart(Map<String, Double> topExpenses) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        topExpenses.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "", dataset, true, true, false);
// tính %
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", new DecimalFormat("###,##0"), new DecimalFormat("0.00%")));

        return chart.createBufferedImage(500, 300);
    }

    // biểu đồ ngang
    public static BufferedImage createHorizontalBarChart(List<TopCategoryExpenses> topExpenses) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (TopCategoryExpenses expense : topExpenses) {
            dataset.addValue(expense.getAmount(), "Amount", expense.getCategory());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Top Expenses",         // Chart title
                "Category",             // Domain axis (X axis)
                "Amount",               // Range axis (Y axis)
                dataset,
                PlotOrientation.HORIZONTAL, // <- Quan trọng: chiều ngang
                false, true, false);

        return barChart.createBufferedImage(600, 400);
    }


    public static String formatUSD(double amount) {
        if (amount % 1 == 0) {
            return String.format("$%,.0f", amount);  // Không có phần thập phân nếu là số nguyên
        } else {
            return String.format("$%,.2f", amount);  // Có phần thập phân nếu là số lẻ
        }
    }



//    // test
//    public static void main(String[] args) {
//        try {
//
//            double income = 3250;
//            double expense = 2150;
//
//            Map<String, Double> topExpenses = new HashMap<>();
//            topExpenses.put("Housing", 800.0);
//            topExpenses.put("Food", 450.0);
//            topExpenses.put("Shopping", 300.0);
//
//            List<TopCategoryExpensesDTO> topCategoryExpenses = List.of(
//                    new TopCategoryExpensesDTO("Housing", "🏠", 800.0),
//                    new TopCategoryExpensesDTO("Food", "🍔", 450.0),
//                    new TopCategoryExpensesDTO("Shopping", "🛍️", 300.0),
//                    new TopCategoryExpensesDTO("Transport", "🚗", 200.0)
//            );
//            ChartItemDTO chartItemDTO = new ChartItemDTO();
//            chartItemDTO.setMonth("hoho");
//            chartItemDTO.setIncome(500.00);
//            chartItemDTO.setExpense(300.00);
//            // Tạo tên file
//            String fileName = "user" + 1 + "_summary_" + 3 + "_" + 2025 + ".pdf";
////            String baseFileName = "user" + userId + "_summary_" + month + "_" + year + ".pdf";
//            // Thư mục lưu file PDF
//            String folderPath = new File("src/main/resources/static/reports").getAbsolutePath();
//            File folder = new File(folderPath);
//            if (!folder.exists()) {
//                folder.mkdirs();
//            }
//
//            // Tạo đường dẫn đầy đủ để lưu file
////            String fullPath = folderPath + File.separator + fileName;
//            // tạm thời su dung fileName, fix xong sử dung baseFileName
//            String fullPath = getAvailableFilePath(folderPath, fileName);
//            generatePdfWithCharts(income, expense, topCategoryExpenses,chartItemDTO ,fullPath);
//            String fileNamepublic = new File(fullPath).getName();
//            String publicURL = "/reports/" + fileNamepublic;
//        System.out.println("📄 PDF created! You can download it via: http://localhost:8080" + publicURL);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
