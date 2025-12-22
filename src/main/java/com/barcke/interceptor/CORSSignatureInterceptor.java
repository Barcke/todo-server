package com.barcke.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * @author yangxianwei
 * @date 2019/8/26 3:17 PM
 */
@Component
public class CORSSignatureInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        // 设置 CORS 响应头
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "content-type, Authorization, TOKEN, Auth-Info, token, BARCKE-TOKEN");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // OPTIONS 预检请求直接返回，不继续执行后续拦截器
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;  // 返回 false 阻止后续拦截器执行
        }

        return true;
    }
}
