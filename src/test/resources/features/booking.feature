Feature: Booking API contract and lifecycle
  As a client of the booking service
  I want booking operations to validate input and return contract-compliant responses
  So that booking data can be used safely by consumers

  @smoke @health
  Scenario: Health endpoint reports that the API is available
    When I check the API health
    Then the health endpoint should report UP