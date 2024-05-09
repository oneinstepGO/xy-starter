package com.oneinstep.starter.admin;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {"com.oneinstep.**"})
@MapperScan(basePackages = {"com.oneinstep.**.mapper"})
@EnableTransactionManagement
@Slf4j
@EnableScheduling
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AdminApplication.class);
        app.run(args);
    }

}
