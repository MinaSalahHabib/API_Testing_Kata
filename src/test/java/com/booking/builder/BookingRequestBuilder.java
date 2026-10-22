package com.booking.builder;

import com.booking.models.BookingDates;
import com.booking.models.BookingRequest;
import java.time.LocalDate;
import java.util.UUID;

/** Builder pattern: supplies valid defaults and allows a test to mutate one field at a time. */
public final class BookingRequestBuilder {
    private int roomId = 204;
    private String firstName = "Mina";
    private String lastName = "Badros";
    private boolean depositPaid = true;
    private String checkIn = LocalDate.now().plusDays(30).toString();
    private String checkOut = LocalDate.now().plusDays(32).toString();
    private String email = "api.kata." + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    // The supplied OpenAPI contract documents 11-21 characters for phone validation.
    private String phone = "01234567890";

    public static BookingRequestBuilder aValidBooking() { return new BookingRequestBuilder(); }
    public BookingRequestBuilder withRoomId(int value) { roomId = value; return this; }
    public BookingRequestBuilder withFirstName(String value) { firstName = value; return this; }
    public BookingRequestBuilder withLastName(String value) { lastName = value; return this; }
    public BookingRequestBuilder withDepositPaid(boolean value) { depositPaid = value; return this; }
    public BookingRequestBuilder withDates(String start, String end) { checkIn = start; checkOut = end; return this; }
    public BookingRequestBuilder withEmail(String value) { email = value; return this; }
    public BookingRequestBuilder withPhone(String value) { phone = value; return this; }

    public BookingRequest build() {
        return new BookingRequest(roomId, firstName, lastName, depositPaid,
                new BookingDates(checkIn, checkOut), email, phone);
    }
}
