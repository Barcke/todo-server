package cn.barcke.service.impl;

import cn.barcke.common.BarckeContext;
import cn.barcke.common.CommonException;
import cn.barcke.common.ResultEnum;
import cn.barcke.dao.UserRepository;
import cn.barcke.dto.LoginResponse;
import cn.barcke.dto.RegisterRequest;
import cn.barcke.dto.UpdateProfileRequest;
import cn.barcke.dto.UserResponse;
import cn.barcke.pojo.UserInfo;
import cn.barcke.service.UserService;
import cn.barcke.tool.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className UserServiceImpl
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 用户服务实现类
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTool jwtTool;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CommonException(ResultEnum.USERNAME_EXISTS);
        }

        // 创建新用户
        UserInfo userInfo = UserInfo.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .delFlag(false)
                .build();

        userInfo = userRepository.save(userInfo);

        // 转换为响应DTO
        return UserResponse.builder()
                .id(userInfo.getUserId())
                .username(userInfo.getUsername())
                .nickname(userInfo.getNickname())
                .email(userInfo.getEmail())
                .phone(userInfo.getPhone())
                .createdAt(userInfo.getCreatedAt())
                .build();
    }

    @Override
    public LoginResponse login(String username, String password) {
        // 查询用户
        Optional<UserInfo> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new CommonException(ResultEnum.PASSWORD_ERROR);
        }

        UserInfo userInfo = userOptional.get();

        // 验证密码
        if (!passwordEncoder.matches(password, userInfo.getPassword())) {
            throw new CommonException(ResultEnum.PASSWORD_ERROR);
        }

        // 检查用户是否被冻结
        if (Boolean.TRUE.equals(userInfo.getDelFlag())) {
            throw CommonException.toast("用户已被冻结");
        }

        // 生成JWT Token
        String token = jwtTool.generateToken(userInfo);

        // 构建用户响应
        UserResponse userResponse = UserResponse.builder()
                .id(userInfo.getUserId())
                .username(userInfo.getUsername())
                .nickname(userInfo.getNickname())
                .email(userInfo.getEmail())
                .phone(userInfo.getPhone())
                .createdAt(userInfo.getCreatedAt())
                .build();

        return LoginResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }

    @Override
    public UserResponse getCurrentUser() {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw new CommonException(ResultEnum.UNAUTHORIZED);
        }

        UserInfo userInfo = getById(userId);
        return UserResponse.builder()
                .id(userInfo.getUserId())
                .username(userInfo.getUsername())
                .nickname(userInfo.getNickname())
                .email(userInfo.getEmail())
                .phone(userInfo.getPhone())
                .createdAt(userInfo.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw new CommonException(ResultEnum.UNAUTHORIZED);
        }

        UserInfo userInfo = getById(userId);

        // 更新字段（只更新非空字段）
        if (request.getNickname() != null) {
            userInfo.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            userInfo.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            userInfo.setPhone(request.getPhone());
        }

        userInfo = userRepository.save(userInfo);

        return UserResponse.builder()
                .id(userInfo.getUserId())
                .username(userInfo.getUsername())
                .nickname(userInfo.getNickname())
                .email(userInfo.getEmail())
                .phone(userInfo.getPhone())
                .createdAt(userInfo.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        String userId = BarckeContext.getUserId();
        if (userId == null) {
            throw new CommonException(ResultEnum.UNAUTHORIZED);
        }

        UserInfo userInfo = getById(userId);

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, userInfo.getPassword())) {
            throw new CommonException(ResultEnum.PASSWORD_ERROR);
        }

        // 更新密码
        userInfo.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userInfo);
    }

    @Override
    public UserInfo getById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ResultEnum.USER_NOT_FOUND));
    }
}

