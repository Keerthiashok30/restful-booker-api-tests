package com.restfulBooker.scenarios;

import com.restfulBooker.utils.*;
import org.json.JSONException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class createBooking extends BaseClass
    {
        static final String createSheetName = "CreateBooking";
        static final String createResponse = RESOURCES_ROOT_DIR+"/Data/Response/createBookingResponse.json";

        @DataProvider(name = "bookingData")
        public Object[][] bookingData() throws Exception
        {
            return testData(excelPath,createSheetName);
        }

        @Test(dataProvider = "bookingData")
        public void testCreateBooking(Map<String, String> inputData) throws IOException, JSONException
        {
            logger.info("Test case : " + inputData.get("TestCase"));
            ExtentReportManager.info("Test case : " + inputData.get("TestCase"));

            //create booking
            String createbookingID=generalCreateBooking(inputData,createResponse);

            //get and verify the booking
            getAndVerifyBooking(inputData, responseJson, createbookingID);

        }

    }
