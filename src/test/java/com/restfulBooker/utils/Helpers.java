package com.restfulBooker.utils;

import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import static io.restassured.RestAssured.given;

public class Helpers
{
    public String getAuthToken(String baseUrl) throws JSONException
    {
        String tokenRequestBody = new JSONObject()
                .put("username", ConfigReader.get("username"))
                .put("password", ConfigReader.get("password"))
                .toString();

        Response response = given()
                .baseUri(baseUrl)
                .body(tokenRequestBody)
                .contentType("application/json")
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .response();
        String authToken = response.jsonPath().getString("token");
        if (authToken == null)
        {
            throw new IllegalStateException("No token in auth response: " + response.getBody().asString());
        }
        ExtentReportManager.info("Auth Token is : " +authToken);
        return authToken;
    }

    public Response createBooking(String baseUrl, String Request)
    {
        Response response = given()
                .baseUri(baseUrl)
                .body(Request)
                .contentType("application/json")
                .when()
                .post("/booking")
                .then()
                .extract()
                .response();
        return response;
    }

    public Response getByIdBooking(String baseUrl, String bookingID)
    {
        Response response = given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .when()
                .get("/booking/"+bookingID)
                .then()
                .extract()
                .response();
        return response;
    }

    public Response updateBooking(String baseUrl, String Request, String bookingID,String token)
    {
        Response response = given()
                .baseUri(baseUrl)
                .body(Request)
                .contentType("application/json")
                .cookie("token", token)
                .when()
                .put("/booking/"+bookingID)
                .then()
                .extract()
                .response();
        return response;
    }

    public Response partialUpdateBooking(String baseUrl, String Request, String bookingID,String token)
    {
        Response response = given()
                .baseUri(baseUrl)
                .body(Request)
                .contentType("application/json")
                .cookie("token", token)
                .when()
                .patch("/booking/"+bookingID)
                .then()
                .extract()
                .response();
        return response;
    }

    public Response deleteBooking(String baseUrl,String bookingID,String token)
    {
        Response response = given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .cookie("token", token)
                .when()
                .delete("/booking/"+bookingID)
                .then()
                .extract()
                .response();
        return response;
    }

}
