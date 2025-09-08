package com.prodonik.chesstwin.dto;

public class UserCreateRequest {
    private String fullname;
    private String username;

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
