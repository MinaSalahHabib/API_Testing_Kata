package com.booking.stepdefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import com.booking.assertions.BookingAssertions;


import com.booking.client.BookingApiClient;

public class GetMessages {
    private Response response;
    private final BookingApiClient api = new BookingApiClient();

    @When("I want to read the messages")
    public void iWantToReadTheMessages() {

        response = api.message();
    }

    @Then("I should receive all existing messages")
    public void iShouldReceiveAllExistingMessages() {
        BookingAssertions.assertAllMessages(response);
    }
}
