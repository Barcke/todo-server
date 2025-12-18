package cn.barcke.config;

import cn.barcke.interceptor.AuthInterceptor;
import cn.barcke.interceptor.CORSSignatureInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
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

    private final CORSSignatureInterceptor corsSignatureInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //1.配置跨域处理拦截器
        registry.addInterceptor(corsSignatureInterceptor).addPathPatterns("/**");

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/h2-console/**",
                        "/error",
                        "/favicon.ico"
                );
    }
}

