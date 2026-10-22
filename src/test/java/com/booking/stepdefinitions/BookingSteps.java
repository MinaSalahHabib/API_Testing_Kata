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

import java.util.Map;

/** Step definitions orchestrate scenarios; API mechanics remain in BookingApiClient. */
public class BookingSteps {
    private final BookingApiClient api = new BookingApiClient();

    private Response response;
    private BookingRequest request;
    private String token;
    private Integer createdBookingId;
    private boolean bookingDeleted;

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


    @When("I replace the created booking with updated details")
    public void iReplaceCreatedBooking() {
        request = BookingRequestBuilder.aValidBooking().withFirstName("Updated").withLastName("Guest").build();
        response = api.updateBooking(createdBookingId, token, request);
    }

    @When("I partially update the created booking's first name")
    public void iPartiallyUpdateCreatedBooking() {
        response = api.patchBooking(createdBookingId, token, Map.of("firstname", "Patched1"));
    }

    @Then("the update should return the updated booking")
    public void theUpdateShouldReturnUpdatedBooking() {
        assertThat("update status", response.statusCode(), is(200));
        BookingAssertions.assertJson(response);
        assertThat(response.jsonPath().getString("booking.firstname"), is("Updated"));
        assertThat(response.jsonPath().getString("booking.lastname"), is("Guest"));
    }

    @Then("the partial update should change only the first name")
    public void thePartialUpdateShouldChangeOnlyFirstName() {
        assertThat("patch status", response.statusCode(), is(200));
        BookingAssertions.assertJson(response);
        assertThat(response.jsonPath().getString("firstname"), is("Patched"));
        assertThat(response.jsonPath().getString("lastname"), is("Guest"));
    }

     @When("I delete the created booking")
    public void iDeleteCreatedBooking() {
        response = api.deleteBooking(createdBookingId, token);
        if (response.statusCode() == 201) bookingDeleted = true;
    }

    @When("I try to retrieve the deleted booking")
    public void iRetrieveDeletedBooking() { response = api.getBooking(createdBookingId, token); }

    @Then("the booking should be deleted successfully")
    public void theBookingShouldBeDeletedSuccessfully() {
        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.asPrettyString());   
        assertThat("delete status documented by OpenAPI", response.statusCode(), is(201));
        bookingDeleted = true;
    }

    @Then("the deleted booking should no longer be retrievable")
    public void theDeletedBookingShouldNoLongerBeRetrievable() {
        assertThat("deleted booking should not return success", response.statusCode(), not(is(200)));
    }
}