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
            .execute();

        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setFullname(request.getFullname());
        dto.setUsername(request.getUsername());
        return dto;
    }
}
