package com.barcke;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className Main
 * @date 2025/12/16 19:52
 * @slogan: 源于生活 高于生活
 * @description:
 **/
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class,args);
    }

    @Bean
    public SpringUtil springUtil(){
        return new SpringUtil();
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
