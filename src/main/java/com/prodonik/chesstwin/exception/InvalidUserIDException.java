package com.prodonik.chesstwin.exception;

public class InvalidUserIDException extends RuntimeException {
    public InvalidUserIDException() {
        super("Invalid user_id");
    }
}
