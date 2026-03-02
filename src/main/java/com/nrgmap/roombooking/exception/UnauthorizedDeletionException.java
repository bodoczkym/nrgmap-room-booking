package com.nrgmap.roombooking.exception;

public class UnauthorizedDeletionException extends RuntimeException {

    public UnauthorizedDeletionException() {
        super("Email does not match the booking owner");
    }

}
