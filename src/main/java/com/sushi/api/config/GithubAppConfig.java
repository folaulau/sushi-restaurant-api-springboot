package com.sushi.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.sushi.api.library.aws.parameterstore.AwsParameterStoreService;
import com.sushi.api.library.aws.parameterstore.StripeSecrets;
import com.sushi.api.library.aws.secretsmanager.XApiKey;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile({"github"})
@Configuration
public class GithubAppConfig {

    @Value("${aws.deploy.region:us-west-2}")
    private String                   targetRegion;

    @Value("${database.username}")
    private String                   databaseUsername;

    @Value("${database.password}")
    private String                   databasePassword;

    @Value("${database.url}")
    private String                   databaseUrl;

    @Value("${spring.datasource.name}")
    private String                   databaseName;

    @Value("${aws.access.key}")
    private String                   awsAccessKey;

    @Value("${aws.secret.key}")
    private String                   awsSecretKey;

    @Value("${firebase.web.api.key}")
    private String                   firebaseWebApiKey;

    @Autowired
    private AwsParameterStoreService awsParameterStoreService;

    private Regions getTargetRegion() {
        return Regions.fromName(targetRegion);
    }

    @Bean
    public AmazonS3 amazonS3(AWSCredentialsProvider amazonAWSCredentialsProvider) {
        return AmazonS3ClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider).withRegion(getTargetRegion()).build();
    }

    @Bean
    public AmazonSimpleEmailService amazonSES(AWSCredentialsProvider amazonAWSCredentialsProvider) {
        return AmazonSimpleEmailServiceClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider).withRegion(getTargetRegion()).build();
    }

    @Bean(name = "xApiKey")
    public XApiKey xApiKeySecrets() {
        return null;
    }

    @Bean(name = "stripeSecrets")
    public StripeSecrets stripeSecrets() {
        return awsParameterStoreService.getStripeSecrets();
    }

    /* ================== datasource =============== */
    @DependsOn("amazonAWSCredentialsProvider")
    @Bean
    public HikariDataSource dataSource() {
        log.info("Configuring dataSource...");

        log.info("dbUrl={}", databaseUrl);
        log.info("dbUsername={}", databaseUsername);
        log.info("dbPassword={}", databasePassword);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(databaseUsername);
        config.setPassword(databasePassword);
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
}
