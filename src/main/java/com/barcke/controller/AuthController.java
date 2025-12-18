package com.barcke.controller;

import com.barcke.common.Result;
import com.barcke.dto.LoginRequest;
import com.barcke.dto.LoginResponse;
import com.barcke.dto.RegisterRequest;
import com.barcke.dto.UserResponse;
import com.barcke.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className AuthController
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 认证控制器
 **/
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse userResponse = userService.register(request);
        return Result.success("注册成功", userResponse);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = userService.login(request.getUsername(), request.getPassword());
        return Result.success("登录成功", loginResponse);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("登出成功");
    }
}

