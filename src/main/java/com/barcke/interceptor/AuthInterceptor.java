package com.barcke.interceptor;

import com.barcke.common.BarckeContext;
import com.barcke.common.CommonException;
import com.barcke.common.ResultEnum;
import com.barcke.pojo.UserInfo;
import com.barcke.service.UserService;
import com.barcke.tool.JwtTool;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className AuthInterceptor
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 认证拦截器
 **/
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTool jwtTool;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    /**
     * 白名单路径
     */
    @Value("${auth.whiteList}")
    private List<String> WHITELIST;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是HandlerMethod，直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 如果不是cn.barcke.controller包下的controller，直接放行
        if (!handlerMethod.getMethod().getDeclaringClass().getName().contains("com.barcke.controller")) {
            return true;
        }

        // 获取请求路径（去掉context-path）
        String requestPath = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }

        // 检查白名单
        if (isWhitelist(requestPath)) {
            return true;
        }

        // 从请求头获取TOKEN
        String token = request.getHeader(tokenHeader);
        if (StrUtil.isBlank(token)) {
            log.error("token is empty,请求失败");
            throw new CommonException(ResultEnum.TOKEN_ERROR);
        }

        // 从token中获取userId
        String userId = jwtTool.getUserIdFromToken(token);
        if (StrUtil.isBlank(userId)) {
            log.error("token is empty,请求失败");
            throw new CommonException(ResultEnum.TOKEN_ERROR);
        }

        // 查询用户信息
        UserInfo user = userService.getById(userId);

        // 验证token有效性
        if (!jwtTool.validateToken(token, user)) {
            log.error("token验证失败,请求失败");
            throw new CommonException(ResultEnum.TOKEN_ERROR);
        }

        // 检查用户是否被冻结
        if (BooleanUtil.isTrue(user.getDelFlag())) {
            throw CommonException.toast("用户已被冻结");
        }

        // 设置上下文信息
        BarckeContext.setUserInfo(user);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束后清理上下文
        BarckeContext.clear();
    }

    /**
     * 检查请求路径是否在白名单中
     */
    private boolean isWhitelist(String requestPath) {
        return WHITELIST.stream().anyMatch(path -> requestPath.equals(path) || requestPath.startsWith(path));
    }
}

