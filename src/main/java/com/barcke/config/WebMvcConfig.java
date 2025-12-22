package com.barcke.config;

import com.barcke.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className WebMvcConfig
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: Web MVC配置类，注册拦截器
 **/
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    /**
     * 配置 CORS 跨域支持
     * Spring 的 CORS 配置会自动处理所有请求，包括 OPTIONS 预检请求和异常响应
     * 这是最可靠的方式，比拦截器更早执行
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 使用 allowedOriginPatterns 支持所有域名（Spring 5.3+）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")  // 允许的 HTTP 方法
                .allowedHeaders("*")          // 允许所有请求头
                .exposedHeaders("*")          // 暴露所有响应头
                .allowCredentials(true)       // 允许携带凭证
                .maxAge(3600);               // 预检请求缓存时间（秒）
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/h2-console/**",
                        "/error",
                        "/favicon.ico"
                );
    }
}

