package com.secondscore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI secondScoreOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("第二课堂学分统计与分析系统 API")
                        .description("Spring Boot 3 + MyBatis-Plus 后端接口文档")
                        .version("1.0.0"));
    }
}

