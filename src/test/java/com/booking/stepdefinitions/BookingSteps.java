package com.booking.stepdefinitions;



import com.booking.builder.BookingRequestBuilder;
import com.booking.client.BookingApiClient;
import com.booking.config.ApiConfig;
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
    private String token;
    private Integer createdBookingId;

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

    @Given("a booking request with first name {string}")
    public void aBookingRequestWithFirstName(String firstName) { request = BookingRequestBuilder.aValidBooking().withFirstName(firstName).build(); }

    @Given("a booking request with last name {string}")
    public void aBookingRequestWithLastName(String lastName) { request = BookingRequestBuilder.aValidBooking().withLastName(lastName).build(); }

    @Then("the API should reject the request with a validation error")
    public void theApiShouldRejectTheRequestWithValidationError() { BookingAssertions.assertValidationError(response); }

    @Given("a booking request with phone {string}")
    public void aBookingRequestWithPhone(String phone) { request = BookingRequestBuilder.aValidBooking().withPhone(phone).build(); }

    @Given("a booking request with email {string}")
    public void aBookingRequestWithEmail(String email) { request = BookingRequestBuilder.aValidBooking().withEmail(email).build(); }

    @Given("a booking request with checkout before check-in")
    public void aBookingRequestWithCheckoutBeforeCheckin() {
        request = BookingRequestBuilder.aValidBooking().withDates("2035-06-10", "2035-06-08").build();
    }

    @Given("a booking request without checkout")
    public void aBookingRequestWithoutCheckout() {
        request = BookingRequestBuilder.aValidBooking().withDates("2035-06-08", "").build();
    }
    @Given("a booking request without checkin")
    public void aBookingRequestWithoutCheckin() {
        request = BookingRequestBuilder.aValidBooking().withDates("", "2035-06-08").build();
    }

    @When("I authenticate with the configured API credentials")
    public void iAuthenticateWithConfiguredCredentials() {
        response = api.login(ApiConfig.username(), ApiConfig.password());
        assertThat("login status", response.statusCode(), is(200));
        BookingAssertions.assertJson(response);
        token = response.jsonPath().getString("token");
        assertThat("login response must contain a non-empty token", token, not(isEmptyOrNullString()));
    }

    @When("I create a booking using the authenticated session")
    public void iCreateBookingUsingAuthenticatedSession() {
        request = BookingRequestBuilder.aValidBooking().build();
        response = api.createBooking(request);
        BookingAssertions.assertCreatedBooking(response, request.getFirstname(), request.getLastname());
        createdBookingId = response.jsonPath().getInt("bookingid");
    }

    @When("I retrieve the created booking using the token")
    public void iRetrieveCreatedBookingUsingToken() { response = api.getBooking(createdBookingId, token); }

    @Then("the retrieved booking should match the submitted details")
    public void theRetrievedBookingShouldMatchSubmittedDetails() {
        assertThat("get status", response.statusCode(), is(200));
        BookingAssertions.assertJson(response);
        assertThat(response.jsonPath().getString("firstname"), is(request.getFirstname()));
        assertThat(response.jsonPath().getString("lastname"), is(request.getLastname()));
        assertThat(response.jsonPath().getInt("roomid"), is(request.getRoomid()));
    }
}