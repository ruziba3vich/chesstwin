package com.prodonik.chesstwin.controller;

import com.prodonik.chesstwin.dto.UserCreateRequest;
import com.prodonik.chesstwin.dto.UserDto;
import com.prodonik.chesstwin.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{username}")
    public UserDto getUserByUsername(@PathVariable String username) {
        UserDto record = userService.getUserByUsername(username);

        if (record == null) {
            throw new RuntimeException("User not found with username: " + username);
        }

        return record;
    }
}
