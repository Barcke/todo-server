package cn.barcke.common;

import cn.barcke.pojo.UserInfo;
import lombok.Data;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className BarckeContext
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 上下文管理类，使用ThreadLocal存储当前登录用户信息
 **/
public class BarckeContext {

    private static final ThreadLocal<ContextUserInfo> CONTEXT = new ThreadLocal<>();

    /**
     * 设置用户信息到上下文
     */
    public static void setUserInfo(UserInfo userInfo) {
        ContextUserInfo contextUserInfo = new ContextUserInfo();
        contextUserInfo.setUserId(userInfo.getUserId());
        contextUserInfo.setUsername(userInfo.getUsername());
        contextUserInfo.setNickname(userInfo.getNickname());
        contextUserInfo.setPhone(userInfo.getPhone());
        contextUserInfo.setEmail(userInfo.getEmail());
        CONTEXT.set(contextUserInfo);
    }

    /**
     * 获取当前用户ID
     */
    public static String getUserId() {
        ContextUserInfo userInfo = CONTEXT.get();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        ContextUserInfo userInfo = CONTEXT.get();
        return userInfo != null ? userInfo.getUsername() : null;
    }

    /**
     * 获取当前用户昵称
     */
    public static String getNickname() {
        ContextUserInfo userInfo = CONTEXT.get();
        return userInfo != null ? userInfo.getNickname() : null;
    }

    /**
     * 获取当前用户手机号
     */
    public static String getPhone() {
        ContextUserInfo userInfo = CONTEXT.get();
        return userInfo != null ? userInfo.getPhone() : null;
    }

    /**
     * 获取当前用户邮箱
     */
    public static String getEmail() {
        ContextUserInfo userInfo = CONTEXT.get();
        return userInfo != null ? userInfo.getEmail() : null;
    }

    /**
     * 获取完整的上下文用户信息
     */
    public static ContextUserInfo getUserInfo() {
        return CONTEXT.get();
    }

    /**
     * 清理上下文
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * 上下文用户信息
     */
    @Data
    public static class ContextUserInfo {
        private String userId;
        private String username;
        private String nickname;
        private String phone;
        private String email;
    }
}

