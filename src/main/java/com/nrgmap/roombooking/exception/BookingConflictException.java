package com.nrgmap.roombooking.exception;

public class BookingConflictException extends RuntimeException {

    public BookingConflictException(Long roomId) {
        super("Booking conflicts with an existing reservation in room with ID: " + roomId);
    }

}
