package com.prodonik.chesstwin.exception;

public class UserUnAuthorizedException extends RuntimeException {
    public UserUnAuthorizedException() {
        super("Unauthorized");
    }
}
