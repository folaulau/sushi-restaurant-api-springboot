package com.sushi.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.sendgrid.SendGrid;
import com.sushi.api.library.aws.secretsmanager.AwsSecretsManagerService;
import com.sushi.api.library.aws.secretsmanager.DatabaseSecrets;
import com.sushi.api.library.aws.secretsmanager.SMTPSecrets;
import com.sushi.api.library.aws.secretsmanager.StripeSecrets;
import com.sushi.api.library.aws.secretsmanager.TwilioSecrets;
import com.sushi.api.library.aws.secretsmanager.XApiKey;
import com.sushi.api.utils.ObjectUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = {"dev","prod"})
@Configuration
public class LiveAppConfig {

  @Value("${aws.deploy.region:us-west-2}")
  private String targetRegion;

  @Value("${spring.datasource.name}")
  private String databaseName;

  @Autowired
  private AwsSecretsManagerService awsSecretsManagerService;

  /* ================== datasource =============== */
  @Bean
  public HikariDataSource dataSource() {
    log.info("Configuring dataSource...");

    DatabaseSecrets databaseSecrets = awsSecretsManagerService.getDbSecret();

    log.info("databaseSecrets={}", ObjectUtils.toJson(databaseSecrets));
    // jdbc:postgresql://localhost:5432/learnmymath_api_db
    int port = 5432;
    String host = databaseSecrets.getHost();
    String username = databaseSecrets.getUsername();
    String password = databaseSecrets.getPassword();
    String url = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setUsername(databaseSecrets.getUsername());
    config.setPassword(databaseSecrets.getPassword());
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
    return awsSecretsManagerService.getStripeSecrets();
  }

  @Bean(name = "twilioSecrets")
  public TwilioSecrets twilioSecrets() {
    return awsSecretsManagerService.getTwilioSecrets();
  }

  @Bean(name = "queue")
  public String queue(@Value("${queue}") String queue) {
    return queue;
  }

  @Bean(name = "xApiKey")
  public XApiKey xApiKeySecrets() {
    return awsSecretsManagerService.getXApiKeys();
  }

  @Bean
  public SendGrid sendGrid() {
    SMTPSecrets sMTPSecrets = awsSecretsManagerService.getSMTPSecrets();
    SendGrid sendGrid = new SendGrid(sMTPSecrets.getPassword());
    return sendGrid;
  }

  /**
   * AWS
   */

  private Regions getTargetRegion() {
    return Regions.fromName(targetRegion);
  }

  @Bean
  public AWSCredentialsProvider amazonAWSCredentialsProvider() {
    return DefaultAWSCredentialsProviderChain.getInstance();
  }

  @Bean
  public AmazonS3 amazonS3() {
    return AmazonS3ClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider())
        .withRegion(getTargetRegion()).build();
  }

  @Bean
  public AmazonSimpleEmailService amazonSES() {
    return AmazonSimpleEmailServiceClientBuilder.standard()
        .withCredentials(amazonAWSCredentialsProvider()).withRegion(getTargetRegion()).build();
  }

  @Bean
  public AWSSecretsManager awsSecretsManager(AWSCredentialsProvider aWSCredentialsProvider) {
    String endpoint = "secretsmanager." + getTargetRegion().getName() + ".amazonaws.com";
    AwsClientBuilder.EndpointConfiguration config =
        new AwsClientBuilder.EndpointConfiguration(endpoint, getTargetRegion().getName());
    AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
    clientBuilder.setEndpointConfiguration(config);
    clientBuilder.setCredentials(aWSCredentialsProvider);
    return clientBuilder.build();
  }

}
