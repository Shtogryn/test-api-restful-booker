package com.shtohryn;

import io.restassured.RestAssured;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class TestApiRestfulBooker {

    private static String token;
    private static int bookingId;

    @BeforeAll
    public static void setup() {
        baseURI = "https://restful-booker.herokuapp.com";
        basePath = "/booking";
    }

    @Test
    public void testAuth() {
        String requestBody = "{\n" + "\"username\": \"admin\",\n" + "\"password\": \"password123\"\n" + "}";
        Response response = RestAssured.given()
                .baseUri("https://restful-booker.herokuapp.com")
                .basePath("/auth")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().response();
        token = response.jsonPath().getString("token");
    }

    @Test
    public void testCreateBooking() {
        String requestBody = "{\"firstname\":\"John\",\"lastname\":\"Doe\",\"totalprice\":100,\"depositpaid\":true,\"bookingdates\":{\"checkin\":\"2023-03-25\",\"checkout\":\"2023-03-28\"},\"additionalneeds\":\"Breakfast\"}";
        bookingId = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post()
                .then()
                .assertThat().statusCode(200)
                .extract().path("bookingid");
    }

    @Test
    public void testGetBookingDetails() {
        given()
                .header("Authorization", "Bearer " + token)
                .pathParam("bookingid", bookingId)
                .when()
                .get("/{bookingid}")
                .then()
                .assertThat().statusCode(200)
                .body("firstname", equalTo("John"))
                .body("lastname", equalTo("Doe"))
                .body("totalprice", equalTo(100));
    }
        @Test
        public void getUpdatedBooking(){
            Response response = RestAssured.given()
                    .baseUri("https://restful-booker.herokuapp.com")
                    .basePath("/booking/" + bookingId)
                    .when()
                    .get()
                    .then()
                    .statusCode(200)
                    .extract().response();
            int totalprice = response.jsonPath().getInt("totalprice");
            assertEquals(totalprice,100);
        }
        @Test
        public void getAllBookings(){
            Response response = RestAssured.given()
                    .baseUri("https://restful-booker.herokuapp.com")
                    .basePath("/booking")
                    .when()
                    .get()
                    .then()
                    .statusCode(200)
                    .extract().response();
            List<Integer> bookingIds = response.jsonPath().getList("bookingid");
            assertTrue(bookingIds.contains(bookingId));
        }

        @Test
        public void deleteBooking(){
            RestAssured.given()
                    .baseUri(basePath)
                    .basePath(basePath + bookingId)
                    .cookie("token", token)
                    .when()
                    .delete()
                    .then()
                    .statusCode(201);
        }
    }


