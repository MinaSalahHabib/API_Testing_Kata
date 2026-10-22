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

  # Negative test case for a phone number < minimum length
  Scenario: Reject a phone number shorter than the documented minimum
    Given a booking request with phone "1234567890"
    When I submit the booking request
    Then the API should reject the request with a validation error
  
  # Negative test case for a phone number > maximum length
  Scenario: Reject a phone number taller than the documented maximum
    Given a booking request with phone "1234567890123456789012"
    When I submit the booking request
    Then the API should reject the request with a validation error

  # Negative test case for invalid email format
  Scenario: Reject a malformed email address
    Given a booking request with email "not-an-email"
    When I submit the booking request
    Then the API should reject the request with a validation error

  # Negative test case for invalid booking dates 
  Scenario: Reject checkout before check-in
    Given a booking request with checkout before check-in
    When I submit the booking request
    Then the API should reject the request with a validation error