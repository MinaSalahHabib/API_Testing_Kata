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
    

    @When("I check the API health")
    public void iCheckApiHealth() {  
        response = api.health(); 
    }
    @Then("the health endpoint should report UP")
    public void theHealthEndpointShouldReportUp() {
        BookingAssertions.assertHealthy(response);
        
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
        response = api.login();
        BookingAssertions.assertLogin(response);
        token = response.jsonPath().getString("token");
        
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
        BookingAssertions.assertRetrievedBooking(response, request.getFirstname(), request.getLastname(), request.getRoomid());
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
        BookingAssertions.assertUpdatedBooking(response, "Updated", "Guest");
    }

    @Then("the partial update should change only the first name")
    public void thePartialUpdateShouldChangeOnlyFirstName() {
        BookingAssertions.assertUpdatedBooking(response, "Patched", "Guest");
    }

     @When("I delete the created booking")
    public void iDeleteCreatedBooking() {
        response = api.deleteBooking(createdBookingId, token);
        
    }

    @When("I try to retrieve the deleted booking")
    public void iRetrieveDeletedBooking() { response = api.getBooking(createdBookingId, token); }

    @Then("the booking should be deleted successfully")
    public void theBookingShouldBeDeletedSuccessfully() {

        BookingAssertions.assertDeletedBooking(response);
        
    }

    @Then("the deleted booking should no longer be retrievable")
    public void theDeletedBookingShouldNoLongerBeRetrievable() {
        BookingAssertions.assertDeletedBookingNoLongerExist(response);
    }
}