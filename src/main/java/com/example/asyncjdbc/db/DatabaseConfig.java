package com.example.asyncjdbc.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhangyu
 * @created 2021-06-16 16:56
 */
@Component
public class DatabaseConfig {
    @Value("${db.url}")
    String url;
    @Value("${db.name}")
    String name;
    @Value("${db.passwd}")
    String passwd;
}
