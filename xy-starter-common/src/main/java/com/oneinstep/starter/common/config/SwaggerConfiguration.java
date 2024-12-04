package com.oneinstep.starter.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * swagger config
 */
@Configuration
@EnableKnife4j
public class SwaggerConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public GroupedOpenApi baseRestApi() {
        return GroupedOpenApi.builder()
                .group(applicationName)
                .packagesToScan("com.oneinstep").build();
    }

}