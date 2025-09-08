package com.prodonik.chesstwin.service;

import com.prodonik.chesstwin.dto.UserCreateRequest;
import com.prodonik.chesstwin.dto.UserDto;
import com.prodonik.chesstwin.helper.TimeUUIDGenerator;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.prodonik.chesstwin.jooq.tables.User.USER;

@Service
public class UserService {
    private final DSLContext dsl;

    public UserService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public UserDto createUser(UserCreateRequest request) {
        UUID id = TimeUUIDGenerator.generateTimeUUID();

        dsl.insertInto(USER)
            .set(USER.ID, id)
            .set(USER.FULLNAME, request.getFullname())
            .set(USER.USERNAME, request.getUsername())
            .set(USER.AVG_OPENING_ELO, request.getAvgOpeningElo())
            .set(USER.AVG_MIDGAME_ELO, request.getAvgMidgameElo())
            .set(USER.AVG_ENDGAME_ELO, request.getGamesCount())
            .set(USER.GAMES_COUNT, request.getAvgMidgameElo())
            .execute();

        UserDto dto = UserDto.builder()
                                .id(id)
                                .fullname(request.getFullname())
                                .username(request.getUsername())
                                .avgOpeningElo(request.getAvgOpeningElo())
                                .avgMidgameElo(request.getAvgMidgameElo())
                                .avgEndgameElo(request.getAvgEndgameElo())
                                .gamesCount(request.getGamesCount())
                                .build();
        return dto;
    }

    public UserDto getUserByUsername(String username) {
        var record = dsl.selectFrom(USER)
                .where(USER.USERNAME.eq(username))
                .fetchOne();

        if (record == null) {
            return null;
        }

        return UserDto.builder()
                .id(record.getId())
                .fullname(record.getFullname())
                .username(record.getUsername())
                .avgOpeningElo(record.getAvgOpeningElo())
                .avgMidgameElo(record.getAvgMidgameElo())
                .avgEndgameElo(record.getAvgEndgameElo())
                .gamesCount(record.getGamesCount())
                .build();
    }
}
