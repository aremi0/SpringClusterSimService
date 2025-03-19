package com.aremi.documentloadermicroservice.exception;

public class ForcedFailureException extends RuntimeException {
    public ForcedFailureException(String message) {
        super(message);
    }
}
