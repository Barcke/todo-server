
package cn.barcke.tool;

import cn.barcke.pojo.UserInfo;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
  *                  ,;,,;
  *                ,;;'(    社
  *      __      ,;;' ' \   会
  *   /'  '\'~~'~' \ /'\.)  主
  * ,;(      )    /  |.     义
  * ,;' \    /-.,,(   ) \    码
  *     ) /       ) / )|    农
  *     ||        ||  \)     
  *     (_\       (_\
  *
  * ～～～源于生活 高于生活～～～
  * @ProjectName parent
  * @ClassName JwkTool
  * @Description TODO
  * @Author Barcke
  * @Date 2020/5/22 2:39 下午
  * @Version 1.0
  * @description:
  **/
@Slf4j
@Component
public class JwtTool {

    private static final String CLAIM_KEY_MINI_OPEN_ID = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    
    @Value("${jwt.secret:barcke-secret}")
    private String secret;
    
    @Value("${jwt.expiration:604800}")
    private Long expiration;

    /**
     * 获取签名用到的Key
     */
    private byte[] getSigningKey() {
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 根据负责生成JWT的token
     */
    private String generateToken(Map<String, Object> claims) {
        // 添加过期时间（Hutool JWT 使用秒级时间戳）
        Date expDate = generateExpirationDate();
        claims.put("exp", expDate.getTime() / 1000);
        return JWT.create()
                .addPayloads(claims)
                .setKey(getSigningKey())
                .sign();
    }

    /**
     * 从token中获取JWT中的负载
     */
    private Map<String, Object> getClaimsFromToken(String token) {
        Map<String, Object> claims = null;
        try {
            JWT jwt = JWT.of(token);
            // 验证签名
            if (jwt.setKey(getSigningKey()).verify()) {
                claims = jwt.getPayloads();
            } else {
                log.info("JWT签名验证失败:{}", token);
            }
        } catch (Exception e) {
            log.info("JWT格式验证失败:{}", token);
        }
        return claims;
    }

    /**
     * 生成token的过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 从token中获取登录手机号
     */
    public String getUserIdFromToken(String token) {
        String userId;
        try {
            Map<String, Object> claims = getClaimsFromToken(token);
            if (claims != null) {
                // 从 sub claim 或 subject 中获取用户ID
                Object sub = claims.get(CLAIM_KEY_MINI_OPEN_ID);
                if (sub != null) {
                    userId = sub.toString();
                } else {
                    userId = null;
                }
            } else {
                userId = null;
            }
        } catch (Exception e) {
            userId = null;
        }
        return userId;
    }

    /**
     * 验证token是否还有效
     *
     * @param token       客户端传入的token
     * @param userInfo 从数据库中查询出来的用户信息
     */
    public boolean validateToken(String token, UserInfo userInfo) {
        String userId = getUserIdFromToken(token);
        if (userId == null || userInfo == null || userInfo.getUserId() == null) {
            return false;
        }
        return userId.equals(userInfo.getUserId()) && !isTokenExpired(token);
    }

    /**
     * 判断token是否已经失效
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        if (expiredDate == null) {
            return true;
        }
        return expiredDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Map<String, Object> claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        Object exp = claims.get("exp");
        if (exp == null) {
            return null;
        }
        // exp 可能是 Date、Long、Integer、String 或其它类型
        if (exp instanceof Date) {
            return (Date) exp;
        } else if (exp instanceof Long) {
            return new Date((Long) exp * 1000);
        } else if (exp instanceof Integer) {
            return new Date(((Integer) exp).longValue() * 1000);
        } else if (exp instanceof String) {
            try {
                long longExp = Long.parseLong((String) exp);
                return new Date(longExp * 1000);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            try {
                long longExp = Long.parseLong(exp.toString());
                return new Date(longExp * 1000);
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * 根据用户信息生成token
     */
    public String generateToken(UserInfo userInfo) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_MINI_OPEN_ID, userInfo.getUserId());
        claims.put(CLAIM_KEY_CREATED, new Date());
        // 添加过期时间（Hutool JWT 使用秒级时间戳）
        Date expDate = generateExpirationDate();
        claims.put("exp", expDate.getTime() / 1000);
        return JWT.create()
                .addPayloads(claims)
                .setKey(getSigningKey())
                .sign();
    }

    /**
     * 当原来的token没过期时是可以刷新的
     *
     * @param oldToken 带tokenHead的token
     */
    public String refreshHeadToken(String oldToken) {
        if(StrUtil.isEmpty(oldToken)){
            return null;
        }
        String token = oldToken;
        if(StrUtil.isEmpty(token)){
            return null;
        }
        //token校验不通过
        Map<String, Object> claims = getClaimsFromToken(token);
        if(claims==null){
            return null;
        }
        //如果token已经过期，不支持刷新
        if(isTokenExpired(token)){
            return null;
        }
        //如果token在30分钟之内刚刷新过，返回原token
        if(tokenRefreshJustBefore(token,30*60)){
            return token;
        }else{
            claims.put(CLAIM_KEY_CREATED, new Date());
            return generateToken(claims);
        }
    }

    /**
     * 判断token在指定时间内是否刚刚刷新过
     * @param token 原token
     * @param time 指定时间（秒）
     */
    private boolean tokenRefreshJustBefore(String token, int time) {
        Map<String, Object> claims = getClaimsFromToken(token);
        if (claims == null) {
            return false;
        }
        Object createdObj = claims.get(CLAIM_KEY_CREATED);
        if (createdObj == null) {
            return false;
        }
        Date created;
        if (createdObj instanceof Date) {
            created = (Date) createdObj;
        } else if (createdObj instanceof Long) {
            created = new Date((Long) createdObj);
        } else if (createdObj instanceof Integer) {
            created = new Date(((Integer) createdObj).longValue());
        } else {
            return false;
        }
        Date refreshDate = new Date();
        //刷新时间在创建时间的指定时间内
        if(refreshDate.after(created)&&refreshDate.before(DateUtil.offsetSecond(created,time))){
            return true;
        }
        return false;
    }

}
