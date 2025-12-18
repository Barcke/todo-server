package com.barcke.service;

import com.barcke.dto.LoginResponse;
import com.barcke.dto.RegisterRequest;
import com.barcke.dto.UpdateProfileRequest;
import com.barcke.dto.UserResponse;
import com.barcke.pojo.UserInfo;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className UserService
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 用户服务接口
 **/
public interface UserService {

    /**
     * 用户注册
     */
    UserResponse register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(String username, String password);

    /**
     * 获取当前用户信息
     */
    UserResponse getCurrentUser();

    /**
     * 更新用户信息
     */
    UserResponse updateProfile(UpdateProfileRequest request);

    /**
     * 修改密码
     */
    void changePassword(String oldPassword, String newPassword);

    /**
     * 根据ID查询用户
     */
    UserInfo getById(String userId);
}

