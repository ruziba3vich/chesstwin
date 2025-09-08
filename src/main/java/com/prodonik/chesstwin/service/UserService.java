package com.prodonik.chesstwin.service;

import com.prodonik.chesstwin.dto.AuthResponse;
import com.prodonik.chesstwin.dto.UserCreateRequest;
import com.prodonik.chesstwin.dto.UserDto;
import com.prodonik.chesstwin.dto.UserLoginRequest;
import com.prodonik.chesstwin.exception.InvalidUserIDException;
import com.prodonik.chesstwin.exception.UserNotFoundException;
import com.prodonik.chesstwin.exception.UserUnAuthorizedException;
import com.prodonik.chesstwin.exception.UsernameAlreadyExistsException;
import com.prodonik.chesstwin.helper.TimeUUIDGenerator;
import com.prodonik.chesstwin.security.JwtUtil;

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

    public AuthResponse createUser(UserCreateRequest request) {
        boolean exists = dsl.fetchExists(
            dsl.selectFrom(USER).where(USER.USERNAME.eq(request.getUsername()))
        );
        if (exists) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        UUID id = TimeUUIDGenerator.generateTimeUUID();

        dsl.insertInto(USER)
            .set(USER.ID, id)
            .set(USER.FULLNAME, request.getFullname())
            .set(USER.USERNAME, request.getUsername())
            .set(USER.AVG_OPENING_ELO, request.getAvgOpeningElo())
            .set(USER.AVG_MIDGAME_ELO, request.getAvgMidgameElo())
            .set(USER.AVG_ENDGAME_ELO, request.getAvgEndgameElo())
            .set(USER.GAMES_COUNT, request.getGamesCount())
            .execute();

        // UserDto dto = UserDto.builder()
        //                         .id(id)
        //                         .fullname(request.getFullname())
        //                         .username(request.getUsername())
        //                         .avgOpeningElo(request.getAvgOpeningElo())
        //                         .avgMidgameElo(request.getAvgMidgameElo())
        //                         .avgEndgameElo(request.getAvgEndgameElo())
        //                         .gamesCount(request.getGamesCount())
        //                         .build();
        // return dto;

        String accessToken = JwtUtil.generateAccessToken(id.toString(), request.getUsername());
        String refreshToken = JwtUtil.generateRefreshToken(id.toString(), request.getUsername());

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse loginUser(UserLoginRequest request) {
        var record = dsl.selectFrom(USER)
            .where(USER.USERNAME.eq(request.getUsername()))
            .fetchOne();
        
        if (record == null) {
            throw new UserNotFoundException(request.getUsername());
        }

        String accessToken = JwtUtil.generateAccessToken(record.getId().toString(), request.getUsername());
        String refreshToken = JwtUtil.generateRefreshToken(record.getId().toString(), request.getUsername());

        return new AuthResponse(accessToken, refreshToken);
    }

    public UserDto getUserByUsername(String username) {
        var record = dsl.selectFrom(USER)
                .where(USER.USERNAME.eq(username))
                .fetchOne();

        if (record == null) {
            throw new UserNotFoundException(username);
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

    public UserDto getMe(String token) {
        String strID = JwtUtil.getUserId(token);
        if (strID.length() == 0) {
            throw new UserUnAuthorizedException();
        }
        UUID id = UUID.fromString(strID);
        var record = dsl.selectFrom(USER)
                        .where(USER.ID.eq(id))
                        .fetchOne();
        if (record == null) {
            throw new InvalidUserIDException();
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
