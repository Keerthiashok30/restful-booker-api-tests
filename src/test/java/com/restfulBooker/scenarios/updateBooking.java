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

public class updateBooking extends BaseClass
{
    static final String updateSheetName    = "UpdateBooking";
    static final String updateRequest=RESOURCES_ROOT_DIR+"/Data/Request/updateBookingRequest.json";
    static final String createResponse = RESOURCES_ROOT_DIR+"/Data/Response/createBookingResponse.json";
    static final String updateResponse=RESOURCES_ROOT_DIR+"/Data/Response/updateBookingResponse.json";

    @DataProvider(name = "updateData")
    public Object[][] updateData() throws Exception
    {
        return testData(excelPath,updateSheetName);
    }

    @Test(dataProvider = "updateData")
    public void testUpdateBooking(Map<String, String> inputData) throws IOException, JSONException
    {
        Map<String, Object> values = new HashMap<>(inputData);
        String updateRequestBody = PayloadBuilder.loadAndFill(updateRequest, values);

        logger.info("Test case : " + inputData.get("TestCase"));
        ExtentReportManager.info("Test case : " + inputData.get("TestCase"));

        //create the booking
        String createbookingID=generalCreateBooking(inputData,createResponse);

        //auth
        String authToken=helperClass.getAuthToken(baseURL);

        //update the booking
        Response updateBookingResponse = helperClass.updateBooking(baseURL, updateRequestBody, createbookingID, authToken);
        logger.info("Response for update booking is : " + updateBookingResponse.getBody().asString());
        ExtentReportManager.infoJson("Response for update booking is : ", updateBookingResponse.getBody().asString());
        Assert.assertEquals(updateBookingResponse.getStatusCode(), Integer.parseInt(inputData.get("ExpectedStatusCode")), "Status code update booking is : " + updateBookingResponse.getStatusCode());
        String expectedUpdateResponse  = PayloadBuilder.loadAndFill(updateResponse, values);
        responseValidator.assertEqualsIgnoringNodes("Response validation for update booking, test case : " + inputData.get("TestCase"),
                                                        updateBookingResponse.getBody().asString(),
                                                        expectedUpdateResponse,
                                                        List.of(),
                                                        JSONCompareMode.NON_EXTENSIBLE);

        //get and verify the booking
        getAndVerifyBooking(inputData, updateResponse, createbookingID);
    }
}
