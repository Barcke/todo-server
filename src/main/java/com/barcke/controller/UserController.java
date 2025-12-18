package com.barcke.controller;

import com.barcke.common.Result;
import com.barcke.dto.ChangePasswordRequest;
import com.barcke.dto.UpdateProfileRequest;
import com.barcke.dto.UserResponse;
import com.barcke.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className UserController
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 用户控制器
 **/
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<UserResponse> getProfile() {
        UserResponse userResponse = userService.getCurrentUser();
        return Result.success(userResponse);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserResponse userResponse = userService.updateProfile(request);
        return Result.success("更新成功", userResponse);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.getOldPassword(), request.getNewPassword());
        return Result.success("密码修改成功");
    }
}

