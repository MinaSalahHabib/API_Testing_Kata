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

  # Negative test case for last name < minimum length
  Scenario: Reject a last name below the documented minimum length
    Given a booking request with last name "Al"
    When I submit the booking request
    Then the API should reject the request with a validation error

  # Negative test case for last name > maximum length
  Scenario: Reject a last name Exceeds the documented maximum length
    Given a booking request with last name "A123456789123456789"
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
  Scenario: Reject Booking with a malformed email address
    Given a booking request with email "not-an-email"
    When I submit the booking request
    Then the API should reject the request with a validation error

  # Negative test case for invalid booking dates 
  Scenario: Reject Booking checkout before check-in
    Given a booking request with checkout before check-in
    When I submit the booking request
    Then the API should reject the request with a validation error

  #Negative test case for Booking without mandatory field (email)
  Scenario: Reject Booking without email address
    Given a booking request with email ""
    When I submit the booking request
    Then the API should reject the request with a validation error
 
  #Negative test case for Booking without mandatory field (first name) 
  Scenario: Reject Booking without first name 
    Given a booking request with first name ""
    When I submit the booking request
    Then the API should reject the request with a validation error

  #Negative test case for Booking without mandatory field (last name) 
  Scenario: Reject Booking without last name 
    Given a booking request with last name ""
    When I submit the booking request
    Then the API should reject the request with a validation error

  #Negative test case for Booking without mandatory field (phone number)
  Scenario: Reject a booking without mandatory field (phone number)
    Given a booking request with phone ""
    When I submit the booking request
    Then the API should reject the request with a validation error

  #Negative test case for Booking without mandatory field (checkin)
  Scenario: Reject a booking without mandatory field (checkin)
    Given a booking request without checkin
    When I submit the booking request
    Then the API should reject the request with a validation error

  #Negative test case for Booking without mandatory field (checkout)
  Scenario: Reject a booking without mandatory field (checkout)
    Given a booking request without checkout
    When I submit the booking request
    Then the API should reject the request with a validation error

  Scenario: Retrive existing a booking with authentication
    When I authenticate with the configured API credentials
    And I create a booking using the authenticated session
    And I retrieve the created booking using the token
    Then the retrieved booking should match the submitted details

 Scenario: Update existing a booking with authentication
    When I authenticate with the configured API credentials
    And I create a booking using the authenticated session
    And I replace the created booking with updated details
    Then the update should return the updated booking
    When I partially update the created booking's first name
    Then the partial update should change only the first name

Scenario: Delete existing a booking with authentication
    When I authenticate with the configured API credentials
    And I create a booking using the authenticated session
    And I delete the created booking
    Then the booking should be deleted successfully
    When I try to retrieve the deleted booking
    Then the deleted booking should no longer be retrievable

Scenario: Created booking response should conform to the OpenAPI schema
  Given a valid booking request
  When I submit the booking request
  Then the created booking response should match the contract schema

Scenario: Retrieve an existing booking without authentication
  When I create a booking using the authenticated session
  And I retrieve the created booking without a token
  Then the request should be rejected due to missing authentication

Scenario: Update an existing booking without authentication
    When I authenticate with the configured API credentials
    And I create a booking using the authenticated session
    And I replace the created booking without a token
    Then the request should be rejected due to missing authentication

Scenario: Partially update a booking without authentication
    When I authenticate with the configured API credentials
    And I create a booking using the authenticated session
    And I partially update the created booking's first name without a token
    Then the request should be rejected due to missing authentication

Scenario: Delete an existing booking without authentication
    When I authenticate with the configured API credentials
    And I create a booking using the authenticated session
    And I delete the created booking without a token
    Then the request should be rejected due to missing authentication