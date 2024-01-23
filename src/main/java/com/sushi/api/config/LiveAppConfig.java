package com.sushi.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.sushi.api.library.aws.parameterstore.AwsParameterStoreService;
import com.sushi.api.library.aws.parameterstore.DatabaseSecrets;
import com.sushi.api.library.aws.parameterstore.StripeSecrets;
import com.sushi.api.library.aws.secretsmanager.XApiKey;
import com.sushi.api.utils.ObjectUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = {"prod"})
@Configuration
public class LiveAppConfig {

    @Value("${spring.datasource.name}")
    private String                   databaseName;

    @Autowired
    private AwsParameterStoreService awsParameterStoreService;

    /* ================== datasource =============== */
    @Bean
    public HikariDataSource dataSource() {
        log.info("Configuring dataSource...");

        DatabaseSecrets databaseSecrets = awsParameterStoreService.getDatabaseSecrets();

        log.info("databaseSecrets={}", ObjectUtils.toJson(databaseSecrets));
        // jdbc:postgresql://localhost:5432/learnmymath_api_db
        int port = 5432;
        String host = databaseSecrets.getHost();
        String username = databaseSecrets.getUsername();
        String password = databaseSecrets.getPassword();
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");

        HikariDataSource hds = new HikariDataSource(config);
        hds.setMaximumPoolSize(30);
        hds.setMinimumIdle(5);
        hds.setMaxLifetime(1800000);
        hds.setConnectionTimeout(30000);
        hds.setIdleTimeout(600000);
        // 45 seconds
        hds.setLeakDetectionThreshold(45000);

        log.info("DataSource configured!");

        return hds;
    }

    @Bean(name = "stripeSecrets")
    public StripeSecrets stripeSecrets() {
        return awsParameterStoreService.getStripeSecrets();
    }

    @Bean(name = "xApiKey")
    public XApiKey xApiKeySecrets() {
        return XApiKey.builder()
                .webXApiKey("uuid-12kjsd-jsoi322-lskll")
                .build();
    }

}
