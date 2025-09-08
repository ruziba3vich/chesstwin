package com.prodonik.chesstwin.dto;

import java.util.UUID;

public class UserDto {
    private UUID id;
    private String fullname;
    private String username;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
