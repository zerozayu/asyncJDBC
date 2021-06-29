package com.example.asyncjdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AsyncJdbcApplication {
    // @Value("${spring.datasource.hikari.maximum-pool-size}")
    // private int connectionPoolSize;

    public static void main(String[] args) {
        SpringApplication.run(AsyncJdbcApplication.class, args);
    }

    //
    // @Bean
    // public Scheduler jdbcScheduler() {
    //     // return Schedulers.fromExecutor(Executors.newFixedThreadPool(20));
    //     return Schedulers.boundedElastic();
    // }
}
