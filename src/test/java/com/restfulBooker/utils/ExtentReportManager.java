package com.restfulBooker.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ExtentReportManager implements ITestListener
{
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    public static synchronized ExtentReports getInstance()
    {
        if (extent == null)
        {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String reportPath = System.getProperty("user.dir") + "/build/reports/extent/ExtentReport_" + timestamp + ".html";
            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setReportName("Restful Booker API Tests");
            spark.config().setDocumentTitle("API Test Report");
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Environment", System.getProperty("env", "qa"));
        }
        return extent;
    }

    public static ExtentTest getTest()
    {
        return testThread.get();
    }

    public static void info(String message)
    {
        if (getTest() != null) {
            getTest().info(message);
        }
    }

    public static void infoJson(String title, String json)
    {
        if (getTest() != null) {
            getTest().info(title);
            getTest().info(MarkupHelper.createCodeBlock(json, CodeLanguage.JSON));
        }
    }

    public static void flush()
    {
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result)
    {
        String name = result.getMethod().getMethodName();
        Object[] params = result.getParameters();
        if (params.length > 0 && params[0] instanceof Map)
        {
            Object testCase = ((Map<?, ?>) params[0]).get("TestCase");
            if (testCase != null) {
                name = name + " - " + testCase;
            }
        }
        testThread.set(getInstance().createTest(name));
    }

    @Override
    public void onTestSuccess(ITestResult result)
    {
        getTest().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result)
    {
        getTest().fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result)
    {
        getTest().skip("Test skipped");
    }
}