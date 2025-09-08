package com.prodonik.chesstwin.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private UUID id;
    private String fullname;
    private String username;
    private int avgOpeningElo;
    private int avgMidgameElo;
    private int avgEndgameElo;
    private int gamesCount;
}
