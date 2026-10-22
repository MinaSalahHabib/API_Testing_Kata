package com.booking.stepdefinitions;



import com.booking.builder.BookingRequestBuilder;
import com.booking.client.BookingApiClient;
import com.booking.models.BookingRequest;
import com.booking.assertions.BookingAssertions;

import io.cucumber.java.en.When;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import static io.restassured.RestAssured.request;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/** Step definitions orchestrate scenarios; API mechanics remain in BookingApiClient. */
public class BookingSteps {
    private final BookingApiClient api = new BookingApiClient();

    private Response response;
    private BookingRequest request;

    @When("I check the API health")
    public void iCheckApiHealth() {  
        response = api.health(); 
    }
    @Then("the health endpoint should report UP")
    public void theHealthEndpointShouldReportUp() {
        assertThat("health status", response.statusCode(), is(200));
        BookingAssertions.assertJson(response);
        assertThat(response.jsonPath().getString("status"), is("UP"));
    }

    @Given("a valid booking request")
    public void aValidBookingRequest() { request = BookingRequestBuilder.aValidBooking().build(); }

    @When("I submit the booking request")
    public void iSubmitTheBookingRequest() { 
        response = api.createBooking(request);
    }

    @Then("the booking should be created with the submitted details")
    public void theBookingShouldBeCreatedWithSubmittedDetails() {
        BookingAssertions.assertCreatedBooking(response, request.getFirstname(), request.getLastname());
    }


}