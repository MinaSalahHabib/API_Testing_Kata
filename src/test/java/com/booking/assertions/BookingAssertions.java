package com.booking.assertions;

import io.restassured.response.Response;

import static io.restassured.RestAssured.request;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Reusable assertions for live API responses.
 */
public final class BookingAssertions {

    private BookingAssertions() {
    }

    public static void assertCreatedBooking(
            Response response,
            String expectedFirstName,
            String expectedLastName) {

        assertThat("HTTP status", response.statusCode(), is(201));
        assertJson(response);

        assertThat(
                "bookingid should be a positive integer",
                response.jsonPath().getInt("bookingid"),
                greaterThan(0)
        );

        assertThat(
                "returned first name",
                response.jsonPath().getString("firstname"),
                is(expectedFirstName)
        );

        assertThat(
                "returned last name",
                response.jsonPath().getString("lastname"),
                is(expectedLastName)
        );

        assertThat(
                "room ID should be positive",
                response.jsonPath().getInt("roomid"),
                greaterThan(0)
        );

        assertThat(
                "depositpaid should be boolean",
                response.jsonPath().get("depositpaid"),
                instanceOf(Boolean.class)
        );

        assertThat(
                "check-in should be ISO date",
                response.jsonPath().getString("bookingdates.checkin"),
                matchesPattern("\\d{4}-\\d{2}-\\d{2}")
        );

        assertThat(
                "check-out should be ISO date",
                response.jsonPath().getString("bookingdates.checkout"),
                matchesPattern("\\d{4}-\\d{2}-\\d{2}")
        );
    }

    public static void assertValidationError(Response response) {
        assertThat(
                "invalid booking should be rejected",
                response.statusCode(),
                is(400)
        );

        assertJson(response);

        assertThat(
                "validation response should include errors",
                response.jsonPath().getList("errors"),
                not(empty())
        );
    }

    public static void assertJson(Response response) {
        assertThat(
                "Content-Type should be JSON",
                response.getContentType(),
                containsString("application/json")
        );
    }

    public static void assertHealthy(Response response) {
        assertThat("health status", response.statusCode(), is(200));
        assertThat("health state", response.jsonPath().getString("status"), is("UP"));
        assertJson(response);
    }
    public static void assertLogin(Response response) {
        assertThat("login status", response.statusCode(), is(200));
        assertJson(response);
        String token = response.jsonPath().getString("token");
        assertThat("login response must contain a non-empty token", token, not(isEmptyOrNullString()));
    }

    public static void assertRetrievedBooking(Response response,
                                              String expectedFirstName,
                                              String expectedLastName,
                                              int expectedRoomId) {
        assertThat("get status", response.statusCode(), is(200));
        assertJson(response);
        assertThat(response.jsonPath().getString("firstname"), is(expectedFirstName));
        assertThat(response.jsonPath().getString("lastname"), is(expectedLastName));
        assertThat(response.jsonPath().getInt("roomid"), is(expectedRoomId));
    }

    public static void assertUpdatedBooking(Response response,String expectedFirstName, String expectedLastName) {
        assertThat("update status", response.statusCode(), is(200));
        BookingAssertions.assertJson(response);
        assertThat(response.jsonPath().getString("booking.firstname"), is(expectedFirstName));
        assertThat(response.jsonPath().getString("booking.lastname"), is(expectedLastName));
    }

    public static void assertDeletedBooking(Response response) {
        assertThat("delete status documented by OpenAPI", response.statusCode(), is(201));
    }
    public static void assertDeletedBookingNoLongerExist(Response response) {
        assertThat("deleted booking should not return success", response.statusCode(), not(is(200)));
    }
    public static void assertAllMessages(Response response) {
    assertThat("messages status", response.statusCode(), is(200));

    assertThat(
        "At least one message should be returned",
        response.jsonPath().getList("messages"),
        not(empty())
    );

    assertThat(
        "Every message should have an ID",
        response.jsonPath().getList("messages.id"),
        everyItem(notNullValue())
    );

    assertThat(
        "Every message should have a non-empty name",
        response.jsonPath().getList("messages.name"),
        everyItem(not(isEmptyOrNullString()))
    );

    assertThat(
        "Every message should have a non-empty subject",
        response.jsonPath().getList("messages.subject"),
        everyItem(not(isEmptyOrNullString()))
    );

    assertThat(
        "Every message should have a read status",
        response.jsonPath().getList("messages.read"),
        everyItem(notNullValue())
    );
}
}