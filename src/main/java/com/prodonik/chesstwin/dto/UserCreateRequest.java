package com.prodonik.chesstwin.dto;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String fullname;
    private String username;
    private int avgOpeningElo;
    private int avgMidgameElo;
    private int avgEndgameElo;
    private int gamesCount;
}
