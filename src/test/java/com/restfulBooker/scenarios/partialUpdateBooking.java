package com.restfulBooker.scenarios;

import com.restfulBooker.utils.BaseClass;
import com.restfulBooker.utils.ExtentReportManager;
import com.restfulBooker.utils.PayloadBuilder;
import io.restassured.response.Response;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class partialUpdateBooking extends BaseClass
{
    static final String partialUpdateSheetName    = "PartialUpdateBooking";
    static final String partialUpdateRequest=RESOURCES_ROOT_DIR+"/Data/Request/partialUpdateBookingRequest.json";
    static final String createResponse = RESOURCES_ROOT_DIR+"/Data/Response/createBookingResponse.json";
    static final String partialUpdateResponse=RESOURCES_ROOT_DIR+"/Data/Response/partialUpdateBookingResponse.json";

    @DataProvider(name = "partialData")
    public Object[][] partialData() throws Exception
    {
        return testData(excelPath,partialUpdateSheetName);
    }

    @Test(dataProvider = "partialData")
    public void testPartialUpdateBooking(Map<String, String> inputData) throws IOException, JSONException
    {
        Map<String, Object> values = new HashMap<>(inputData);
        String partialUpdateRequestBody = PayloadBuilder.loadAndFill(partialUpdateRequest, values);

        logger.info("Test case : " + inputData.get("TestCase"));
        ExtentReportManager.info("Test case : " + inputData.get("TestCase"));

        //create the booking
        String createbookingID=generalCreateBooking(inputData,createResponse);

        //auth
        String authToken=helperClass.getAuthToken(baseURL);

        //update partial  booking
        Response partialUpdateBookingResponse = helperClass.partialUpdateBooking(baseURL, partialUpdateRequestBody, createbookingID, authToken);
        logger.info("Response for partial update Booking is : " + partialUpdateBookingResponse.getBody().asString());
        ExtentReportManager.infoJson("Response for partial update Booking is : ", partialUpdateBookingResponse.getBody().asString());
        Assert.assertEquals(partialUpdateBookingResponse.getStatusCode(), Integer.parseInt(inputData.get("ExpectedStatusCode")), "Status code for partial update booking is : " + partialUpdateBookingResponse.getStatusCode());
        String expectedPartialUpdateResponse  = PayloadBuilder.loadAndFill(partialUpdateResponse, values);
        responseValidator.assertEqualsIgnoringNodes("Response validation for partial update booking, test case : " + inputData.get("TestCase"),
                                                        partialUpdateBookingResponse.getBody().asString(),
                                                        expectedPartialUpdateResponse,
                                                        List.of(),
                                                        JSONCompareMode.NON_EXTENSIBLE);

        //get and verify the booking
        getAndVerifyBooking(inputData, partialUpdateResponse, createbookingID);

    }

}
