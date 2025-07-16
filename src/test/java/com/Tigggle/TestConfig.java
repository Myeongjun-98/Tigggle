package com.Tigggle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Configuration
public class TestConfig {
    @Bean
    public Clock clock(){
        return Clock.fixed(
                LocalDateTime.of(2025, 7, 18, 10, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("Asia/Seoul")
        );
    }
}
