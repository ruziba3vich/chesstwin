package com.prodonik.chesstwin.exception;

public class UserUnAuthorizedException extends RuntimeException {
    public UserUnAuthorizedException(String username) {
        super("User not found with username: " + username);
    }
}
