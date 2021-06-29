package com.example.asyncjdbc.db;

import org.davidmoten.rx.jdbc.ConnectionProvider;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.pool.DatabaseType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyu
 * @created 2021-06-16 17:15
 */
@Service
public class DatabaseInstance {
    DatabaseConfig databaseConfig;

    public DatabaseInstance(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    private Database db;

    @PostConstruct
    public void initDbConnection(){
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // String url = "jdbc:oracle:thin:@//127.0.0.1:1521/XE";
            db = Database.nonBlocking()
                    .connectionProvider(ConnectionProvider.from(databaseConfig.url, databaseConfig.name, databaseConfig.passwd))
                    .maxIdleTime(30, TimeUnit.MINUTES)
                    .healthCheck(DatabaseType.ORACLE)
                    .idleTimeBeforeHealthCheck(5, TimeUnit.SECONDS)
                    .maxPoolSize(100)
                    .build();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public Database getDB(){
        return this.db;
    }
}
