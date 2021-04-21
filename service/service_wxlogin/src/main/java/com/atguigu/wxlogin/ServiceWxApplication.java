package com.atguigu.wxlogin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.atguigu.wxlogin.mapper")
@ComponentScan("com.atguigu")
@SpringBootApplication
public class ServiceWxApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceWxApplication.class, args);
    }
}
