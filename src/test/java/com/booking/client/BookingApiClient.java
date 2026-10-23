package com.booking.client;

import com.booking.config.ApiConfig;
import com.booking.models.BookingRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Map;
import static io.restassured.RestAssured.given;

/** Facade/API Client: keeps HTTP details and endpoint paths out of Cucumber step definitions. */
public class BookingApiClient {
    private static final String BOOKING_PATH = "/booking";
    private static final String HEALTH_PATH = "/actuator/health";
    private static final String AUTHORIZATIO_PATH = "/auth/login";
    private static final String MESSAGE_PATH = "/message";

    public Response message() {
        return given().log().all().baseUri(ApiConfig.baseUrl()).when().get(MESSAGE_PATH);
    }

    public Response health() {
        return given().baseUri(ApiConfig.baseUrl()).accept(ContentType.JSON).when().get(BOOKING_PATH + HEALTH_PATH);
    }
    public Response createBooking(BookingRequest request) {
        return given().baseUri(ApiConfig.baseUrl()).contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request).when().post(BOOKING_PATH);
    }
    public Response createBookingRaw(String json) {
        return given().baseUri(ApiConfig.baseUrl()).contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(json).when().post(BOOKING_PATH);
    }
    public Response login() {
        return given().baseUri(ApiConfig.baseUrl()).contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(Map.of("username", ApiConfig.username(), "password", ApiConfig.password())).when().post(AUTHORIZATIO_PATH);
    }
    public Response getBooking(int bookingId, String token) {
        var request = given().baseUri(ApiConfig.baseUrl()).accept(ContentType.JSON);
        if (token != null && !token.isBlank()) request.header("Cookie", "token=" + token);
        return request.when().get(BOOKING_PATH + "/" + bookingId);
    }
    
    public Response updateBooking(int bookingId, String token, BookingRequest body) {
        var request = given().baseUri(ApiConfig.baseUrl()).contentType(ContentType.JSON).accept(ContentType.JSON);
        if (token != null && !token.isBlank()) request.header("Cookie", "token=" + token);
        return request.body(body).when().put(BOOKING_PATH + "/" + bookingId);
    }

    public Response patchBooking(int bookingId, String token, Map<String, Object> body) {
        var request = given().baseUri(ApiConfig.baseUrl()).contentType(ContentType.JSON).accept(ContentType.JSON);
        if (token != null && !token.isBlank()) request.header("Cookie", "token=" + token);
        return request.body(body).when().patch(BOOKING_PATH + "/" + bookingId);
    }

    public Response deleteBooking(int bookingId, String token) {
        var request = given().baseUri(ApiConfig.baseUrl());
        if (token != null && !token.isBlank()) request.header("Cookie", "token=" + token); 
        return request.when().delete(BOOKING_PATH + "/" + bookingId);
    }
}
