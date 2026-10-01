package com.restfulBooker.scenarios;

import com.restfulBooker.utils.*;
import io.restassured.response.Response;
import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class deleteBooking extends BaseClass
{
    static final String deleteSheetName    = "DeleteBooking";
    static final String createResponse = RESOURCES_ROOT_DIR+"/Data/Response/createBookingResponse.json";

    @DataProvider(name = "deleteData")
    public Object[][] deleteData() throws Exception
    {
        return testData(excelPath,deleteSheetName);
    }

    @Test(dataProvider = "deleteData")
    public void testDeleteBooking(Map<String, String> inputData) throws IOException,JSONException
    {
        logger.info("Test case : " + inputData.get("TestCase"));
        ExtentReportManager.info("Test case : " + inputData.get("TestCase"));

        //create the booking
        String createbookingID=generalCreateBooking(inputData,createResponse);

        //auth
        String authToken=helperClass.getAuthToken(baseURL);

        //delete the booking
        Response deleteBookingResponse = helperClass.deleteBooking(baseURL,createbookingID, authToken);
        logger.info("Response for deleted booking is : " + deleteBookingResponse.getBody().asString());
        ExtentReportManager.info("Response for deleted booking is : "+deleteBookingResponse.getBody().asString());
        Assert.assertEquals(deleteBookingResponse.getStatusCode(), Integer.parseInt(inputData.get("DeleteStatusCode")), "Status code for delete booking is : " + deleteBookingResponse.getStatusCode());
        logger.info("Status code for delete booking is : " + deleteBookingResponse.getStatusCode());
        ExtentReportManager.info("Status code for delete booking is : " + deleteBookingResponse.getStatusCode());

        //get and verify the booking
        Response getBookingResponse = helperClass.getByIdBooking(baseURL, createbookingID);
        logger.info("Response for get by ID booking is : " + getBookingResponse.getBody().asString());
        ExtentReportManager.info("Response for get by ID booking is : "+getBookingResponse.getBody().asString());
        Assert.assertEquals(getBookingResponse.getStatusCode(), Integer.parseInt(inputData.get("GetStatusCode")), "Status code for get by ID booking is : " + getBookingResponse.getStatusCode());
        logger.info("Status code for get by ID booking is : " + getBookingResponse.getStatusCode());
        ExtentReportManager.info("Status code for get by ID booking is : " + getBookingResponse.getStatusCode());
    }

}
