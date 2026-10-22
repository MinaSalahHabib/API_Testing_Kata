package com.kata.api.assertions;

import io.restassured.response.Response;

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
}