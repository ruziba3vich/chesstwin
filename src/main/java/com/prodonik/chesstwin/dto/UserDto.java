package com.prodonik.chesstwin.dto;

import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    private UUID id;
    private String fullname;
    private String username;
    private int avgOpeningElo;
    private int avgMidgameElo;
    private int avgEndgameElo;
    private int gamesCount;
}
