package com.prodonik.chesstwin.controller;

import com.prodonik.chesstwin.dto.AuthResponse;
import com.prodonik.chesstwin.dto.UserCreateRequest;
import com.prodonik.chesstwin.dto.UserDto;
import com.prodonik.chesstwin.dto.UserLoginRequest;
import com.prodonik.chesstwin.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public AuthResponse createUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{username}")
    public UserDto getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/me")
    public UserDto getMe(HttpServletRequest request) {
        String userID = (String) request.getAttribute("userId");
        return userService.getUserByUsername(userID);
    }

    @PostMapping("/login")
    public AuthResponse loginUser(@RequestBody UserLoginRequest request) {
        return userService.loginUser(request);
    }
    
}
