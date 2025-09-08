package com.prodonik.chesstwin.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserCreateRequest {
    private String fullname;
    private String username;
    private int avgOpeningElo;
    private int avgMidgameElo;
    private int avgEndgameElo;
    private int gamesCount;
}
