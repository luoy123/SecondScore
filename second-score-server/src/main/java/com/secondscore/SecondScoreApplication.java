package com.secondscore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.secondscore.modules.**.mapper")
public class SecondScoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondScoreApplication.class, args);
    }
}
