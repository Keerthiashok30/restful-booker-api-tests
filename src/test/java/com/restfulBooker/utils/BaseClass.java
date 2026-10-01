package com.restfulBooker.utils;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.Assert;
import org.testng.annotations.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Listeners(ExtentReportManager.class)
public class BaseClass
 {
    public static final String RESOURCES_ROOT_DIR = System.getProperty("user.dir") + "/src/test/resources";
    protected Logger logger = LogManager.getLogger(getClass());
    public Helpers helperClass = new Helpers();
    public ResponseValidator responseValidator = new ResponseValidator();
    public static final String baseURL=ConfigReader.get("baseUrl");
    public static final String excelPath    = RESOURCES_ROOT_DIR+"/Data/Excel/Testdata.xlsx";
    public static final String responseJson = RESOURCES_ROOT_DIR+"/Data/Response/bookingResponse.json";
    public static final String requestJson  = RESOURCES_ROOT_DIR+"/Data/Request/createBookingRequest.json";

    @BeforeSuite(alwaysRun = true)
    public void suiteStart()
    {
    logger.info("*** Suite started ***");
    }

    @BeforeTest(alwaysRun = true)
    public void testStart()
    {
    logger.info("*** Test started ***");
    }

    @AfterTest(alwaysRun = true)
    public void testFinish()
    {
    logger.info("*** Test finished ***");
    }

    @AfterSuite(alwaysRun = true)
    public void suiteFinish()
    {
    ExtentReportManager.flush();
    logger.info("*** Suite finished, report flushed ***");
    }

    public Object[][] testData(String EXCEL_PATH,String SHEET_NAME)
     {
        try
        {
        List<Map<String, String>> inputData = ExcelUtils.getData(EXCEL_PATH, SHEET_NAME);
        logger.info("Rows found: " + inputData.size());

        Object[][] data = new Object[inputData.size()][1];
        for (int i = 0; i < inputData.size(); i++)
        {
        data[i][0] = inputData.get(i);
        }
        return data;
        } catch (Exception e)
        {
        e.printStackTrace();
        throw new RuntimeException(e);
        }
    }

    public String generalCreateBooking(Map<String, String> inputData, String createResponseJson) throws IOException, JSONException
    {
        Map<String, Object> values = new HashMap<>(inputData);
        String createRequestBody = PayloadBuilder.loadAndFill(requestJson, values);
        Response createBookingResponse = helperClass.createBooking(baseURL, createRequestBody);
        logger.info("Response for create Booking is : " + createBookingResponse.getBody().asString());
        ExtentReportManager.infoJson("Response for create Booking is : ", createBookingResponse.getBody().asString());
        String bookingID = createBookingResponse.jsonPath().getString("bookingid");
        logger.info("BookingID for create booking is : " + bookingID);
        logger.info("Status code for create booking is : " + createBookingResponse.getStatusCode());
        Assert.assertEquals(createBookingResponse.getStatusCode(), Integer.parseInt(inputData.get("ExpectedStatusCode")), "Status code for create booking is : " + createBookingResponse.getStatusCode());
        Map<String, Pattern> patterns = Map.of("bookingid", Pattern.compile("\\d+"));
        values.put("bookingid", bookingID);
        String expectedCreateResponse  = PayloadBuilder.loadAndFill(createResponseJson, values);
        responseValidator.assertEqualsWithShapeChecks("Response validation for create booking, test case : " + inputData.get("TestCase"),
                                                            createBookingResponse.getBody().asString(),
                                                            expectedCreateResponse,
                                                            patterns,
                                                            JSONCompareMode.NON_EXTENSIBLE);
        return bookingID;
    }

    public void getAndVerifyBooking(Map<String, String> inputData, String expectedGetResponse, String bookingID) throws IOException, JSONException
    {
        Response getResponse = helperClass.getByIdBooking(baseURL, bookingID);
        logger.info("Response for get by ID booking is : " + getResponse.getBody().asString());
        ExtentReportManager.infoJson("Response for get by ID booking is : ", getResponse.getBody().asString());
        logger.info("Status code for get by ID booking is : " + getResponse.getStatusCode());
        Assert.assertEquals(getResponse.getStatusCode(), Integer.parseInt(inputData.get("ExpectedStatusCode")), "Status code for get by ID booking is : " + getResponse.getStatusCode());
        String expectedResponse = PayloadBuilder.loadAndFill(expectedGetResponse, new HashMap<>(inputData));
        responseValidator.assertEqualsIgnoringNodes("Response validation for get by ID booking, test case : " + inputData.get("TestCase"),
                                                        getResponse.getBody().asString(),
                                                        expectedResponse,
                                                        List.of(),
                                                        JSONCompareMode.NON_EXTENSIBLE);
    }
}
