Feature: Booking API contract and lifecycle
  As a client of the booking service
  I want booking operations to validate input and return contract-compliant responses
  So that booking data can be used safely by consumers


  Scenario: Health endpoint reports that the API is available
    When I check the API health
    Then the health endpoint should report UP

  Scenario: Create a valid booking
    Given a valid booking request
    When I submit the booking request
    Then the booking should be created with the submitted details

  # Negative test case for first name < minimum length
  Scenario: Reject a first name below the documented minimum length
    Given a booking request with first name "Al"
    When I submit the booking request
    Then the API should reject the request with a validation error

  # Negative test case for first name > maximum length
  Scenario: Reject a first name Exceeds the documented maximum length
    Given a booking request with first name "A123456789123456789"
    When I submit the booking request
    Then the API should reject the request with a validation error